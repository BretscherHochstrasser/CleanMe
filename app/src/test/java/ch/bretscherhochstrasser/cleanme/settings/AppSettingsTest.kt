package ch.bretscherhochstrasser.cleanme.settings

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.bretscherhochstrasser.cleanme.overlay.ParticleGrowthModel
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.CoreMatchers.`is` as Is

@RunWith(AndroidJUnit4::class)
class AppSettingsTest {

    lateinit var appSettings: AppSettings

    @Before
    fun setUp() {
        appSettings = AppSettings(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun testDefaults() {
        assertThat(appSettings.serviceEnabled, Is(true))
        assertThat(appSettings.startOnBoot, Is(false))
        assertThat(appSettings.cleanInterval, Is(CleanInterval.TWO_HOURS))
        assertThat(appSettings.overlayEnabled, Is(false))
        assertThat(appSettings.maxOverlayParticleCount, Is(25))
        assertThat(appSettings.overlayParticleAlpha, Is(191))
        assertThat(appSettings.overlayParticleTransparency, Is(25))
        assertThat(appSettings.overlayParticleSize, Is(24))
        assertThat(appSettings.overlayParticleGrowthModel, Is(ParticleGrowthModel.EXPONENTIAL))
    }

    @Test
    fun testServiceEnabled() {
        appSettings.serviceEnabled = false
        assertThat(appSettings.serviceEnabled, Is(false))
    }

    @Test
    fun testStartOnBoot() {
        appSettings.startOnBoot = true
        assertThat(appSettings.startOnBoot, Is(true))
    }

    @Test
    fun testCleanInterval() {
        appSettings.cleanInterval = CleanInterval.SIX_HOURS
        assertThat(appSettings.cleanInterval, Is(CleanInterval.SIX_HOURS))
    }

    @Test
    fun testOverlayEnabled() {
        appSettings.overlayEnabled = true
        assertThat(appSettings.overlayEnabled, Is(true))
    }

    @Test
    fun testMaxOverlayParticleCount() {
        appSettings.maxOverlayParticleCount = 82
        assertThat(appSettings.maxOverlayParticleCount, Is(82))
    }

    @Test(expected = IllegalStateException::class)
    fun testMaxOverlayParticleCount_BelowZero() {
        appSettings.maxOverlayParticleCount = -13
    }

    @Test
    fun testOverlayParticleAlpha() {
        appSettings.overlayParticleAlpha = 50
        assertThat(appSettings.overlayParticleAlpha, Is(50))
    }

    @Test(expected = IllegalStateException::class)
    fun testOverlayParticleAlpha_TooLarge() {
        appSettings.overlayParticleAlpha = 260
    }

    @Test(expected = IllegalStateException::class)
    fun testOverlayParticleAlpha_BelowZero() {
        appSettings.overlayParticleAlpha = -4
    }

    @Test
    fun testOverlayParticleTransparency() {
        appSettings.overlayParticleTransparency = 100
        assertThat(appSettings.overlayParticleTransparency, Is(100))
        assertThat(appSettings.overlayParticleAlpha, Is(0))

        appSettings.overlayParticleTransparency = 50
        assertThat(appSettings.overlayParticleTransparency, Is(50))
        assertThat(appSettings.overlayParticleAlpha, Is(127))
    }

    @Test(expected = IllegalStateException::class)
    fun testOverlayParticleTransparency_TooLarge() {
        appSettings.overlayParticleTransparency = 102
    }

    @Test(expected = IllegalStateException::class)
    fun testOverlayParticleTransparency_BelowZero() {
        appSettings.overlayParticleTransparency = -3
    }

    @Test
    fun testOverlayParticleSize() {
        appSettings.overlayParticleSize = 34
        assertThat(appSettings.overlayParticleSize, Is(34))
    }

    @Test(expected = IllegalStateException::class)
    fun testOverlayParticlesSize_Zero() {
        appSettings.overlayParticleSize = 0
    }

    @Test(expected = IllegalStateException::class)
    fun testOverlayParticlesSize_BelowZero() {
        appSettings.overlayParticleSize = -24
    }

    @Test
    fun testOverlayParticleGrowthModel() {
        appSettings.overlayParticleGrowthModel = ParticleGrowthModel.LINEAR
        assertThat(appSettings.overlayParticleGrowthModel, Is(ParticleGrowthModel.LINEAR))

        appSettings.overlayParticleGrowthModel = ParticleGrowthModel.EXPONENTIAL
        assertThat(appSettings.overlayParticleGrowthModel, Is(ParticleGrowthModel.EXPONENTIAL))
    }
}