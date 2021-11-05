package ch.bretscherhochstrasser.cleanme.helper

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.bretscherhochstrasser.cleanme.R
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.Duration
import org.hamcrest.CoreMatchers.`is` as Is

@RunWith(AndroidJUnit4::class)
class FormatCleanIntervalTest {

    @Test
    fun testFormatCleanInterval() {
        val appContext = ApplicationProvider.getApplicationContext<Context>()

        val and = appContext.getString(R.string.settings_label_clean_interval_and)
        val formatHours = appContext.resources.getQuantityString(
            R.plurals.settings_label_clean_interval_hours,
            10
        )
        val formatHour =
            appContext.resources.getQuantityString(R.plurals.settings_label_clean_interval_hours, 1)
        val formatMinutes = appContext.resources.getQuantityString(
            R.plurals.settings_label_clean_interval_minutes,
            10
        )
        val formatMinute = appContext.resources.getQuantityString(
            R.plurals.settings_label_clean_interval_minutes,
            1
        )

        assertThat(
            formatCleanInterval(Duration.ofHours(3).plusMinutes(44), appContext),
            Is(String.format("$formatHours $and $formatMinutes", 3, 44))
        )
        assertThat(
            formatCleanInterval(Duration.ofHours(17), appContext),
            Is(String.format(formatHours, 17))
        )
        assertThat(
            formatCleanInterval(Duration.ofHours(6).plusMinutes(1), appContext),
            Is(String.format("$formatHours $and $formatMinute", 6, 1))
        )
        assertThat(
            formatCleanInterval(Duration.ofHours(1).plusMinutes(14), appContext),
            Is(String.format("$formatHour $and $formatMinutes", 1, 14))
        )
        assertThat(
            formatCleanInterval(Duration.ofHours(1).plusMinutes(1), appContext),
            Is(String.format("$formatHour $and $formatMinute", 1, 1))
        )
        assertThat(
            formatCleanInterval(Duration.ofMinutes(50), appContext),
            Is(String.format(formatMinutes, 50))
        )
        assertThat(
            formatCleanInterval(Duration.ofMinutes(1), appContext),
            Is(String.format(formatMinute, 1))
        )
        assertThat(
            formatCleanInterval(Duration.ofHours(0), appContext),
            Is(String.format(formatMinutes, 0))
        )
    }
}