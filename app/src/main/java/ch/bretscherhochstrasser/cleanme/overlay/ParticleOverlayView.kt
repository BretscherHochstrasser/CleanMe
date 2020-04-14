package ch.bretscherhochstrasser.cleanme.overlay

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
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
        private const val DEBUG_OVERLAY_ENABLED = true
        private const val DEBUG_CORNER_RADIUS_DP = 16f
        private const val DEBUG_TEXT_SIZE = 16f
    }

    private val particles = LinkedList<Particle>()

    private val corners: Array<DebugCorner> = arrayOf(
        DebugCorner(0.0, 0.0, Color.parseColor("#AAFF0000")),
        DebugCorner(1.0, 0.0, Color.parseColor("#AA00FF00")),
        DebugCorner(1.0, 1.0, Color.parseColor("#AA00FFFF")),
        DebugCorner(0.0, 1.0, Color.parseColor("#AAFFFF00"))
    )

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
            val particleSize = dpToPx(PARTICLE_SIZE_DP)
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
                drawDebugInfo(rotationHelper, canvas)
            }
        }
    }

    private fun drawDebugInfo(
        rotationHelper: RotationHelper,
        canvas: Canvas
    ) {
        val debugCornerRadius = dpToPx(DEBUG_CORNER_RADIUS_DP)

        val paint = Paint()
        paint.style = Paint.Style.FILL

        corners.forEachIndexed { index, corner ->
            val drawPos = rotationHelper.calculateOrientationCorrectedPosition(
                corner.x, corner.y,
                width - debugCornerRadius * 2,
                height - debugCornerRadius * 2
            )
            paint.color = corner.color
            canvas.drawCircle(
                drawPos.x + debugCornerRadius,
                drawPos.y + debugCornerRadius,
                debugCornerRadius.toFloat(), paint
            )
            paint.color = Color.DKGRAY
            paint.textSize = dpToPx(DEBUG_TEXT_SIZE).toFloat()
            paint.textAlign = Paint.Align.CENTER
            canvas.drawText(
                (index + 1).toString(),
                drawPos.x + debugCornerRadius,
                drawPos.y + debugCornerRadius + paint.textSize / 2, paint
            )
        }

        paint.color = Color.DKGRAY
        canvas.drawText(rotationHelper.name, width / 2f, height - 50f, paint)
    }

    private fun dpToPx(dp: Float): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics
    ).toInt()

    private fun loadDrawable(type: ParticleType): Drawable {
        if (drawableCache[type] == null) {
            drawableCache[type] = AppCompatResources.getDrawable(context, type.icon)
        }
        return drawableCache[type]!!
    }

    private data class DebugCorner(
        val x: Double,
        val y: Double,
        val color: Int
    )


}