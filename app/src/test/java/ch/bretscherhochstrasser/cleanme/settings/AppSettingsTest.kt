package ch.bretscherhochstrasser.cleanme.settings

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.CoreMatchers.`is` as Is

@RunWith(AndroidJUnit4::class)
class AppSettingsTest {

    @Test
    fun testDefaults() {
        val appSettings = AppSettings(ApplicationProvider.getApplicationContext())

        assertThat(appSettings.serviceEnabled, Is(true))
        assertThat(appSettings.startOnBoot, Is(false))
        assertThat(appSettings.cleanInterval, Is(CleanInterval.TWO_HOURS))
        assertThat(appSettings.overlayEnabled, Is(false))
        assertThat(appSettings.maxOverlayParticleCount, Is(25))
    }

}