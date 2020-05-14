package ch.bretscherhochstrasser.cleanme.about

import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.bretscherhochstrasser.cleanme.BuildConfig
import ch.bretscherhochstrasser.cleanme.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AboutActivityTest {

    @Test
    fun testAppVersion() {
        launchActivity<AboutActivity>().use {
            onView(withId(R.id.label_app_version)).check(matches(withText(BuildConfig.VERSION_NAME)))
        }
    }

}