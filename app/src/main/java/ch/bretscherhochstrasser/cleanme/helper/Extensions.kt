package ch.bretscherhochstrasser.cleanme.helper

import android.content.Context
import androidx.lifecycle.LiveData
import ch.bretscherhochstrasser.cleanme.R
import org.threeten.bp.Duration

/**
 * Extension property to avoid null handling for live data values
 */
val <T> LiveData<T>.valueNN: T
    get() = this.value!!

/**
 * Formats a remaining duration in milliseconds to count down format HH:mm.
 * A negative value will result in a format +HH:mm
 */
fun formatCountdownHoursAndMinutes(millis: Long): String {
    val duration = Duration.ofMillis(millis)
    var formatted =
        String.format("%02d:%02d", duration.abs().toHours(), duration.abs().toMinutes() % 60)
    if (duration.isNegative) formatted = "+$formatted"
    return formatted
}

/**
 * Formats a duration, pretty-printing hours and minutes.
 * D6h -> "6 hours"
 * D1h35m -> "1 hour and 35 minutes"
 * D50m -> "50 minutes"
 */
fun formatCleanInterval(duration: Duration, context: Context): String {
    val hours = duration.abs().toHours().toInt()
    val minutes = duration.abs().toMinutes().toInt() % 60
    var formattedDuration = ""
    if (hours > 0) {
        formattedDuration = context.resources.getQuantityString(R.plurals.settings_label_clean_interval_hours, hours, hours)
    }
    if(hours > 0 && minutes > 0) {
        formattedDuration += " "
        formattedDuration += context.getString(R.string.settings_label_clean_interval_and)
        formattedDuration += " "
    }
    if(minutes > 0) {
        formattedDuration += context.resources.getQuantityString(R.plurals.settings_label_clean_interval_minutes, minutes, minutes)
    }
    if(hours == 0 && minutes == 0) {
        formattedDuration = context.resources.getQuantityString(R.plurals.settings_label_clean_interval_minutes, 0, 0)
    }
    return formattedDuration
}
