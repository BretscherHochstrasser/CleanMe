package ch.bretscherhochstrasser.cleanme.helper

import androidx.lifecycle.LiveData

/**
 * Extension property to avoid null handling for live data values
 */
val <T> LiveData<T>.valueNN: T
    get() = this.value!!