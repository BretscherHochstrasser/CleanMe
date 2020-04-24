package ch.bretscherhochstrasser.cleanme.settings

import android.content.Context
import android.content.SharedPreferences
import ch.bretscherhochstrasser.cleanme.annotation.AppContext
import toothpick.InjectConstructor
import javax.inject.Singleton

/**
 * Utility to access app settings stored in preferences
 */
@Singleton
@InjectConstructor
class AppSettings(@AppContext private val context: Context) {

    companion object {
        private const val PREF_NAME = "app_settings"

        private const val PREF_SERVICE_ENABLED = "service_enabled"
        private const val PREF_OVERLAY_ENABLED = "overlay_enabled"
        private const val PREF_CLEAN_INTERVAL = "clean_interval"
        private const val PREF_MAX_OVERLAY_PARTICLE_COUNT = "max_overlay_particle_count"
        private const val PREF_START_ON_BOOT = "start_on_boot"

        private const val DEFAULT_MAX_OVERLAY_PARTICLE_COUNT = 25
    }

    private val settings: SharedPreferences
        get() {
            return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }

    /**
     * Indicates whether the overlay should be shown or not
     */
    var serviceEnabled: Boolean
        get() {
            return settings.getBoolean(PREF_SERVICE_ENABLED, true)
        }
        set(value) {
            settings.edit().putBoolean(PREF_SERVICE_ENABLED, value).apply()
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
    var cleanInterval: CleanInterval
        get() {
            return CleanInterval.fromMinutes(
                settings.getInt(
                    PREF_CLEAN_INTERVAL,
                    CleanInterval.TWO_HOURS.durationMinutes
                )
            )
        }
        set(value) {
            settings.edit().putInt(PREF_CLEAN_INTERVAL, value.durationMinutes).apply()
        }

    /**
     * Maximum number of overlay particles to show
     */
    var maxOverlayParticleCount: Int
        get() {
            return settings.getInt(
                PREF_MAX_OVERLAY_PARTICLE_COUNT,
                DEFAULT_MAX_OVERLAY_PARTICLE_COUNT
            )
        }
        set(value) {
            check(value > 0) { "max overlay particle count must be a positive value" }
            settings.edit().putInt(PREF_MAX_OVERLAY_PARTICLE_COUNT, value).apply()
        }

    /**
     * Start app on device boot
     */
    var startOnBoot: Boolean
        get() {
            return settings.getBoolean(
                PREF_START_ON_BOOT,
                false
            )
        }
        set(value) {
            settings.edit().putBoolean(PREF_START_ON_BOOT, value).apply()
        }

}
