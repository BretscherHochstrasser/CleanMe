package ch.bretscherhochstrasser.cleanme.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.view.WindowManager
import androidx.lifecycle.LifecycleService
import ch.bretscherhochstrasser.cleanme.annotation.ApplicationScope
import ch.bretscherhochstrasser.cleanme.deviceusage.DeviceUsageObserver
import ch.bretscherhochstrasser.cleanme.deviceusage.DeviceUsageStats
import ch.bretscherhochstrasser.cleanme.deviceusage.DeviceUsageStatsManager
import ch.bretscherhochstrasser.cleanme.helper.valueNN
import ch.bretscherhochstrasser.cleanme.notification.NotificationHelper
import ch.bretscherhochstrasser.cleanme.overlay.ParticleOverlayManager
import ch.bretscherhochstrasser.cleanme.settings.AppSettings
import timber.log.Timber
import toothpick.ktp.KTP
import toothpick.ktp.binding.bind
import toothpick.ktp.binding.module
import toothpick.ktp.delegate.inject

/**
 * Service to handle to collect the device usage time and trigger notifications.
 * It needs to be a foreground service to be able to track device state continuously.
 * Use the [ServiceHelper] to interact with this service and read its state.
 */
class CleanMeService : LifecycleService() {

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }

    private val deviceUsageStatsManager: DeviceUsageStatsManager by inject<DeviceUsageStatsManager>()
    private val appSettings: AppSettings by inject<AppSettings>()
    private val observer: DeviceUsageObserver by inject<DeviceUsageObserver>()
    private val overlayManager: ParticleOverlayManager by inject<ParticleOverlayManager>()
    private val notificationHelper: NotificationHelper by inject<NotificationHelper>()
    private val reminderManager: ReminderManager by inject<ReminderManager>()

    private var observingDeviceUsage = false

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
        deviceUsageStatsManager.deviceUsageStats.observe(this, {
            onDeviceUsageUpdate(it)
        })
        Timber.d("Service created")
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        throw UnsupportedOperationException("This service cannot be bound.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        when (intent?.action) {
            ACTION_START -> startIfNotObserving()
            ACTION_STOP -> stopIfObserving()
        }
        if (intent == null && appSettings.serviceEnabled) {
            Timber.d("STICKY service restart occurred")
            startIfNotObserving()
        }
        return Service.START_STICKY
    }

    private fun startIfNotObserving() {
        if (!observingDeviceUsage) {
            startForeground(
                NotificationHelper.NOTIFICATION_ID_SERVICE,
                notificationHelper.createServiceNotification(deviceUsageStatsManager.deviceUsageStats.valueNN)
            )
            observingDeviceUsage = true
            observer.startObserveDeviceState()
        }
    }

    private fun stopIfObserving() {
        if (observingDeviceUsage) {
            observer.stopObserveDeviceState()
            observingDeviceUsage = false
            stopForeground(true)
            stopSelf()
        }
    }

    private fun onDeviceUsageUpdate(deviceUsageStats: DeviceUsageStats) {
        // only handle device usage stats if currently in observing state
        if (observingDeviceUsage) {
            notificationHelper.updateServiceNotification(deviceUsageStats)
            reminderManager.showReminderIfRequired(deviceUsageStats)
            refreshOverlay(deviceUsageStats)
        }
    }

    private fun refreshOverlay(deviceUsageStats: DeviceUsageStats) {
        if (appSettings.overlayEnabled && observingDeviceUsage) {
            overlayManager.showOverlay()
            overlayManager.update(
                deviceUsageStats.deviceUseDuration,
                appSettings.cleanInterval.toMillis()
            )
        } else {
            overlayManager.hideOverlay()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        overlayManager.hideOverlay()
        Timber.d("Service destroyed")
    }

}