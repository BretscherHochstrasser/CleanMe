package ch.bretscherhochstrasser.cleanme.helper

import androidx.lifecycle.LiveData
import org.threeten.bp.Duration

/**
 * Extension property to avoid null handling for live data values
 */
val <T> LiveData<T>.valueNN: T
    get() = this.value!!

/**
 * Formats a duration in milliseconds to format HH:mm
 */
fun formatHoursAndMinutes(millis: Long): String {
    val duration = Duration.ofMillis(millis)
    return String.format("%02d:%02d", duration.toHours(), duration.toMinutes() % 60)
}
