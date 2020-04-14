package ch.bretscherhochstrasser.cleanme.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.provider.Settings
import android.view.WindowManager
import timber.log.Timber

/**
 * Handles the particle overlay, calculates the particle count from device usage time, and generates
 * the randomly generated particles.
 */
class ParticleOverlayManager(private val context: Context) {

    companion object {
        private const val MAX_PARTICLES = 50
    }

    private val windowManager
        get() = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private val particleGenerator = ParticleGenerator()

    private lateinit var particleOverlay: ParticleOverlayView

    private var overlayShown = false

    fun update(deviceUseDuration: Long, timeUntilMaxParticles: Long) {
        Timber.d("Updating particle overlay use duration %dms.", deviceUseDuration)
        val targetParticleCount = calculateParticleCount(deviceUseDuration, timeUntilMaxParticles)
        adaptOverlayToTargetParticleCount(targetParticleCount)
    }

    fun showOverlay() {
        if (!overlayShown) {
            // lazy init the particle overlay view the first time it is required
            if (!::particleOverlay.isInitialized) particleOverlay = ParticleOverlayView(context)

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || // pre API 23 always allow overlay
                Settings.canDrawOverlays(context) // from API 23, check for permission
            ) {
                @Suppress("DEPRECATION")
                val overlayType =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                    else
                        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT

                val layoutParams = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    overlayType,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    PixelFormat.TRANSLUCENT
                )
                Timber.d("Showing particle overlay")
                windowManager.addView(particleOverlay, layoutParams)
                overlayShown = true
            }
        }
    }

    fun hideOverlay() {
        if (overlayShown) {
            Timber.d("Hiding particle overlay")
            windowManager.removeView(particleOverlay)
            overlayShown = false
        }
    }

    private fun calculateParticleCount(deviceUsageTime: Long, timeUntilMaxParticles: Long): Int {
        val limitedDeviceUsageTime = deviceUsageTime.coerceAtMost(timeUntilMaxParticles)
        // for now we only to linear particle creation
        val relativeProgress = limitedDeviceUsageTime.toDouble() / timeUntilMaxParticles
        val targetParticleCount = (MAX_PARTICLES * relativeProgress).toInt()
        Timber.d(
            "Target particle count %d (%.0f%%) for %dms device usage",
            targetParticleCount, relativeProgress * 100, deviceUsageTime
        )
        return targetParticleCount
    }

    private fun adaptOverlayToTargetParticleCount(targetParticleCount: Int) {
        Timber.d(
            "Adapting particle overly: current %d, target %d",
            particleOverlay.particleCount, targetParticleCount
        )

        // remove particles if there are too many
        while (particleOverlay.particleCount > targetParticleCount) {
            val oldParticle = particleOverlay.popParticle()
            Timber.d("Removed particle %s", oldParticle)
        }

        // add particles if not enough
        while (particleOverlay.particleCount < targetParticleCount) {
            val newParticle = particleGenerator.generateParticle()
            particleOverlay.pushParticle(newParticle)
            Timber.d("Added particle %s", newParticle)
        }
    }

}