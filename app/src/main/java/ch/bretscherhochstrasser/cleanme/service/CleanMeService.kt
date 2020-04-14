package ch.bretscherhochstrasser.cleanme.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import ch.bretscherhochstrasser.cleanme.AppSettings
import ch.bretscherhochstrasser.cleanme.deviceusage.DeviceUsageObserver
import ch.bretscherhochstrasser.cleanme.deviceusage.IDeviceUsageStats
import ch.bretscherhochstrasser.cleanme.helper.NotificationHelper
import ch.bretscherhochstrasser.cleanme.overlay.ParticleOverlayManager

/**
 * Service to handle to collect the device usage time and trigger notifications.
 * It needs to be a foreground service to be able to track device state continuously.
 */
class CleanMeService : Service() {

    companion object {
        const val ACTION_START_OBSERVE_DEVICE_STATE = "ACTION_START_OBSERVE_DEVICE_STATE"
        const val ACTION_STOP_OBSERVE_DEVICE_STATE = "ACTION_STOP_OBSERVE_DEVICE_STATE"
        const val ACTION_RESET_USAGE_STATS = "ACTION_RESET_DEVICE_STATS"
    }

    private val observer = DeviceUsageObserver(this)
    private val overlayManager = ParticleOverlayManager(this)
    private val notificationHelper = NotificationHelper(this)
    private val settings = AppSettings(this)

    override fun onCreate() {
        super.onCreate()
        notificationHelper.createNotificationChannel()
        observer.onDeviceUsageUpdateListener = ::onDeviceUsageUpdate
    }

    override fun onBind(intent: Intent): IBinder {
        throw UnsupportedOperationException("This service cannot be bound.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_OBSERVE_DEVICE_STATE -> {
                if (!observer.observing) {
                    startForeground(
                        NotificationHelper.NOTIFICATION_ID,
                        notificationHelper.createNotification(observer.deviceUsageStats)
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
            ACTION_RESET_USAGE_STATS -> observer.resetDeviceUsageStats()
        }
        return START_NOT_STICKY
    }

    private fun onDeviceUsageUpdate(deviceUsageStats: IDeviceUsageStats) {
        notificationHelper.updateNotification(deviceUsageStats)
        if (settings.overlayEnabled) {
            overlayManager.showOverlay()
            overlayManager.update(deviceUsageStats.deviceUseDuration, settings.cleanIntervalMillis)
        } else {
            overlayManager.hideOverlay()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        overlayManager.hideOverlay()
    }

}