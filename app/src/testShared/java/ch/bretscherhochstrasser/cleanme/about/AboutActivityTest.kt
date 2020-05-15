package ch.bretscherhochstrasser.cleanme.about

import android.content.Intent
import android.net.Uri
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.bretscherhochstrasser.cleanme.BuildConfig
import ch.bretscherhochstrasser.cleanme.R
import ch.bretscherhochstrasser.cleanme.annotation.ApplicationScope
import ch.bretscherhochstrasser.cleanme.chooser
import ch.bretscherhochstrasser.cleanme.getString
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
    fun testSendFeedback() {
        launchActivity<AboutActivity>().use {
            onView(withId(R.id.button_feedback)).perform(click())

            intended(
                chooser(
                    allOf(
                        hasAction(Intent.ACTION_SEND),
                        hasType("message/rfc822"),
                        hasExtra(
                            Intent.EXTRA_EMAIL,
                            arrayOf("bretscherhochstrasser@gmail.com")
                        ),
                        hasExtra(
                            Intent.EXTRA_SUBJECT,
                            getString(R.string.feedback_subject)
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

}