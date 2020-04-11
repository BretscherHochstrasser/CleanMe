package ch.bretscherhochstrasser.cleanme

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager

/**
 * Service to handle to collect the device usage time and trigger notifications.
 * It needs to be a foreground service to be able to track device state continuously.
 */
class DeviceUsageService : Service() {

    companion object {
        const val ACTION_START_OBSERVE_DEVICE_STATE = "ACTION_START_OBSERVE_DEVICE_STATE"
        const val ACTION_STOP_OBSERVE_DEVICE_STATE = "ACTION_STOP_OBSERVE_DEVICE_STATE"
        const val ACTION_RESET_USAGE_STATS = "ACTION_RESET_DEVICE_STATS"

        private const val STATS_UPDATE_INTERVAL = 30000L
    }

    private val usageStats = DeviceUsageStats(this)
    private val notificationHelper = NotificationHelper(this)
    private val statsUpdater = RepeatingUpdater(::updateDeviceUsageTime, STATS_UPDATE_INTERVAL)

    private lateinit var screenStateReceiver: ScreenStateReceiver
    private var observing = false
    private var lastScreenOnTime: Long = 0

    override fun onCreate() {
        super.onCreate()
        notificationHelper.createNotificationChannel()
    }

    override fun onBind(intent: Intent): IBinder {
        throw UnsupportedOperationException("This service cannot be bound.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_OBSERVE_DEVICE_STATE -> if (!observing) startObserveDeviceStateState()
            ACTION_STOP_OBSERVE_DEVICE_STATE -> if (observing) stopObserveDeviceState()
            ACTION_RESET_USAGE_STATS -> resetDeviceUsageStats()
        }
        return START_NOT_STICKY
    }

    private fun startObserveDeviceStateState() {
        observing = true
        startForeground(NotificationHelper.NOTIFICATION_ID, notificationHelper.createNotification())
        if (deviceInUse) {
            onDeviceUseStart()
        }
        screenStateReceiver = ScreenStateReceiver(::onDeviceUseStart, ::onDeviceUseStop)
        screenStateReceiver.register(this)
    }

    private fun stopObserveDeviceState() {
        screenStateReceiver.unregister(this)
        if (deviceInUse) {
            onDeviceUseStop()
        }
        stopForeground(true)
        stopSelf()
    }

    private fun resetDeviceUsageStats() {
        usageStats.reset()
        if(observing) {
            lastScreenOnTime = System.currentTimeMillis()
            notificationHelper.updateNotification()
        }
    }

    private fun onDeviceUseStart() {
        usageStats.screenOnCount++
        lastScreenOnTime = System.currentTimeMillis()
        statsUpdater.start()
        notificationHelper.updateNotification()
    }

    private fun onDeviceUseStop() {
        updateDeviceUsageTime()
        statsUpdater.stop()
    }

    private fun updateDeviceUsageTime() {
        val additionalScreenOnDuration = System.currentTimeMillis() - lastScreenOnTime
        usageStats.deviceUseDuration += additionalScreenOnDuration
        lastScreenOnTime = System.currentTimeMillis()
        notificationHelper.updateNotification()
    }

    private val deviceInUse: Boolean
        get() {
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                powerManager.isInteractive
            } else {
                @Suppress("DEPRECATION")
                powerManager.isScreenOn
            }
        }

}