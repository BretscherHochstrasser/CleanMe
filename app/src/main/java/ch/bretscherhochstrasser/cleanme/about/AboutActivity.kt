package ch.bretscherhochstrasser.cleanme.about

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ch.bretscherhochstrasser.cleanme.BuildConfig
import ch.bretscherhochstrasser.cleanme.R
import ch.bretscherhochstrasser.cleanme.annotation.ApplicationScope
import ch.bretscherhochstrasser.cleanme.databinding.ActivityAboutBinding
import toothpick.ktp.KTP
import toothpick.smoothie.lifecycle.closeOnDestroy

class AboutActivity : AppCompatActivity() {

    companion object {
        private const val WEB_SITE_URL = "https://bretscherhochstrasser.ch"
        private const val PLAY_STORE_URL =
            "https://play.google.com/store/apps/details?id=ch.bretscherhochstrasser.cleanme"
        private const val EMAIL_ADDRESS = "bretscherhochstrasser@gmail.com"
    }

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        KTP.openScopes(ApplicationScope::class, this)
            .closeOnDestroy(this)
            .inject(this)

        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.labelAppVersion.text = BuildConfig.VERSION_NAME

        binding.imageDeveloperLogo.setOnClickListener { goToWebsite() }

        binding.buttonShare.setOnClickListener { shareAppLink() }

        binding.buttonFeedback.setOnClickListener { sendFeedbackEmail() }
    }

    private fun goToWebsite() {
        val websiteIntent = Intent(Intent.ACTION_VIEW)
        websiteIntent.data = Uri.parse(WEB_SITE_URL)
        startActivity(websiteIntent)
    }

    private fun shareAppLink() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.about_share_subject))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.about_share_text, PLAY_STORE_URL))
            type = "text/plain"
        }
        startActivity(
            Intent.createChooser(
                shareIntent,
                getString(R.string.about_share_chooser_title)
            )
        )
    }

    private fun sendFeedbackEmail() {
        val subject = getString(R.string.about_feedback_subject)
        val body = "\n\n\n\n\n${deviceInfo}"

        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(EMAIL_ADDRESS))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        try {
            startActivity(
                Intent.createChooser(
                    emailIntent,
                    getText(R.string.about_feedback_choose_email_app)
                )
            )
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                this,
                R.string.about_feedback_no_email_app,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private val deviceInfo: String = """
            Device Information:
            Manufacturer: ${Build.MANUFACTURER}
            Model: ${Build.MODEL}
            Device: ${Build.DEVICE}
            Android API: ${Build.VERSION.SDK_INT}
            App version: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})
            """.trimIndent()

}
