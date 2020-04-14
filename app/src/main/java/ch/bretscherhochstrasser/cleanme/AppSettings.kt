package ch.bretscherhochstrasser.cleanme

import android.content.Context
import android.content.SharedPreferences
import org.threeten.bp.Duration

/**
 * Utility to access app settings stored in preferences
 */
class AppSettings(private val context: Context) {

    companion object {
        private const val PREF_NAME = "app_settings"
        private const val PREF_OVERLAY_ENABLED = "overlay_enabled"
        private const val PREF_CLEAN_INTERVAL = "clean_interval"

        private const val DEFAULT_CLEAN_INTERVAL = 15 // 15 min for initial testing
    }

    private val settings: SharedPreferences
        get() {
            return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }

    /**
     * Indicates whether the overlay should be shown or not
     */
    var overlayEnabled: Boolean
        get() {
            return settings.getBoolean(PREF_OVERLAY_ENABLED, false)
        }
        set(value) {
            settings.edit().putBoolean(PREF_OVERLAY_ENABLED, value).apply()
        }

    /**
     * Clean interval in minutes
     */
    var cleanIntervalMinutes: Int
        get() {
            return settings.getInt(PREF_CLEAN_INTERVAL, DEFAULT_CLEAN_INTERVAL)
        }
        set(value) {
            check(value > 0) { "clean interval must be a positive value" }
            settings.edit().putInt(PREF_CLEAN_INTERVAL, value).apply()
        }

    /**
     * Clean interval in milliseconds (read-only)
     */
    val cleanIntervalMillis: Long
        get() = Duration.ofMinutes(cleanIntervalMinutes.toLong()).toMillis()
}
