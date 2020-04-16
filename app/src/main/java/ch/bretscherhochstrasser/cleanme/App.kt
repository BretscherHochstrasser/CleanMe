package ch.bretscherhochstrasser.cleanme

import android.app.Application
import android.content.Context
import ch.bretscherhochstrasser.cleanme.deviceusage.DeviceUsageStatsManager
import com.jakewharton.threetenabp.AndroidThreeTen
import timber.log.Timber

/**
 * Application class, contains global initialization stuff
 */
class App : Application() {

    val deviceUsageStatsManager = DeviceUsageStatsManager(this)

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
