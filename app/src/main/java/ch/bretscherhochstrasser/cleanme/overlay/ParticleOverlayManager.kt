package ch.bretscherhochstrasser.cleanme.overlay

import android.graphics.PixelFormat
import android.os.Build
import android.view.WindowManager
import ch.bretscherhochstrasser.cleanme.helper.OverlayPermissionWrapper
import ch.bretscherhochstrasser.cleanme.settings.AppSettings
import timber.log.Timber
import toothpick.InjectConstructor

/**
 * Handles the particle overlay, calculates the particle count from device usage time, and generates
 * the randomly generated particles.
 */
@InjectConstructor
class ParticleOverlayManager(
    private val particleOverlay: ParticleOverlayView,
    private val particleGenerator: ParticleGenerator,
    private val appSettings: AppSettings,
    private val overlayPermissionWrapper: OverlayPermissionWrapper,
    private val windowManager: WindowManager
) {

    private var overlayShown = false

    fun update(deviceUseDuration: Long, timeUntilMaxParticles: Long) {
        Timber.d("Updating particle overlay use duration %dms.", deviceUseDuration)
        particleOverlay.alpha = appSettings.overlayParticleAlpha
        particleOverlay.particleSize = appSettings.overlayParticleSize
        val growthModel = appSettings.overlayParticleGrowthModel
        val targetParticleCount = growthModel.calculateParticleCount(
            deviceUseDuration,
            timeUntilMaxParticles,
            appSettings.maxOverlayParticleCount
        )
        adaptOverlayToTargetParticleCount(targetParticleCount)
    }

    fun showOverlay() {
        if (!overlayShown) {
            if (overlayPermissionWrapper.canDrawOverlay) {
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