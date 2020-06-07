package ch.bretscherhochstrasser.cleanme.about

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.ComponentNameMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import ch.bretscherhochstrasser.cleanme.BuildConfig
import ch.bretscherhochstrasser.cleanme.R
import ch.bretscherhochstrasser.cleanme.annotation.ApplicationScope
import ch.bretscherhochstrasser.cleanme.chooser
import ch.bretscherhochstrasser.cleanme.getString
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.containsString
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import toothpick.Toothpick
import toothpick.testing.ToothPickTestModule
import org.hamcrest.CoreMatchers.`is` as Is

@RunWith(AndroidJUnit4::class)
class AboutActivityTest {

    @Before
    fun setUp() {
        Toothpick.openScope(ApplicationScope::class.java)
            .installTestModules(ToothPickTestModule(this))
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
        Toothpick.closeScope(ApplicationScope::class.java)
    }

    @Test
    fun testAppVersion() {
        launchActivity<AboutActivity>().use {
            onView(withId(R.id.label_app_version)).check(matches(withText(BuildConfig.VERSION_NAME)))
        }
    }

    @Test
    fun testOpenWebsite() {
        intending(hasAction(Intent.ACTION_VIEW)).respondWith(
            Instrumentation.ActivityResult(
                Activity.RESULT_CANCELED, null
            )
        )

        launchActivity<AboutActivity>().use {
            onView(withId(R.id.image_developer_logo)).perform(click())

            intended(
                allOf(
                    hasAction(Intent.ACTION_VIEW),
                    hasData(Uri.parse("https://bretscherhochstrasser.ch"))
                )
            )
        }
    }

    @Test
    fun testShareAppLink() {
        intending(hasAction(Intent.ACTION_CHOOSER)).respondWith(
            Instrumentation.ActivityResult(
                Activity.RESULT_CANCELED, null
            )
        )

        launchActivity<AboutActivity>().use {
            onView(withId(R.id.button_share)).perform(click())

            intended(
                chooser(
                    allOf(
                        hasAction(Intent.ACTION_SEND),
                        hasExtra(Intent.EXTRA_SUBJECT, getString(R.string.about_share_subject)),
                        hasExtra(
                            Intent.EXTRA_TEXT,
                            getString(
                                R.string.about_share_text,
                                "https://play.google.com/store/apps/details?id=ch.bretscherhochstrasser.cleanme"
                            )
                        ),
                        hasType("text/plain")
                    )
                )
            )
        }
    }

    @Test
    fun testSendFeedback() {
        intending(hasAction(Intent.ACTION_CHOOSER)).respondWith(
            Instrumentation.ActivityResult(
                Activity.RESULT_CANCELED, null
            )
        )

        launchActivity<AboutActivity>().use {
            onView(withId(R.id.button_feedback)).perform(click())

            intended(
                chooser(
                    allOf(
                        hasAction(Intent.ACTION_SENDTO),
                        hasData("mailto:"),
                        hasExtra(
                            Intent.EXTRA_EMAIL,
                            arrayOf("bretscherhochstrasser@gmail.com")
                        ),
                        hasExtra(
                            Intent.EXTRA_SUBJECT,
                            getString(R.string.about_feedback_subject)
                        ),
                        hasExtra(
                            Is(Intent.EXTRA_TEXT),
                            containsString("Device Information")
                        )
                    )
                )
            )
        }
    }

    @Test
    fun testShowWelcomeWizardAgain() {
        launchActivity<AboutActivity>().use {
            onView(withText(R.string.about_button_replay_welcome)).perform(click())
            intended(hasComponent(ComponentNameMatchers.hasShortClassName(".welcome.WelcomeWizardActivity")))
        }
    }

    @Test
    fun testOpen3rdPartyLicenseActivity() {
        launchActivity<AboutActivity>().use {
            openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().context)
            onView(withText(R.string.about_menu_3rd_party_licenses)).perform(click())

            intended(hasComponent(OssLicensesMenuActivity::class.java.name))
        }
    }
}