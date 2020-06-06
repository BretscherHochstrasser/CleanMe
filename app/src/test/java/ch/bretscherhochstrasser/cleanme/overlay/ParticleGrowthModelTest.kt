package ch.bretscherhochstrasser.cleanme.overlay

import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

import org.hamcrest.CoreMatchers.`is` as Is

class ParticleGrowthModelTest {

    var model: ParticleGrowthModel? = null

    @Test
    fun calculateParticleCount_Linear() {
        model = ParticleGrowthModel.LINEAR
        testModel(0L, 0)
        testModel(3782L, 38)
        testModel(5000L, 50)
        testModel(7500L, 75)
        testModel(9924L, 99)
        testModel(10000L, 100)
        testModel(12000L, 100)
    }

    @Test
    fun calculateParticleCount_Exponential() {
        model = ParticleGrowthModel.EXPONENTIAL
        testModel(0L, 1)
        testModel(3782L, 6)
        testModel(5000L, 10)
        testModel(7500L, 32)
        testModel(9924L, 97)
        testModel(10000L, 100)
        testModel(12000L, 100)
    }

    @Test(expected = IllegalArgumentException::class)
    fun calculateParticleCount_ThrowsExceptionBelowZero() {
        model = ParticleGrowthModel.LINEAR
        testModel(-100L, 0)
    }

    private fun testModel(useTime: Long, expectedCount: Int) {
        val particleCount = model?.calculateParticleCount(useTime, 10000L, 100)
        assertThat(particleCount, Is(expectedCount))
    }

    @Test
    fun testValueOfSafely() {
        assertThat(ParticleGrowthModel.valueOfSafely(null), Is(ParticleGrowthModel.EXPONENTIAL))
        assertThat(ParticleGrowthModel.valueOfSafely(""), Is(ParticleGrowthModel.EXPONENTIAL))
        assertThat(
            ParticleGrowthModel.valueOfSafely("invalid"),
            Is(ParticleGrowthModel.EXPONENTIAL)
        )
        assertThat(
            ParticleGrowthModel.valueOfSafely("EXPONENTIAL"),
            Is(ParticleGrowthModel.EXPONENTIAL)
        )
        assertThat(ParticleGrowthModel.valueOfSafely("LINEAR"), Is(ParticleGrowthModel.LINEAR))
    }
}