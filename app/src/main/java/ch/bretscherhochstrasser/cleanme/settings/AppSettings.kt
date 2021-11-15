package ch.bretscherhochstrasser.cleanme.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import ch.bretscherhochstrasser.cleanme.annotation.AppContext
import ch.bretscherhochstrasser.cleanme.overlay.ParticleGrowthModel
import org.threeten.bp.Duration
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
        private const val PREF_OVERLAY_PARICLE_GROWTH_MODEL = "overlay_particle_growth_model"
        private const val PREF_START_ON_BOOT = "start_on_boot"

        private const val DEFAULT_CLEAN_INTERVAL = 120 // 2 hours
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
     * Clean interval duration
     */
    var cleanInterval: Duration
        get() {
            val durationInMinutes = settings.getInt(
                PREF_CLEAN_INTERVAL,
                DEFAULT_CLEAN_INTERVAL
            )
            return Duration.ofMinutes(durationInMinutes.toLong())
        }
        set(value) {
            settings.edit().putInt(PREF_CLEAN_INTERVAL, value.toMinutes().toInt()).apply()
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

    /**
     * Growth model for overlay particles
     */
    var overlayParticleGrowthModel: ParticleGrowthModel
        get() {
            return ParticleGrowthModel.valueOfSafely(
                settings.getString(
                    PREF_OVERLAY_PARICLE_GROWTH_MODEL, ParticleGrowthModel.EXPONENTIAL.name
                )
            )
        }
        set(value) {
            settings.edit().putString(PREF_OVERLAY_PARICLE_GROWTH_MODEL, value.name).apply()
        }

    /**
     * Indicates whether the particle overlay is supported on this device or not.
     */
    val overlaySupported: Boolean
        // Android 12+ does not allow input through overlay windows on other apps.
        // Therefore the particle overlay feature is unusable and must be disabled :-(
        get() = Build.VERSION.SDK_INT < Build.VERSION_CODES.S

}
