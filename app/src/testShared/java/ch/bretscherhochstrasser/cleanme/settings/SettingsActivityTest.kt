package ch.bretscherhochstrasser.cleanme.settings

import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.bretscherhochstrasser.cleanme.R
import ch.bretscherhochstrasser.cleanme.annotation.ApplicationScope
import ch.bretscherhochstrasser.cleanme.deviceusage.DeviceUsageStatsManager
import ch.bretscherhochstrasser.cleanme.getString
import ch.bretscherhochstrasser.cleanme.helper.OverlayPermissionWrapper
import ch.bretscherhochstrasser.cleanme.service.ServiceHelper
import ch.bretscherhochstrasser.cleanme.withFormattedText
import ch.bretscherhochstrasser.cleanme.withSliderValue
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import toothpick.Toothpick
import toothpick.testing.ToothPickTestModule

@RunWith(AndroidJUnit4::class)
class SettingsActivityTest {

    @Mock
    private lateinit var mockAppSettings: AppSettings

    @Mock
    private lateinit var mockUsageStatsManager: DeviceUsageStatsManager

    @Mock
    private lateinit var mockServiceHelper: ServiceHelper

    @Mock
    private lateinit var mockOverlayPermissionWrapper: OverlayPermissionWrapper

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        // clean interval and max particle count and size must be set, so the activity can start
        whenever(mockAppSettings.cleanInterval).thenReturn(CleanInterval.TWO_HOURS)
        whenever(mockAppSettings.maxOverlayParticleCount).thenReturn(25)
        whenever(mockAppSettings.overlayParticleSize).thenReturn(24)
        //service and overlay enabled to enable all elements by default
        whenever(mockAppSettings.serviceEnabled).thenReturn(true)
        whenever(mockAppSettings.overlayEnabled).thenReturn(true)
        // overlay permission is enabled by default
        whenever(mockOverlayPermissionWrapper.canDrawOverlay()).thenReturn(true)
        Toothpick.openScope(ApplicationScope::class.java)
            .installTestModules(ToothPickTestModule(this))
    }

    @After
    fun tearDown() {
        Toothpick.closeScope(ApplicationScope::class.java)
    }

    @Test
    fun testServiceEnabled_SwitchOff() {
        whenever(mockAppSettings.serviceEnabled).thenReturn(true)

        launchActivity<SettingsActivity>().use {
            onView(withId(R.id.switch_start_on_boot)).check(matches(isEnabled()))
            onView(withId(R.id.button_edit_clean_interval)).check(matches(isEnabled()))
            onView(withId(R.id.switch_overlay_enabled)).check(matches(isEnabled()))
            onView(withId(R.id.slider_max_overlay_particles)).check(matches(isEnabled()))
            onView(withId(R.id.slider_particle_size)).check(matches(isEnabled()))
            onView(withId(R.id.slider_particle_transparency)).check(matches(isEnabled()))

            val switch = onView(withId(R.id.switch_track_device_usage))
            switch.check(matches(isChecked()))
            switch.perform(click())

            verify(mockAppSettings).serviceEnabled = false
            verify(mockServiceHelper).stopObserveDeviceUsage()
            onView(withId(R.id.switch_start_on_boot)).check(matches(not(isEnabled())))
            onView(withId(R.id.button_edit_clean_interval)).check(matches(not(isEnabled())))
            onView(withId(R.id.switch_overlay_enabled)).check(matches(not(isEnabled())))
            onView(withId(R.id.slider_max_overlay_particles)).check(matches(not(isEnabled())))
            onView(withId(R.id.slider_particle_size)).check(matches(not(isEnabled())))
            onView(withId(R.id.slider_particle_transparency)).check(matches(not(isEnabled())))
        }
    }

    @Test
    fun testServiceEnabled_SwitchOn() {
        whenever(mockAppSettings.serviceEnabled).thenReturn(false)

        launchActivity<SettingsActivity>().use {
            onView(withId(R.id.switch_start_on_boot)).check(matches(not(isEnabled())))
            onView(withId(R.id.button_edit_clean_interval)).check(matches(not(isEnabled())))
            onView(withId(R.id.switch_overlay_enabled)).check(matches(not(isEnabled())))
            onView(withId(R.id.slider_max_overlay_particles)).check(matches(not(isEnabled())))
            onView(withId(R.id.slider_particle_size)).check(matches(not(isEnabled())))
            onView(withId(R.id.slider_particle_transparency)).check(matches(not(isEnabled())))

            val switch = onView(withId(R.id.switch_track_device_usage))
            switch.check(matches(isNotChecked()))
            switch.perform(click())

            verify(mockAppSettings).serviceEnabled = true
            verify(mockServiceHelper).startObserveDeviceUsage()
            onView(withId(R.id.switch_start_on_boot)).check(matches(isEnabled()))
            onView(withId(R.id.button_edit_clean_interval)).check(matches(isEnabled()))
            onView(withId(R.id.switch_overlay_enabled)).check(matches(isEnabled()))
            onView(withId(R.id.slider_max_overlay_particles)).check(matches(isEnabled()))
            onView(withId(R.id.slider_particle_size)).check(matches(isEnabled()))
            onView(withId(R.id.slider_particle_transparency)).check(matches(isEnabled()))
        }
    }

    @Test
    fun testStartOnBoot_SwitchOff() {
        whenever(mockAppSettings.startOnBoot).thenReturn(true)

        launchActivity<SettingsActivity>().use {
            val switch = onView(withId(R.id.switch_start_on_boot))
            switch.check(matches(isChecked()))
            switch.perform(click())

            verify(mockAppSettings).startOnBoot = false
        }
    }

    @Test
    fun testStartOnBoot_SwitchOn() {
        whenever(mockAppSettings.startOnBoot).thenReturn(false)

        launchActivity<SettingsActivity>().use {
            val switch = onView(withId(R.id.switch_start_on_boot))
            switch.check(matches(isNotChecked()))
            switch.perform(click())

            verify(mockAppSettings).startOnBoot = true
        }
    }

    @Test
    fun testSetCleanIntervalSelection() {
        whenever(mockAppSettings.cleanInterval).thenReturn(CleanInterval.ONE_HOUR)

        launchActivity<SettingsActivity>().use {
            val label = onView(withId(R.id.label_clean_interval))
            label.check(
                matches(
                    withFormattedText(
                        R.string.settings_label_clean_interval,
                        getString(CleanInterval.ONE_HOUR.text)
                    )
                )
            )

            onView(withId(R.id.button_edit_clean_interval)).perform(scrollTo(), click())
            //selection dialog opens
            onView(withText(CleanInterval.FOUR_HOURS.text)).inRoot(isDialog()).perform(click())

            verify(mockAppSettings).cleanInterval = CleanInterval.FOUR_HOURS
            verify(mockUsageStatsManager).updateUsageStats()
            label.check(
                matches(
                    withFormattedText(
                        R.string.settings_label_clean_interval,
                        getString(CleanInterval.FOUR_HOURS.text)
                    )
                )
            )
        }
    }

    @Test
    fun testEnableOverlay_SwitchOn() {
        whenever(mockAppSettings.overlayEnabled).thenReturn(false)

        launchActivity<SettingsActivity>().use {
            onView(withId(R.id.slider_max_overlay_particles)).check(matches(not(isEnabled())))
            onView(withId(R.id.slider_particle_transparency)).check(matches(not(isEnabled())))
            onView(withId(R.id.slider_particle_size)).check(matches(not(isEnabled())))

            val switch = onView(withId(R.id.switch_overlay_enabled))
            switch.check(matches(isNotChecked()))
            switch.perform(scrollTo(), click())

            verify(mockAppSettings).overlayEnabled = true
            verify(mockUsageStatsManager).updateUsageStats()
            onView(withId(R.id.slider_max_overlay_particles)).check(matches(isEnabled()))
            onView(withId(R.id.slider_particle_size)).check(matches(isEnabled()))
            onView(withId(R.id.slider_particle_transparency)).check(matches(isEnabled()))
        }
    }

    @Test
    fun testEnableOverlay_SwitchOff() {
        whenever(mockAppSettings.overlayEnabled).thenReturn(true)

        launchActivity<SettingsActivity>().use {
            onView(withId(R.id.slider_max_overlay_particles)).check(matches(isEnabled()))
            onView(withId(R.id.slider_particle_size)).check(matches(isEnabled()))
            onView(withId(R.id.slider_particle_transparency)).check(matches(isEnabled()))

            val switch = onView(withId(R.id.switch_overlay_enabled))
            switch.check(matches(isChecked()))
            switch.perform(scrollTo(), click())

            verify(mockAppSettings).overlayEnabled = false
            verify(mockUsageStatsManager).updateUsageStats()
            onView(withId(R.id.slider_max_overlay_particles)).check(matches(not(isEnabled())))
            onView(withId(R.id.slider_particle_size)).check(matches(not(isEnabled())))
            onView(withId(R.id.slider_particle_transparency)).check(matches(not(isEnabled())))
        }
    }

    @Test
    fun testSetMaxParticles() {
        whenever(mockAppSettings.maxOverlayParticleCount).thenReturn(42)
        launchActivity<SettingsActivity>().use {
            onView(withId(R.id.label_max_overlay_particles)).perform(scrollTo())
                .check(matches(withFormattedText(R.string.settings_label_max_particles, 42)))
            val slider = onView(withId(R.id.slider_max_overlay_particles))
            slider.check(matches(withSliderValue(42f)))
            slider.perform(scrollTo(), click())

            onView(withId(R.id.label_max_overlay_particles)).perform(scrollTo())
                .check(matches(withFormattedText(R.string.settings_label_max_particles, 52)))
            verify(mockAppSettings).maxOverlayParticleCount = 52
            verify(mockUsageStatsManager).updateUsageStats()
        }
    }

    @Test
    fun testSetParticleSize() {
        whenever(mockAppSettings.overlayParticleSize).thenReturn(24)

        launchActivity<SettingsActivity>().use {
            onView(withId(R.id.label_particle_size)).perform(scrollTo())
                .check(matches(withFormattedText(R.string.settings_label_particle_size, 24)))
            val slider = onView(withId(R.id.slider_particle_size))
            slider.check(matches(withSliderValue(24f)))
            slider.perform(scrollTo(), click())

            onView(withId(R.id.label_particle_size)).perform(scrollTo())
                .check(matches(withFormattedText(R.string.settings_label_particle_size, 29)))
            verify(mockAppSettings).overlayParticleSize = 29
            verify(mockUsageStatsManager).updateUsageStats()
        }
    }

    @Test
    fun testSetParticleTransparency() {
        whenever(mockAppSettings.overlayParticleTransparency).thenReturn(25)
        launchActivity<SettingsActivity>().use {
            onView(withId(R.id.label_particle_transparency)).perform(scrollTo())
                .check(
                    matches(
                        withFormattedText(
                            R.string.settings_label_particle_transparency,
                            25
                        )
                    )
                )
            val slider = onView(withId(R.id.slider_particle_transparency))
            slider.check(matches(withSliderValue(25f)))
            slider.perform(scrollTo(), click())

            onView(withId(R.id.label_particle_transparency)).perform(scrollTo())
                .check(
                    matches(
                        withFormattedText(
                            R.string.settings_label_particle_transparency,
                            49
                        )
                    )
                )
            verify(mockAppSettings).overlayParticleTransparency = 49
            verify(mockUsageStatsManager).updateUsageStats()
        }
    }
}