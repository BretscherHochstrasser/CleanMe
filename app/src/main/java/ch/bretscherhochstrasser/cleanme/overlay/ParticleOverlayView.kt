package ch.bretscherhochstrasser.cleanme.overlay

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import androidx.appcompat.content.res.AppCompatResources
import toothpick.InjectConstructor
import java.util.*

/**
 * View to render [Particle]s based on relative coordinates. The view adapts rendering depending on
 * screen rotation and tries to keep the particles on roughly fixed positions on the physical screen
 */
@InjectConstructor
class ParticleOverlayView(context: Context) : View(context) {

    private val particles = LinkedList<Particle>()

    private val drawableCache = EnumMap<ParticleType, Drawable>(ParticleType::class.java)

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    val particleCount: Int
        get() {
            return particles.size
        }

    var alpha: Int = 255
        set(value) {
            check(alpha in 0..255)
            // invalidate if value has changed
            if (value != field) {
                field = value
                invalidate()
            }
        }

    var particleSize: Int = 24
        set(value) {
            check(value > 0)
            // invalidate if value has changed
            if (value != field) {
                field = value
                invalidate()
            }
        }

    fun pushParticle(particle: Particle) {
        particles.push(particle)
        invalidate()
    }

    fun popParticle(): Particle {
        val particle = particles.pop()
        invalidate()
        return particle
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        if (canvas != null) {
            val particleSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, particleSize.toFloat(), resources.displayMetrics
            ).toInt()
            val availableWidth = width - particleSize
            val availableHeight = height - particleSize
            val rotationHelper =
                RotationHelper.fromScreenRotation(windowManager.defaultDisplay.rotation)

            particles.forEach {
                val particleDrawable = loadDrawable(it.type)
                particleDrawable.setBounds(0, 0, particleSize, particleSize)
                particleDrawable.alpha = alpha

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
        }
    }

    private fun loadDrawable(type: ParticleType): Drawable {
        if (drawableCache[type] == null) {
            drawableCache[type] = AppCompatResources.getDrawable(context, type.icon)
        }
        return drawableCache[type]!!
    }

}