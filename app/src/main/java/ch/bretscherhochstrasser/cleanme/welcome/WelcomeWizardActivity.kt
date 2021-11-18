package ch.bretscherhochstrasser.cleanme.welcome

import android.app.Activity
import android.os.Bundle
import ch.bretscherhochstrasser.cleanme.R
import ch.bretscherhochstrasser.cleanme.annotation.ApplicationScope
import ch.bretscherhochstrasser.cleanme.settings.AppSettings
import com.stephentuso.welcome.BasicPage
import com.stephentuso.welcome.WelcomeActivity
import com.stephentuso.welcome.WelcomeConfiguration
import toothpick.ktp.KTP
import toothpick.ktp.binding.bind
import toothpick.ktp.binding.module
import toothpick.ktp.delegate.inject
import toothpick.smoothie.lifecycle.closeOnDestroy

class WelcomeWizardActivity : WelcomeActivity() {

    private val appSettings: AppSettings by inject<AppSettings>()

    override fun onCreate(savedInstanceState: Bundle?) {
        KTP.openScopes(ApplicationScope::class.java, this)
            .installModules(module {
                bind(Activity::class).toInstance(this@WelcomeWizardActivity)
            })
            .closeOnDestroy(this)
            .inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun configuration(): WelcomeConfiguration {
        val configBuilder = WelcomeConfiguration.Builder(this)
            .defaultBackgroundColor(R.color.primaryColor)
            .page(
                BasicPage(
                    R.drawable.welcome_intro,
                    getString(R.string.welcome_intro_title),
                    getString(R.string.welcome_intro_text)
                )
            )
            .page(
                BasicPage(
                    R.drawable.welcome_track_use,
                    getString(R.string.welcome_track_use_title),
                    getString(R.string.welcome_track_use_text)
                )
            )
            .page(
                BasicPage(
                    R.drawable.welcome_clean_reminder,
                    getString(R.string.welcome_clean_reminder_title),
                    getString(R.string.welcome_clean_reminder_text)
                )
            )
            .page(
                BasicPage(
                    R.drawable.welcome_clean_and_reset,
                    getString(R.string.welcome_clean_and_reset_title),
                    getString(R.string.welcome_clean_and_reset_text)
                )
            )
        if (appSettings.overlaySupported) {
            configBuilder.page(
                BasicPage(
                    R.drawable.welcome_overlay,
                    getString(R.string.welcome_overlay_title),
                    getString(R.string.welcome_overlay_text)
                )
            )
        }
        return configBuilder.build()
    }

}
