package ch.bretscherhochstrasser.cleanme.overlay

import timber.log.Timber
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Growth models with different growth functions.
 * The growth functions take a relativeTime argument [0..1] and return relativeCount value [0..1].
 */
enum class ParticleGrowthModel(private val growthFunction: (relativeTime: Double) -> Double) {

    LINEAR({ relativeTime -> relativeTime }),
    EXPONENTIAL({ relativeTime -> 100.0.pow(relativeTime) / 100 });

    /**
     * Calculate the particle count from the device use time.
     *
     * @param deviceUsageTime the time the device has been in use
     * @param timeUntilMaxParticles time until the maximum number of particles should be reached
     * @param maxParticleCount maximum number of particle to show
     * @throws IllegalStateException if deviceUsageTime is < 0
     */
    fun calculateParticleCount(
        deviceUsageTime: Long,
        timeUntilMaxParticles: Long,
        maxParticleCount: Int
    ): Int {
        require(deviceUsageTime >= 0)
        val limitedDeviceUsageTime = deviceUsageTime.coerceAtMost(timeUntilMaxParticles)
        val relativeProgress = limitedDeviceUsageTime.toDouble() / timeUntilMaxParticles
        val relativeTargetCount = growthFunction.invoke(relativeProgress)
        val targetParticleCount = (relativeTargetCount * maxParticleCount).roundToInt()
        Timber.d(
            "Target particle count %d (%.0f%%) for %dms device usage (%.0f%%)",
            targetParticleCount, relativeTargetCount * 100, deviceUsageTime, relativeProgress * 100
        )
        return targetParticleCount
    }

    companion object {

        /**
         * Same as [valueOf] but returns [EXPONENTIAL] as default value if the given name is null
         * or invalid.
         */
        fun valueOfSafely(name: String?): ParticleGrowthModel {
            return if (name == null) {
                EXPONENTIAL
            } else {
                try {
                    valueOf(name)
                } catch (e: IllegalArgumentException) {
                    EXPONENTIAL
                }
            }
        }
    }

}