package ch.bretscherhochstrasser.cleanme.settings

import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.bretscherhochstrasser.cleanme.App
import ch.bretscherhochstrasser.cleanme.R
import ch.bretscherhochstrasser.cleanme.annotation.ApplicationScope
import ch.bretscherhochstrasser.cleanme.deviceusage.DeviceUsageStatsManager
import ch.bretscherhochstrasser.cleanme.service.ServiceHelper
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
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

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        // clean interval and max particle count must be set, so the activity can start
        whenever(mockAppSettings.cleanInterval).thenReturn(CleanInterval.TWO_HOURS)
        whenever(mockAppSettings.maxOverlayParticleCount).thenReturn(25)
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
            val switch = onView(withId(R.id.switch_track_device_usage))
            switch.check(matches(isChecked()))
            switch.perform(click())

            verify(mockAppSettings).serviceEnabled = false
            verify(mockServiceHelper).stopObserveDeviceUsage()
        }
    }

    @Test
    fun testServiceEnabled_SwitchOn() {
        whenever(mockAppSettings.serviceEnabled).thenReturn(false)

        launchActivity<SettingsActivity>().use {
            val switch = onView(withId(R.id.switch_track_device_usage))
            switch.check(matches(isNotChecked()))
            switch.perform(click())

            verify(mockAppSettings).serviceEnabled = true
            verify(mockServiceHelper).startObserveDeviceUsage()
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

        val appContext = ApplicationProvider.getApplicationContext<App>()
        val expectedLabelBefore = appContext.getString(
            R.string.settings_label_clean_interval,
            appContext.getString(CleanInterval.ONE_HOUR.text)
        )
        val expectedLabelAfter = appContext.getString(
            R.string.settings_label_clean_interval,
            appContext.getString(CleanInterval.FOUR_HOURS.text)
        )

        launchActivity<SettingsActivity>().use {
            val label = onView(withId(R.id.label_clean_interval))
            label.check(matches(withText(expectedLabelBefore)))

            onView(withId(R.id.button_edit_clean_interval)).perform(scrollTo(), click())
            //selection dialog opens
            onView(withText(CleanInterval.FOUR_HOURS.text)).inRoot(isDialog()).perform(click())

            verify(mockAppSettings).cleanInterval = CleanInterval.FOUR_HOURS
            verify(mockUsageStatsManager).updateUsageStats()
            label.check(matches(withText(expectedLabelAfter)))
        }
    }

    fun testEnableOverlay_SwitchOn() {
        whenever(mockAppSettings.overlayEnabled).thenReturn(false)

        launchActivity<SettingsActivity>().use {
            val switch = onView(withId(R.id.switch_overlay_enabled))
            switch.check(matches(isNotChecked()))
            switch.perform(click())

            verify(mockAppSettings).overlayEnabled = true
            verify(mockUsageStatsManager).updateUsageStats()
        }
    }

    fun testEnableOverlay_SwitchOff() {
        whenever(mockAppSettings.overlayEnabled).thenReturn(true)

        launchActivity<SettingsActivity>().use {
            val switch = onView(withId(R.id.switch_overlay_enabled))
            switch.check(matches(isChecked()))
            switch.perform(click())

            verify(mockAppSettings).overlayEnabled = false
            verify(mockUsageStatsManager).updateUsageStats()
        }
    }

    @Test
    fun testSetMaxParticles() {
        whenever(mockAppSettings.maxOverlayParticleCount).thenReturn(42)
        launchActivity<SettingsActivity>().use {
            val slider = onView(withId(R.id.slider_max_overlay_particles))
            slider.perform(scrollTo(), click())

            verify(mockAppSettings).maxOverlayParticleCount = 52
            verify(mockUsageStatsManager).updateUsageStats()
        }
    }
}