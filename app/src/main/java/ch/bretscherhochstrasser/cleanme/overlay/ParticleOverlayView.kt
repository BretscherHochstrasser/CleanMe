package ch.bretscherhochstrasser.cleanme.overlay

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import androidx.appcompat.content.res.AppCompatResources
import java.util.*

/**
 * View to render [Particle]s based on relative coordinates. The view adapts rendering depending on
 * screen rotation and tries to keep the particles on roughly fixed positions on the physical screen
 */
class ParticleOverlayView(context: Context) : View(context) {

    companion object {
        private const val PARTICLE_SIZE_DP = 24f
        private const val DEBUG_OVERLAY_ENABLED = false
    }

    private val particles = LinkedList<Particle>()

    private val drawableCache = EnumMap<ParticleType, Drawable>(ParticleType::class.java)

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    val particleCount: Int
        get() {
            return particles.size
        }

    fun pushParticle(particle: Particle) {
        particles.push(particle)
        invalidate()
    }

    fun popParticle(): Particle {
        return particles.pop()
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        if (canvas != null) {
            val particleSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, PARTICLE_SIZE_DP, resources.displayMetrics
            ).toInt()
            val availableWidth = width - particleSize
            val availableHeight = height - particleSize
            val rotationHelper =
                RotationHelper.fromScreenRotation(windowManager.defaultDisplay.rotation)

            particles.forEach {
                val particleDrawable = loadDrawable(it.type)
                particleDrawable.setBounds(0, 0, particleSize, particleSize)

                val drawPos = rotationHelper.calculateOrientationCorrectedPosition(
                    it.relPosX, it.relPosY, availableWidth, availableHeight
                )

                canvas.save()

                // translate to center of particle to draw
                canvas.translate(drawPos.x + particleSize / 2, drawPos.y + particleSize / 2)
                // rotate to apply particle rotation and correct for screen rotation
                canvas.rotate(it.rotationAngle + rotationHelper.correctionAngle)
                // translate to top-left of particle position
                canvas.translate(-particleSize / 2f, -particleSize / 2f)
                // draw particle
                particleDrawable.draw(canvas)

                canvas.restore()
            }

            if (DEBUG_OVERLAY_ENABLED) {
                DebugInfo().drawDebugInfo(canvas, rotationHelper)
            }
        }
    }

    private fun loadDrawable(type: ParticleType): Drawable {
        if (drawableCache[type] == null) {
            drawableCache[type] = AppCompatResources.getDrawable(context, type.icon)
        }
        return drawableCache[type]!!
    }

}