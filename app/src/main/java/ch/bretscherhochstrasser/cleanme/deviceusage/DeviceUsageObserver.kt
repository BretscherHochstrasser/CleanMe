package ch.bretscherhochstrasser.cleanme.deviceusage

import android.content.Context
import android.os.Build
import android.os.PowerManager
import ch.bretscherhochstrasser.cleanme.helper.RepeatingUpdater
import timber.log.Timber

/**
 * Observes the device usage.
 * The gathered device usage statistics is accessible through [deviceUsageStats] and is actively
 * propagated on update with the [onDeviceUsageUpdateListener].
 */
class DeviceUsageObserver(private val context: Context) {

    companion object {
        private const val STATS_UPDATE_INTERVAL = 30000L
    }

    val deviceUsageStats: IDeviceUsageStats
        get() = deviceStats
    private val deviceStats = DeviceUsageStatsImpl(context)

    private val statsUpdater =
        RepeatingUpdater(
            ::updateDeviceUsageStats,
            STATS_UPDATE_INTERVAL
        )

    var observing: Boolean = false
        private set
    private lateinit var screenStateReceiver: ScreenStateReceiver
    private var lastScreenOnTime: Long = 0

    var onDeviceUsageUpdateListener: ((IDeviceUsageStats) -> Unit)? = null

    fun startObserveDeviceStateState() {
        Timber.i("Start observing device state")
        observing = true
        if (deviceInUse) {
            onDeviceUseStart()
        }
        screenStateReceiver =
            ScreenStateReceiver(
                ::onDeviceUseStart,
                ::onDeviceUseStop
            )
        screenStateReceiver.register(context)
    }

    fun stopObserveDeviceState() {
        Timber.i("Stop observing device state")
        screenStateReceiver.unregister(context)
        if (deviceInUse) {
            onDeviceUseStop()
        }
    }

    fun resetDeviceUsageStats() {
        Timber.i("Resetting device usage stats")
        deviceStats.reset()
        if (observing) {
            lastScreenOnTime = System.currentTimeMillis()
            onDeviceUsageUpdateListener?.invoke(deviceStats)
        }
    }

    private fun onDeviceUseStart() {
        Timber.d("Detected device usage start")
        deviceStats.screenOnCount++
        lastScreenOnTime = System.currentTimeMillis()
        statsUpdater.start()
        onDeviceUsageUpdateListener?.invoke(deviceStats)
    }

    private fun onDeviceUseStop() {
        Timber.d("Detected device usage stop")
        statsUpdater.stop()
        updateDeviceUsageStats()
    }

    private fun updateDeviceUsageStats() {
        val additionalScreenOnDuration = System.currentTimeMillis() - lastScreenOnTime
        lastScreenOnTime = System.currentTimeMillis()
        Timber.d("Additional device use time: %dms", additionalScreenOnDuration)

        deviceStats.deviceUseDuration += additionalScreenOnDuration
        Timber.d("Updated stats: %s", deviceUsageStats.toString())

        onDeviceUsageUpdateListener?.invoke(deviceStats)
    }

    private val deviceInUse: Boolean
        get() {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                powerManager.isInteractive
            } else {
                @Suppress("DEPRECATION")
                powerManager.isScreenOn
            }
        }
}