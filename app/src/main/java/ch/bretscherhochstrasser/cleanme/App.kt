package ch.bretscherhochstrasser.cleanme

import android.app.Application
import android.content.Context
import ch.bretscherhochstrasser.cleanme.deviceusage.DeviceUsageStatsManager
import ch.bretscherhochstrasser.cleanme.service.ServiceState
import com.jakewharton.threetenabp.AndroidThreeTen
import timber.log.Timber

/**
 * Application class, contains global initialization stuff
 */
class App : Application() {

    //TODO: Solve global singletons better once there is DI added
    val deviceUsageStatsManager = DeviceUsageStatsManager(this)
    val serviceState = ServiceState()
    val appSettings = AppSettings(this)

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        AndroidThreeTen.init(this)
    }

}

val Context.deviceUsageStatsManager
    get() = (applicationContext as App).deviceUsageStatsManager

val Context.serviceState
    get() = (applicationContext as App).serviceState

val Context.appSettings
    get() = (applicationContext as App).appSettings