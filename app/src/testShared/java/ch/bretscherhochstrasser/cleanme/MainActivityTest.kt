package ch.bretscherhochstrasser.cleanme

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.ComponentNameMatchers.hasShortClassName
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.bretscherhochstrasser.cleanme.annotation.ApplicationScope
import ch.bretscherhochstrasser.cleanme.deviceusage.DeviceUsageStats
import ch.bretscherhochstrasser.cleanme.deviceusage.DeviceUsageStatsManager
import ch.bretscherhochstrasser.cleanme.settings.AppSettings
import ch.bretscherhochstrasser.cleanme.settings.CleanInterval
import com.stephentuso.welcome.WelcomeSharedPreferencesHelper
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.kotlin.whenever
import toothpick.Toothpick
import toothpick.testing.ToothPickTestModule
import org.hamcrest.CoreMatchers.`is` as Is


@RunWith(AndroidJUnit4::class)
class MainActivityTest: MockitoTest() {

    @Mock
    private lateinit var mockAppSettings: AppSettings

    @Mock
    private lateinit var mockUsageStatsManager: DeviceUsageStatsManager

    @Before
    fun setUp() {
        // clean interval and device usage stats are required for the activity to start up
        whenever(mockAppSettings.cleanInterval).thenReturn(CleanInterval.TWO_HOURS)
        whenever(mockUsageStatsManager.deviceUsageStats).thenReturn(
            MutableLiveData(DeviceUsageStats(12, 300000L))
        )
        Toothpick.openScope(ApplicationScope::class.java)
            .installTestModules(ToothPickTestModule(this))
        Intents.init()
    }

    @After
    fun tearDown() {
        Toothpick.closeScope(ApplicationScope::class.java)
        Intents.release()
    }

    @Test
    fun testWizardStartedOnFirstStart() {

        //make sure welcome-completed flag is false
        WelcomeSharedPreferencesHelper.removeWelcomeCompleted(
            ApplicationProvider.getApplicationContext<App>(),
            ""
        )

        launchActivity<MainActivity>()

        intended(hasComponent(hasShortClassName(".welcome.WelcomeWizardActivity")))
    }

    @Test
    fun testWizardNotStartedIfAlreadyFinished() {

        //make sure welcome-completed flag is true
        WelcomeSharedPreferencesHelper.storeWelcomeCompleted(
            ApplicationProvider.getApplicationContext<App>(),
            ""
        )

        val scenario = launchActivity<MainActivity>()

        // check that we are still on the main activity
        assertThat(scenario.state, Is(Lifecycle.State.RESUMED))
    }

}