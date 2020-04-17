package ch.bretscherhochstrasser.cleanme.service

import androidx.lifecycle.MutableLiveData
import javax.inject.Singleton

/**
 * Holds the current state of the [CleanMeService]
 */
@Singleton
class ServiceState {

    val observingDeviceUsage = MutableLiveData(false)

}
