package ch.bretscherhochstrasser.cleanme.service

import androidx.lifecycle.MutableLiveData

/**
 * Holds the current state of the [CleanMeService]
 */
class ServiceState {

    val observingDeviceUsage = MutableLiveData(false)

}
