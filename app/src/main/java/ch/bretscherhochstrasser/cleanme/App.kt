package ch.bretscherhochstrasser.cleanme

import android.app.Application
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import ch.bretscherhochstrasser.cleanme.annotation.AppContext
import ch.bretscherhochstrasser.cleanme.annotation.ApplicationScope
import ch.bretscherhochstrasser.cleanme.service.ServiceHelper
import ch.bretscherhochstrasser.cleanme.settings.AppSettings
import com.jakewharton.threetenabp.AndroidThreeTen
import timber.log.Timber
import toothpick.ktp.KTP
import toothpick.ktp.binding.bind
import toothpick.ktp.binding.module
import toothpick.ktp.delegate.inject

/**
 * Application class, contains global initialization stuff
 */
class App : Application() {

    private val serviceHelper: ServiceHelper by inject()
    private val appSettings: AppSettings by inject()

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        AndroidThreeTen.init(this)
        KTP.openScope(ApplicationScope::class.java)
            .installModules(module {
                bind<Context>().withName(AppContext::class).toInstance { this@App }
                bind<NotificationManagerCompat>().toProviderInstance {
                    NotificationManagerCompat.from(
                        this@App
                    )
                }
            }).inject(this)

        if (appSettings.serviceEnabled) {
            serviceHelper.startObserveDeviceUsage()
        }
    }

}