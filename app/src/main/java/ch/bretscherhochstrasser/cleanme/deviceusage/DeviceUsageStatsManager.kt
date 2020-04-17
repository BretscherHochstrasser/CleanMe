package ch.bretscherhochstrasser.cleanme.deviceusage

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.bretscherhochstrasser.cleanme.annotation.AppContext
import timber.log.Timber
import toothpick.InjectConstructor
import javax.inject.Singleton

/**
 * Manager for the [DeviceUsageStats]. Handles persistence of stat data and provides
 * [LiveData] to observe updates.
 */
@Singleton
@InjectConstructor
class DeviceUsageStatsManager(@AppContext private val context: Context) {

    companion object {
        private const val PREF_NAME = "device_usage_stats"
        private const val PREF_SCREEN_ON_COUNT = "screen_on_count"
        private const val PREF_DEVICE_USE_DURATION = "device_use_duration"
    }

    val deviceUsageStats: LiveData<DeviceUsageStats>
        get() = mutableDeviceUsageStats

    private val mutableDeviceUsageStats =
        MutableLiveData(DeviceUsageStats(screenOnCount, deviceUseDuration))

    private val usageStatPrefs: SharedPreferences
        get() {
            return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }

    private var screenOnCount: Int
        get() = usageStatPrefs.getInt(PREF_SCREEN_ON_COUNT, 0)
        set(value) {
            usageStatPrefs.edit().putInt(PREF_SCREEN_ON_COUNT, value).apply()
        }

    private var deviceUseDuration: Long
        get() = usageStatPrefs.getLong(PREF_DEVICE_USE_DURATION, 0)
        set(value) {
            usageStatPrefs.edit().putLong(PREF_DEVICE_USE_DURATION, value).apply()
        }

    fun resetStats() {
        screenOnCount = 0
        deviceUseDuration = 0
        updateUsageStats()
    }

    fun addDeviceUseDuration(additionalDuration: Long) {
        deviceUseDuration += additionalDuration
        updateUsageStats()
    }

    fun incrementScreenOnCount() {
        screenOnCount++
        updateUsageStats()
    }

    private fun updateUsageStats() {
        Timber.d("Updated stats: %s", deviceUsageStats.toString())
        mutableDeviceUsageStats.value = DeviceUsageStats(screenOnCount, deviceUseDuration)
    }

    override fun toString(): String {
        return String.format(
            "DeviceUsageStats[screenOnCount=%d, deviceUseDuration=%dms]",
            screenOnCount,
            deviceUseDuration
        )
    }

}