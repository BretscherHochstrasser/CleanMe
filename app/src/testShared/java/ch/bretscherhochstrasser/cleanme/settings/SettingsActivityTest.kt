package ch.bretscherhochstrasser.cleanme.settings

import android.app.Activity
import android.app.Instrumentation
import android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION
import android.view.InputDevice
import android.view.MotionEvent
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.GeneralClickAction
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Tap
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.bretscherhochstrasser.cleanme.*
import ch.bretscherhochstrasser.cleanme.annotation.ApplicationScope
import ch.bretscherhochstrasser.cleanme.deviceusage.DeviceUsageStatsManager
import ch.bretscherhochstrasser.cleanme.helper.OverlayPermissionWrapper
import ch.bretscherhochstrasser.cleanme.overlay.ParticleGrowthModel
import ch.bretscherhochstrasser.cleanme.service.ServiceHelper
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import toothpick.Toothpick
import toothpick.testing.ToothPickTestModule

@RunWith(AndroidJUnit4::class)
class SettingsActivityTest: MockitoTest() {

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
        // clean interval, max particle count, size, and growth model must be set, so the activity can start
        whenever(mockAppSettings.cleanInterval).thenReturn(CleanInterval.TWO_HOURS)
        whenever(mockAppSettings.maxOverlayParticleCount).thenReturn(25)
        whenever(mockAppSettings.overlayParticleGrowthModel).thenReturn(ParticleGrowthModel.LINEAR)
        whenever(mockAppSettings.overlayParticleSize).thenReturn(24)
        //service and overlay enabled to enable all elements by default
        whenever(mockAppSettings.serviceEnabled).thenReturn(true)
        whenever(mockAppSettings.overlayEnabled).thenReturn(true)
        // overlay permission is enabled by default
        whenever(mockOverlayPermissionWrapper.canDrawOverlay).thenReturn(true)
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
            onView(withId(R.id.button_growth_model_linear)).check(matches(isEnabled()))
            onView(withId(R.id.button_growth_model_exponential)).check(matches(isEnabled()))
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
            onView(withId(R.id.button_growth_model_linear)).check(matches(not(isEnabled())))
            onView(withId(R.id.button_growth_model_exponential)).check(matches(not(isEnabled())))
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
            onView(withId(R.id.button_growth_model_linear)).check(matches(not(isEnabled())))
            onView(withId(R.id.button_growth_model_exponential)).check(matches(not(isEnabled())))
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
            onView(withId(R.id.button_growth_model_linear)).check(matches(isEnabled()))
            onView(withId(R.id.button_growth_model_exponential)).check(matches(isEnabled()))
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
    fun testEnableOverlay_InitialSwitchSetting_OverlayOnPermissionGranted() {
        whenever(mockAppSettings.overlayEnabled).thenReturn(true)
        whenever(mockOverlayPermissionWrapper.canDrawOverlay).thenReturn(true)
        launchActivity<SettingsActivity>().use {
            checkOverlaySwitchOn()
        }
    }

    @Test
    fun testEnableOverlay_InitialSwitchSetting_OverlayOnPermissionNotGranted() {
        whenever(mockAppSettings.overlayEnabled).thenReturn(true)
        whenever(mockOverlayPermissionWrapper.canDrawOverlay).thenReturn(false)
        launchActivity<SettingsActivity>().use {
            checkOverlaySwitchOff()
        }
    }

    @Test
    fun testEnableOverlay_InitialSwitchSetting_OverlayOffPermissionGranted() {
        whenever(mockAppSettings.overlayEnabled).thenReturn(false)
        whenever(mockOverlayPermissionWrapper.canDrawOverlay).thenReturn(true)
        launchActivity<SettingsActivity>().use {
            checkOverlaySwitchOff()
        }
    }

    @Test
    fun testEnableOverlay_InitialSwitchSetting_OverlayOffPermissionNotGranted() {
        whenever(mockAppSettings.overlayEnabled).thenReturn(false)
        whenever(mockOverlayPermissionWrapper.canDrawOverlay).thenReturn(false)
        launchActivity<SettingsActivity>().use {
            checkOverlaySwitchOff()
        }
    }

    @Test
    fun testEnableOverlay_SwitchOn_WithPermission() {
        whenever(mockAppSettings.overlayEnabled).thenReturn(false)

        launchActivity<SettingsActivity>().use {
            checkOverlaySwitchOff()

            onView(withId(R.id.switch_overlay_enabled)).perform(scrollTo(), click())

            verify(mockAppSettings).overlayEnabled = true
            verify(mockUsageStatsManager).updateUsageStats()
            checkOverlaySwitchOn()
        }
    }

    @Test
    fun testEnableOverlay_SwitchOn_NoPermission_CancelInfoDialogButton() {
        whenever(mockAppSettings.overlayEnabled).thenReturn(false)
        whenever(mockOverlayPermissionWrapper.canDrawOverlay).thenReturn(false)

        launchActivity<SettingsActivity>().use {
            checkOverlaySwitchOff()

            onView(withId(R.id.switch_overlay_enabled)).perform(scrollTo(), click())
            checkPermissionInfoDialogShown()
            onView(withId(android.R.id.button2)).inRoot(isDialog()).perform(click())

            verify(mockAppSettings).overlayEnabled = false
            checkOverlaySwitchOff()
        }
    }

    @Test
    fun testEnableOverlay_SwitchOn_NoPermission_CancelInfoDialogOutside() {
        whenever(mockAppSettings.overlayEnabled).thenReturn(false)
        whenever(mockOverlayPermissionWrapper.canDrawOverlay).thenReturn(false)

        launchActivity<SettingsActivity>().use {
            checkOverlaySwitchOff()

            onView(withId(R.id.switch_overlay_enabled)).perform(scrollTo(), click())
            checkPermissionInfoDialogShown()
            // click top left of screen at 100/100, outside of the dialog window
            onView(withText(R.string.settings_dialog_overlay_permission_info_message)).perform(
                GeneralClickAction(
                    Tap.SINGLE, { FloatArray(2) { 100f } }, Press.FINGER,
                    InputDevice.SOURCE_UNKNOWN, MotionEvent.BUTTON_PRIMARY, null
                )
            )

            verify(mockAppSettings).overlayEnabled = false
            checkOverlaySwitchOff()
        }
    }

    @Test
    fun testEnableOverlay_SwitchOn_NoPermission_GrantedInSettings() {
        whenever(mockAppSettings.overlayEnabled).thenReturn(false)
        whenever(mockOverlayPermissionWrapper.canDrawOverlay).thenReturn(false)

        Intents.init()

        // simulate user accepting permission in device settings
        intending(hasAction(ACTION_MANAGE_OVERLAY_PERMISSION)).respondWithFunction {
            whenever(mockOverlayPermissionWrapper.canDrawOverlay).thenReturn(true)
            // we must return the overlayEnabled setting as true, because the activity will read it
            // during onResume after onActivityResult
            whenever(mockAppSettings.overlayEnabled).thenReturn(true)
            Instrumentation.ActivityResult(Activity.RESULT_OK, null)
        }

        launchActivity<SettingsActivity>().use {
            checkOverlaySwitchOff()

            onView(withId(R.id.switch_overlay_enabled)).perform(scrollTo(), click())
            checkPermissionInfoDialogShown()
            onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click())

            verify(mockAppSettings).overlayEnabled = true
            verify(mockUsageStatsManager).updateUsageStats()
            checkOverlaySwitchOn()
        }
        Intents.release()
    }

    @Test
    fun testEnableOverlay_SwitchOn_NoPermission_NotGrantedInSettings() {
        whenever(mockAppSettings.overlayEnabled).thenReturn(false)
        whenever(mockOverlayPermissionWrapper.canDrawOverlay).thenReturn(false)

        Intents.init()

        // simulate user accepting permission in device settings
        intending(allOf(hasAction(ACTION_MANAGE_OVERLAY_PERMISSION))).respondWithFunction {
            Instrumentation.ActivityResult(Activity.RESULT_OK, null)
        }

        launchActivity<SettingsActivity>().use {
            checkOverlaySwitchOff()

            onView(withId(R.id.switch_overlay_enabled)).perform(scrollTo(), click())
            checkPermissionInfoDialogShown()
            onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click())

            verify(mockAppSettings).overlayEnabled = false
            checkOverlaySwitchOff()
        }
        Intents.release()
    }

    @Test
    fun testEnableOverlay_SwitchOff() {
        whenever(mockAppSettings.overlayEnabled).thenReturn(true)

        launchActivity<SettingsActivity>().use {
            checkOverlaySwitchOn()

            onView(withId(R.id.switch_overlay_enabled)).perform(scrollTo(), click())

            verify(mockAppSettings).overlayEnabled = false
            verify(mockUsageStatsManager).updateUsageStats()
            checkOverlaySwitchOff()
        }
    }

    private fun checkOverlaySwitchOn() {
        onView(withId(R.id.switch_overlay_enabled)).check(matches(isChecked()))
        onView(withId(R.id.slider_max_overlay_particles)).check(matches(isEnabled()))
        onView(withId(R.id.button_growth_model_linear)).check(matches(isEnabled()))
        onView(withId(R.id.button_growth_model_exponential)).check(matches(isEnabled()))
        onView(withId(R.id.slider_particle_size)).check(matches(isEnabled()))
        onView(withId(R.id.slider_particle_transparency)).check(matches(isEnabled()))
    }

    private fun checkOverlaySwitchOff() {
        onView(withId(R.id.switch_overlay_enabled)).check(matches(isNotChecked()))
        onView(withId(R.id.slider_max_overlay_particles)).check(matches(not(isEnabled())))
        onView(withId(R.id.button_growth_model_linear)).check(matches(not(isEnabled())))
        onView(withId(R.id.button_growth_model_exponential)).check(matches(not(isEnabled())))
        onView(withId(R.id.slider_particle_transparency)).check(matches(not(isEnabled())))
        onView(withId(R.id.slider_particle_size)).check(matches(not(isEnabled())))
    }

    private fun checkPermissionInfoDialogShown() {
        onView(withText(R.string.settings_dialog_overlay_permission_info_title))
            .inRoot(isDialog()).check(matches(isDisplayed()))
        onView(withText(R.string.settings_dialog_overlay_permission_info_message))
            .inRoot(isDialog()).check(matches(isDisplayed()))
        onView(withId(android.R.id.button1))
            .inRoot(isDialog()).check(matches(
                withText(R.string.settings_dialog_overlay_permission_info_open_settings_button)))
        onView(withId(android.R.id.button2))
            .inRoot(isDialog()).check(matches(
                withText(android.R.string.cancel)))
    }

    @Test
    fun testParticleGrowthModel_SetLinear() {
        whenever(mockAppSettings.overlayParticleGrowthModel).thenReturn(ParticleGrowthModel.EXPONENTIAL)

        launchActivity<SettingsActivity>().use {
            onView(withId(R.id.button_growth_model_exponential)).check(matches(isChecked()))
            val switch = onView(withId(R.id.button_growth_model_linear))
            switch.check(matches(isNotChecked()))
            switch.perform(scrollTo(), click())

            verify(mockAppSettings).overlayParticleGrowthModel = ParticleGrowthModel.LINEAR
        }
    }

    @Test
    fun testParticleGrowthModel_SetExponential() {
        whenever(mockAppSettings.overlayParticleGrowthModel).thenReturn(ParticleGrowthModel.LINEAR)

        launchActivity<SettingsActivity>().use {
            onView(withId(R.id.button_growth_model_linear)).check(matches(isChecked()))
            val switch = onView(withId(R.id.button_growth_model_exponential))
            switch.check(matches(isNotChecked()))
            switch.perform(scrollTo(), click())

            verify(mockAppSettings).overlayParticleGrowthModel = ParticleGrowthModel.EXPONENTIAL
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