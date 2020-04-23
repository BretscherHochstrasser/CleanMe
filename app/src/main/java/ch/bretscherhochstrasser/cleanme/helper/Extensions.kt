package ch.bretscherhochstrasser.cleanme.helper

import androidx.lifecycle.LiveData
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
