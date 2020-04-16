package ch.bretscherhochstrasser.cleanme.service

import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import ch.bretscherhochstrasser.cleanme.appSettings
import ch.bretscherhochstrasser.cleanme.deviceUsageStatsManager
import ch.bretscherhochstrasser.cleanme.deviceusage.DeviceUsageObserver
import ch.bretscherhochstrasser.cleanme.deviceusage.DeviceUsageStats
import ch.bretscherhochstrasser.cleanme.helper.NotificationHelper
import ch.bretscherhochstrasser.cleanme.helper.valueNN
import ch.bretscherhochstrasser.cleanme.overlay.ParticleOverlayManager

/**
 * Service to handle to collect the device usage time and trigger notifications.
 * It needs to be a foreground service to be able to track device state continuously.
 */
class CleanMeService : LifecycleService() {

    companion object {
        const val ACTION_START_OBSERVE_DEVICE_STATE = "ACTION_START_OBSERVE_DEVICE_STATE"
        const val ACTION_STOP_OBSERVE_DEVICE_STATE = "ACTION_STOP_OBSERVE_DEVICE_STATE"
        const val ACTION_REFRESH_OVERLAY = "ACTION_REFRESH_OVERLAY"
    }

    private val observer by lazy {
        // we must initialize lazy deviceUsageStatsManager cannot be accessed during context
        // creation time. TODO: might be non-lazy once we have dependency injection
        DeviceUsageObserver(this, deviceUsageStatsManager)
    }
    private val overlayManager = ParticleOverlayManager(this)
    private val notificationHelper = NotificationHelper(this)

    override fun onCreate() {
        super.onCreate()
        notificationHelper.createNotificationChannel()
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
            ACTION_START_OBSERVE_DEVICE_STATE -> {
                if (!observer.observing) {
                    startForeground(
                        NotificationHelper.NOTIFICATION_ID,
                        notificationHelper.createNotification(deviceUsageStatsManager.deviceUsageStats.valueNN)
                    )
                    observer.startObserveDeviceStateState()
                }
            }
            ACTION_STOP_OBSERVE_DEVICE_STATE -> {
                if (observer.observing) {
                    observer.stopObserveDeviceState()
                    stopForeground(true)
                    stopSelf()
                }
            }
            ACTION_REFRESH_OVERLAY -> refreshOverlay(deviceUsageStatsManager.deviceUsageStats.valueNN)
        }
        return START_NOT_STICKY
    }

    private fun onDeviceUsageUpdate(deviceUsageStats: DeviceUsageStats) {
        // only handle device usage stats if currently in observing state
        if (observer.observing) {
            notificationHelper.updateNotification(deviceUsageStats)
            refreshOverlay(deviceUsageStats)
        }
    }

    private fun refreshOverlay(deviceUsageStats: DeviceUsageStats) {
        if (appSettings.overlayEnabled && observer.observing) {
            overlayManager.showOverlay()
            overlayManager.update(
                deviceUsageStats.deviceUseDuration,
                appSettings.cleanIntervalMillis
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