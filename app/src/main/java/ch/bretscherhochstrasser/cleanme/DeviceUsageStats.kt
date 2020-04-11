package ch.bretscherhochstrasser.cleanme

import android.content.Context
import android.content.SharedPreferences

/**
 * Wrapper around shared preferences to handle device usage stats
 */
class DeviceUsageStats(private val context: Context) {

    companion object {
        private const val PREF_NAME = "device_usage_stats"
        private const val PREF_SCREEN_ON_COUNT = "screen_on_count"
        private const val PREF_DEVICE_USE_DURATION = "device_use_duration"
    }

    private val usageStatPrefs: SharedPreferences
        get() {
            return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }

    var screenOnCount: Int
        get() = usageStatPrefs.getInt(PREF_SCREEN_ON_COUNT, 0)
        set(value) {
            usageStatPrefs.edit().putInt(PREF_SCREEN_ON_COUNT, value).apply()
        }

    var deviceUseDuration: Long
        get() = usageStatPrefs.getLong(PREF_DEVICE_USE_DURATION, 0)
        set(value) {
            usageStatPrefs.edit().putLong(PREF_DEVICE_USE_DURATION, value).apply()
        }

    fun reset() {
        screenOnCount = 0
        deviceUseDuration = 0
    }

    override fun toString(): String {
        return String.format("DeviceUsageStats[screenOnCount=%d, deviceUseDuration=%dms]", screenOnCount, deviceUseDuration)
    }

}