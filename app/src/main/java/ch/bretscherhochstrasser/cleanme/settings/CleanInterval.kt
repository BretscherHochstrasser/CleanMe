package ch.bretscherhochstrasser.cleanme.settings

import android.content.Context
import androidx.annotation.StringRes
import ch.bretscherhochstrasser.cleanme.R

/**
 * Holds the choice values for the clean interval
 */
enum class CleanInterval(@StringRes val text: Int, val durationMinutes: Int) {

    THIRTY_MIN(R.string.clean_interval_30min, 30),
    ONE_HOUR(R.string.clean_interval_1hour, 60),
    TWO_HOURS(R.string.clean_interval_2hours, 120),
    FOUR_HOURS(R.string.clean_interval_4hours, 240),
    SIX_HOURS(R.string.clean_interval_6hours, 360),
    EIGHT_HOURS(R.string.clean_interval_8hours, 480);

    val durationMillis = durationMinutes * 60000L

    companion object {
        fun fromMinutes(minutes: Int): CleanInterval {
            return when {
                minutes < 60 -> THIRTY_MIN
                minutes < 120 -> ONE_HOUR
                minutes < 240 -> TWO_HOURS
                minutes < 360 -> FOUR_HOURS
                minutes < 480 -> SIX_HOURS
                else -> EIGHT_HOURS
            }
        }

        fun getTextValues(context: Context): Array<String> {
            return values().map { context.getString(it.text) }.toTypedArray()
        }
    }
}