package ch.bretscherhochstrasser.cleanme.overlay

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

/**
 * Draws debug information on the canvas of
 */
class DebugInfo {

    companion object {
        private const val DEBUG_CORNER_RADIUS_PX = 24
        private const val DEBUG_TEXT_SIZE_PX = 24
    }

    private data class DebugCorner(
        val x: Double,
        val y: Double,
        val color: Int
    )

    private val corners: Array<DebugCorner> = arrayOf(
        DebugCorner(0.0, 0.0, Color.parseColor("#AAFF0000")),
        DebugCorner(1.0, 0.0, Color.parseColor("#AA00FF00")),
        DebugCorner(1.0, 1.0, Color.parseColor("#AA00FFFF")),
        DebugCorner(0.0, 1.0, Color.parseColor("#AAFFFF00"))
    )

    fun drawDebugInfo(canvas: Canvas, rotationHelper: RotationHelper) {
        val width = canvas.width
        val height = canvas.height

        val paint = Paint()
        paint.style = Paint.Style.FILL

        corners.forEachIndexed { index, corner ->
            val drawPos = rotationHelper.calculateOrientationCorrectedPosition(
                corner.x, corner.y,
                width - DEBUG_CORNER_RADIUS_PX * 2,
                height - DEBUG_CORNER_RADIUS_PX * 2
            )
            paint.color = corner.color
            canvas.drawCircle(
                drawPos.x + DEBUG_CORNER_RADIUS_PX,
                drawPos.y + DEBUG_CORNER_RADIUS_PX,
                DEBUG_CORNER_RADIUS_PX.toFloat(), paint
            )
            paint.color = Color.DKGRAY
            paint.textSize = DEBUG_TEXT_SIZE_PX.toFloat()
            paint.textAlign = Paint.Align.CENTER
            canvas.drawText(
                (index + 1).toString(),
                drawPos.x + DEBUG_CORNER_RADIUS_PX,
                drawPos.y + DEBUG_CORNER_RADIUS_PX + paint.textSize / 2,
                paint
            )
        }

        paint.color = Color.DKGRAY
        canvas.drawText(rotationHelper.name, width / 2f, height - 50f, paint)
    }

}