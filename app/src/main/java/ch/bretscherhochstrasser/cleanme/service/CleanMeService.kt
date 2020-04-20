package ch.bretscherhochstrasser.cleanme.service

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.view.WindowManager
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import ch.bretscherhochstrasser.cleanme.annotation.ApplicationScope
import ch.bretscherhochstrasser.cleanme.deviceusage.DeviceUsageObserver
import ch.bretscherhochstrasser.cleanme.deviceusage.DeviceUsageStats
import ch.bretscherhochstrasser.cleanme.deviceusage.DeviceUsageStatsManager
import ch.bretscherhochstrasser.cleanme.helper.NotificationHelper
import ch.bretscherhochstrasser.cleanme.helper.valueNN
import ch.bretscherhochstrasser.cleanme.overlay.ParticleOverlayManager
import ch.bretscherhochstrasser.cleanme.service.CleanMeService.Companion.refresh
import ch.bretscherhochstrasser.cleanme.service.CleanMeService.Companion.start
import ch.bretscherhochstrasser.cleanme.service.CleanMeService.Companion.stop
import ch.bretscherhochstrasser.cleanme.settings.AppSettings
import toothpick.ktp.KTP
import toothpick.ktp.binding.bind
import toothpick.ktp.binding.module
import toothpick.ktp.delegate.inject

/**
 * Service to handle to collect the device usage time and trigger notifications.
 * It needs to be a foreground service to be able to track device state continuously.
 * Use the utility functions [start], [stop], [refresh] to invoke service commands.
 */
class CleanMeService : LifecycleService() {

    companion object {
        private const val ACTION_START = "ACTION_START"
        private const val ACTION_STOP = "ACTION_STOP"
        private const val ACTION_REFRESH = "ACTION_REFRESH"

        fun start(context: Context) {
            startWithAction(ACTION_START, context)
        }

        fun stop(context: Context) {
            startWithAction(ACTION_STOP, context)
        }

        fun refresh(context: Context) {
            startWithAction(ACTION_REFRESH, context)
        }

        private fun startWithAction(action: String, context: Context) {
            val intent = Intent(context, CleanMeService::class.java)
            intent.action = action
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

    private val serviceState: ServiceState by inject()
    private val deviceUsageStatsManager: DeviceUsageStatsManager by inject()
    private val appSettings: AppSettings by inject()
    private val observer: DeviceUsageObserver by inject()
    private val overlayManager: ParticleOverlayManager by inject()
    private val notificationHelper: NotificationHelper by inject()
    private val reminderManager: ReminderManager by inject()

    override fun onCreate() {
        super.onCreate()
        KTP.openScopes(ApplicationScope::class.java, this)
            .installModules(module {
                bind<Context>().toInstance(this@CleanMeService)
                bind<WindowManager>().toProviderInstance {
                    this@CleanMeService.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                }
            })
            .inject(this)

        notificationHelper.createNotificationChannels()
        deviceUsageStatsManager.deviceUsageStats.observe(this, Observer {
            onDeviceUsageUpdate(it)
        })
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        throw UnsupportedOperationException("This service cannot be bound.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        when (intent?.action) {
            ACTION_START -> {
                if (!serviceState.observingDeviceUsage.valueNN) {
                    startForeground(
                        NotificationHelper.NOTIFICATION_ID_SERVICE,
                        notificationHelper.createServiceNotification(deviceUsageStatsManager.deviceUsageStats.valueNN)
                    )
                    serviceState.observingDeviceUsage.value = true
                    observer.startObserveDeviceState()
                }
            }
            ACTION_STOP -> {
                if (serviceState.observingDeviceUsage.valueNN) {
                    observer.stopObserveDeviceState()
                    serviceState.observingDeviceUsage.value = false
                    stopForeground(true)
                    stopSelf()
                }
            }
            ACTION_REFRESH -> refreshOverlay(deviceUsageStatsManager.deviceUsageStats.valueNN)
        }
        return START_NOT_STICKY
    }

    private fun onDeviceUsageUpdate(deviceUsageStats: DeviceUsageStats) {
        // only handle device usage stats if currently in observing state
        if (serviceState.observingDeviceUsage.valueNN) {
            notificationHelper.updateServiceNotification(deviceUsageStats)
            reminderManager.showReminderIfRequired(deviceUsageStats)
            refreshOverlay(deviceUsageStats)
        }
    }

    private fun refreshOverlay(deviceUsageStats: DeviceUsageStats) {
        if (appSettings.overlayEnabled && serviceState.observingDeviceUsage.valueNN) {
            overlayManager.showOverlay()
            overlayManager.update(
                deviceUsageStats.deviceUseDuration,
                appSettings.cleanInterval.durationMillis
            )
        } else {
            overlayManager.hideOverlay()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        overlayManager.hideOverlay()
    }

}