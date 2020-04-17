package ch.bretscherhochstrasser.cleanme.deviceusage

import android.content.Context
import android.os.Build
import android.os.PowerManager
import androidx.lifecycle.Observer
import ch.bretscherhochstrasser.cleanme.helper.RepeatingUpdater
import timber.log.Timber

/**
 * Observes the device usage by listening for screen on/off broadcasts with a [ScreenStateReceiver]
 * and adds the device use time periodically to the [DeviceUsageStatsManager].
 */
class DeviceUsageObserver(
    private val context: Context,
    private val usageStatsManager: DeviceUsageStatsManager
) {

    companion object {
        private const val STATS_UPDATE_INTERVAL = 30000L
    }

    private val statsUpdater =
        RepeatingUpdater(
            ::updateDeviceUsageStats,
            STATS_UPDATE_INTERVAL
        )

    private lateinit var screenStateReceiver: ScreenStateReceiver

    private var lastScreenOnTime: Long = 0

    private var statsResetObserver = Observer<DeviceUsageStats> {
        // if the device use duration has been reset, we reset the last ScreenOnTime
        if (it.deviceUseDuration == 0L) {
            lastScreenOnTime = System.currentTimeMillis()
        }
    }

    fun startObserveDeviceStateState() {
        Timber.i("Start observing device state")
        if (deviceInUse) {
            onDeviceUseStart()
        }
        screenStateReceiver =
            ScreenStateReceiver(
                ::onDeviceUseStart,
                ::onDeviceUseStop
            )
        screenStateReceiver.register(context)
        usageStatsManager.deviceUsageStats.observeForever(statsResetObserver)
    }

    fun stopObserveDeviceState() {
        Timber.i("Stop observing device state")
        usageStatsManager.deviceUsageStats.removeObserver(statsResetObserver)
        screenStateReceiver.unregister(context)
        if (deviceInUse) {
            onDeviceUseStop()
        }
    }

    private fun onDeviceUseStart() {
        Timber.d("Detected device usage start")
        usageStatsManager.incrementScreenOnCount()
        lastScreenOnTime = System.currentTimeMillis()
        statsUpdater.start()
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
        usageStatsManager.addDeviceUseDuration(additionalScreenOnDuration)
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