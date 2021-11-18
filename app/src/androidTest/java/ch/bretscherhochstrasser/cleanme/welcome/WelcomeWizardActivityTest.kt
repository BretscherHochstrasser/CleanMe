package ch.bretscherhochstrasser.cleanme.welcome

import android.app.Activity
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.bretscherhochstrasser.cleanme.MockitoTest
import ch.bretscherhochstrasser.cleanme.R
import ch.bretscherhochstrasser.cleanme.annotation.ApplicationScope
import ch.bretscherhochstrasser.cleanme.settings.AppSettings
import ch.bretscherhochstrasser.cleanme.withDrawable
import org.hamcrest.CoreMatchers.allOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.kotlin.whenever
import toothpick.Toothpick
import toothpick.testing.ToothPickTestModule
import org.hamcrest.CoreMatchers.`is` as Is


@RunWith(AndroidJUnit4::class)
class WelcomeWizardActivityTest : MockitoTest() {

    @Mock
    private lateinit var mockAppSettings: AppSettings

    @Before
    fun setUp() {
        Toothpick.openScope(ApplicationScope::class.java)
            .installTestModules(ToothPickTestModule(this))
    }

    @Test
    fun testWizardSteps_OverlaySupported() {
        whenever(mockAppSettings.overlaySupported).thenReturn(true)
        launchActivity<WelcomeWizardActivity>().use {

            verifyWizardPage(
                R.drawable.welcome_intro,
                R.string.welcome_intro_title,
                R.string.welcome_intro_text
            )
            onView(withId(R.id.wel_button_next)).perform(click())

            verifyWizardPage(
                R.drawable.welcome_track_use,
                R.string.welcome_track_use_title,
                R.string.welcome_track_use_text
            )
            onView(withId(R.id.wel_button_next)).perform(click())

            verifyWizardPage(
                R.drawable.welcome_clean_reminder,
                R.string.welcome_clean_reminder_title,
                R.string.welcome_clean_reminder_text
            )
            onView(withId(R.id.wel_button_next)).perform(click())

            verifyWizardPage(
                R.drawable.welcome_clean_and_reset,
                R.string.welcome_clean_and_reset_title,
                R.string.welcome_clean_and_reset_text
            )
            onView(withId(R.id.wel_button_next)).perform(click())

            verifyWizardPage(
                R.drawable.welcome_overlay,
                R.string.welcome_overlay_title,
                R.string.welcome_overlay_text
            )
            onView(withId(R.id.wel_button_done)).perform(click())

            assertThat(it.state, Is(Lifecycle.State.DESTROYED))
            assertThat(it.result.resultCode, Is(Activity.RESULT_OK))
        }
    }

    @Test
    fun testWizardSteps_OverlayNotSupported() {
        whenever(mockAppSettings.overlaySupported).thenReturn(false)
        launchActivity<WelcomeWizardActivity>().use {

            verifyWizardPage(
                R.drawable.welcome_intro,
                R.string.welcome_intro_title,
                R.string.welcome_intro_text
            )
            onView(withId(R.id.wel_button_next)).perform(click())

            verifyWizardPage(
                R.drawable.welcome_track_use,
                R.string.welcome_track_use_title,
                R.string.welcome_track_use_text
            )
            onView(withId(R.id.wel_button_next)).perform(click())

            verifyWizardPage(
                R.drawable.welcome_clean_reminder,
                R.string.welcome_clean_reminder_title,
                R.string.welcome_clean_reminder_text
            )
            onView(withId(R.id.wel_button_next)).perform(click())

            verifyWizardPage(
                R.drawable.welcome_clean_and_reset,
                R.string.welcome_clean_and_reset_title,
                R.string.welcome_clean_and_reset_text
            )
            onView(withId(R.id.wel_button_done)).perform(click())

            assertThat(it.state, Is(Lifecycle.State.DESTROYED))
            assertThat(it.result.resultCode, Is(Activity.RESULT_OK))
        }
    }

    private fun verifyWizardPage(
        @DrawableRes drawable: Int,
        @StringRes title: Int,
        @StringRes text: Int
    ) {
        // searching via text avoids false negatives because of the repeating view IDs in the view
        // pager. Also gives the animation more time to finish before checking the image.
        onView(withText(title)).check(matches(isDisplayed()))
        onView(withText(text)).check(matches(isDisplayed()))
        onView(allOf(withId(R.id.wel_image), isDisplayed())).check(matches(withDrawable(drawable)))
    }
}