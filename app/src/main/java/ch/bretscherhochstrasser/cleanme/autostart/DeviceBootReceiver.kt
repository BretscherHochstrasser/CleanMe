package ch.bretscherhochstrasser.cleanme.autostart

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_BOOT_COMPLETED
import android.content.Intent.ACTION_MY_PACKAGE_REPLACED
import ch.bretscherhochstrasser.cleanme.annotation.ApplicationScope
import ch.bretscherhochstrasser.cleanme.service.CleanMeService
import ch.bretscherhochstrasser.cleanme.settings.AppSettings
import timber.log.Timber
import toothpick.ktp.KTP
import toothpick.ktp.delegate.inject

class DeviceBootReceiver : BroadcastReceiver() {

    private val appSettings: AppSettings by inject()

    override fun onReceive(context: Context, intent: Intent) {
        KTP.openScopes(ApplicationScope::class.java, this)
            .inject(this)

        when (intent.action) {
            ACTION_BOOT_COMPLETED -> {
                Timber.d("Received device boot broadcast")
                autoStartServiceIfNeeded(context)
            }
            ACTION_MY_PACKAGE_REPLACED -> {
                Timber.d("Received app update broadcast")
                autoStartServiceIfNeeded(context)
            }
        }
    }

    private fun autoStartServiceIfNeeded(context: Context) {
        if (appSettings.startOnBoot) {
            Timber.d("Auto starting service")
            CleanMeService.start(context)
        }
    }
}
