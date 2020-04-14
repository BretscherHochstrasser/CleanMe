package ch.bretscherhochstrasser.cleanme.overlay

import java.util.*

/**
 * Generator for randomized [Particle]s
 */
class ParticleGenerator {

    private val random = Random(System.currentTimeMillis())

    fun generateParticle(): Particle {
        val relPosX = getNegativeInvertedRelativeGaussPosition()
        val relPosY = getNegativeInvertedRelativeGaussPosition()
        return Particle(getType(), relPosX, relPosY, getRotationAngle())
    }

    private fun getType(): ParticleType {
        val types = ParticleType.values()
        return types[random.nextInt(types.size)]
    }

    private fun getNegativeInvertedRelativeGaussPosition(): Double {
        // We want the particles to gather more on the sides than in the center of the screen.
        // To get this effect, we use an 'inverted' Gauss distribution
        var gaussVal = random.nextGaussian().coerceIn(-3.0, 3.0) // limited gauss value
        if (gaussVal < 0) gaussVal += 6 // move negative values to positive to create a 'valley' between 0 to 6
        return gaussVal / 6 // scale value to 0 to 1
    }

    private fun getRotationAngle(): Int {
        return random.nextInt(360)
    }

}