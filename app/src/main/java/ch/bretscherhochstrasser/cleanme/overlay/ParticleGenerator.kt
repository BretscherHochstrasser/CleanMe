package ch.bretscherhochstrasser.cleanme.overlay

import timber.log.Timber
import toothpick.InjectConstructor
import java.util.*
import kotlin.math.*

/**
 * Generator for randomized [Particle]s
 */
@InjectConstructor
class ParticleGenerator {

    companion object {
        // valid coordinate values must be within this range
        private val COORDINATE_BOUNDS = 0.0..1.0

        // the maximum radius is half of the diagonal of a square with side 1
        private val MAX_RADIUS = sqrt(2.0) / 2

        // max allowed deviations of gauss distribution value from mean. Defines the 'width' of
        // the inverted gauss distribution 'valley', and therefore how much the particles will
        // be gathered towards the screen edge
        private const val STD_DEV_LIMIT = 4.0
    }

    private val random = Random(System.currentTimeMillis())

    fun generateParticle(): Particle {
        val position = getPosition()
        return Particle(getType(), position.first, position.second, getRotationAngle())
    }

    private fun getType(): ParticleType {
        val types = ParticleType.values()
        return types[random.nextInt(types.size)]
    }

    private fun getPosition(): Pair<Double, Double> {
        // get a random circle position
        var randomCirclePos = getPositionOnCircle()
        var tryCount = 1
        // the random circle position could be out of bounds, recreate until position is within bounds
        while (!COORDINATE_BOUNDS.contains(randomCirclePos.first) ||
            !COORDINATE_BOUNDS.contains(randomCirclePos.second)
        ) {
            randomCirclePos = getPositionOnCircle()
            tryCount++
        }
        Timber.d(
            "Generated valid particle position (%f/%f) after %d tries",
            randomCirclePos.first,
            randomCirclePos.second,
            tryCount
        )
        return randomCirclePos
    }

    private fun getPositionOnCircle(): Pair<Double, Double> {
        // We want the particles to gather more on the sides than in the center of the screen.
        // To get this effect, we use an 'inverted-gaussian-distributed' random value.
        // Since we do not care about negative values, we simply mirror negative values into positive.
        // Now we have a value between 0..1 with higher probability towards 1.
        val invertedGaussPositive = getInvertedScaledGauss().absoluteValue

        // scale the maxRadius with this value
        val randomRadius = invertedGaussPositive * MAX_RADIUS

        // create a random angle between 0..2PI
        val randomAngle = random.nextDouble() * 2 * PI

        // calculate coordinates on the circle
        val relX = sin(randomAngle) * randomRadius
        val relY = cos(randomAngle) * randomRadius

        // move center of circle from 0/0 to 0.5/0.5
        return Pair(relX + 0.5, relY + 0.5)
    }

    /**
     * Returns a scaled inverted-gaussian-distributed random value between -1 and +1
     */
    private fun getInvertedScaledGauss(): Double {
        // limited gauss value to defined standard deviation limits
        var gaussVal = random.nextGaussian().coerceIn(-STD_DEV_LIMIT, STD_DEV_LIMIT)

        // move negative values to positive to create a 'valley'
        if (gaussVal < 0) gaussVal += 2 * STD_DEV_LIMIT

        gaussVal -= STD_DEV_LIMIT // move 'vally' center back to 0

        return gaussVal / STD_DEV_LIMIT // scale value to -1..1
    }

    private fun getRotationAngle(): Int {
        return random.nextInt(360)
    }

}