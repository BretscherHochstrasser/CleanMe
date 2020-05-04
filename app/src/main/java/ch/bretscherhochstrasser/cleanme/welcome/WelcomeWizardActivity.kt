package ch.bretscherhochstrasser.cleanme.welcome

import ch.bretscherhochstrasser.cleanme.R
import com.stephentuso.welcome.BasicPage
import com.stephentuso.welcome.WelcomeActivity
import com.stephentuso.welcome.WelcomeConfiguration

class WelcomeWizardActivity : WelcomeActivity() {

    override fun configuration(): WelcomeConfiguration {
        return WelcomeConfiguration.Builder(this)
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
            .page(
                BasicPage(
                    R.drawable.welcome_overlay,
                    getString(R.string.welcome_overlay_title),
                    getString(R.string.welcome_overlay_text)
                )
            )
            .build()
    }

}
