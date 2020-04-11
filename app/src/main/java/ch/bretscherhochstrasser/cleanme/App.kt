package ch.bretscherhochstrasser.cleanme

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

/**
 * Application class, contains global initialization stuff
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }

}