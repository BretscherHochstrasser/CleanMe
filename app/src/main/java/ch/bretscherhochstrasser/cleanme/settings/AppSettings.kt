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
        private const val PREF_OVERLAY_PARTICLE_ALPHA = "overlay_particle_alpha"
        private const val PREF_OVERLAY_PARTICLE_SIZE = "overlay_particle_size"
        private const val PREF_START_ON_BOOT = "start_on_boot"

        private const val DEFAULT_MAX_OVERLAY_PARTICLE_COUNT = 25
        private const val DEFAULT_OVERLAY_PARTICLE_ALPHA = 191 // 25% transparency
        private const val DEFAULT_OVERLAY_PARTICLE_SIZE = 24
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

    /**
     * Transparency of overlay particles as alpha value.
     * (0 = transparent, 255 opaque)
     */
    var overlayParticleAlpha: Int
        get() {
            return settings.getInt(
                PREF_OVERLAY_PARTICLE_ALPHA,
                DEFAULT_OVERLAY_PARTICLE_ALPHA
            )
        }
        set(value) {
            check(value in 0..255) { "overlay particle alpha must be in the range 0..255" }
            settings.edit().putInt(PREF_OVERLAY_PARTICLE_ALPHA, value).apply()
        }

    /**
     * Transparency of overly particles in percent
     * (0 = opaque, 100 = transparent)
     */
    var overlayParticleTransparency: Int
        get() {
            return ((255f - overlayParticleAlpha) / 255 * 100).toInt()
        }
        set(value) {
            check(value in 0..100)
            overlayParticleAlpha = ((100f - value) / 100 * 255).toInt()
        }

    /**
     * Size of overlay particles in DP
     */
    var overlayParticleSize: Int
        get() {
            return settings.getInt(PREF_OVERLAY_PARTICLE_SIZE, DEFAULT_OVERLAY_PARTICLE_SIZE)
        }
        set(value) {
            check(value > 0)
            settings.edit().putInt(PREF_OVERLAY_PARTICLE_SIZE, value).apply()
        }

}
