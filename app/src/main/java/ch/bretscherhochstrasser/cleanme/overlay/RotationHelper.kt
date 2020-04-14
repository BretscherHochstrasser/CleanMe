package ch.bretscherhochstrasser.cleanme.overlay

import android.graphics.PointF
import android.view.Surface

/**
 * Draw corrections to handle screen rotation
 */
enum class RotationHelper(
    private val correctRelPos: (relPosX: Double, relPosY: Double) -> Pair<Double, Double>,
    val correctionAngle: Float
) {

    ROTATION_0(
        { relPosX: Double, relPosY: Double -> Pair<Double, Double>(relPosX, relPosY) },
        0f
    ),
    ROTATION_90(
        { relPosX: Double, relPosY: Double -> Pair<Double, Double>(relPosY, 1 - relPosX) },
        -90f
    ),
    ROTATION_180(
        { relPosX: Double, relPosY: Double -> Pair<Double, Double>(1 - relPosX, 1 - relPosY) },
        -180f
    ),
    ROTATION_270(
        { relPosX: Double, relPosY: Double -> Pair<Double, Double>(1 - relPosY, relPosX) },
        -270f
    );

    fun calculateOrientationCorrectedPosition(
        relPosX: Double,
        relPosY: Double,
        availableWidth: Int,
        availableHeight: Int
    ): PointF {
        val adaptedRelPos = correctRelPos(relPosX, relPosY)
        val drawPosX = (adaptedRelPos.first * availableWidth).toFloat()
        val drawPosY = (adaptedRelPos.second * availableHeight).toFloat()
        return PointF(drawPosX, drawPosY)
    }

    companion object {
        fun fromScreenRotation(screenRotation: Int): RotationHelper {
            return when (screenRotation) {
                Surface.ROTATION_0 -> ROTATION_0
                Surface.ROTATION_90 -> ROTATION_90
                Surface.ROTATION_180 -> ROTATION_180
                Surface.ROTATION_270 -> ROTATION_270
                else -> ROTATION_0 // should never happen
            }
        }
    }
}