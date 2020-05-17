package ch.bretscherhochstrasser.cleanme.about

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ch.bretscherhochstrasser.cleanme.BuildConfig
import ch.bretscherhochstrasser.cleanme.R
import ch.bretscherhochstrasser.cleanme.annotation.ApplicationScope
import ch.bretscherhochstrasser.cleanme.databinding.ActivityAboutBinding
import ch.bretscherhochstrasser.cleanme.helper.FeedbackHelper
import toothpick.ktp.KTP
import toothpick.ktp.binding.bind
import toothpick.ktp.binding.module
import toothpick.ktp.delegate.inject
import toothpick.smoothie.lifecycle.closeOnDestroy

class AboutActivity : AppCompatActivity() {

    companion object {
        private const val WEB_SITE_URL = "https://bretscherhochstrasser.ch"
        private const val PLAY_STORE_URL =
            "https://play.google.com/store/apps/details?id=ch.bretscherhochstrasser.cleanme"
    }

    private val feedbackHelper: FeedbackHelper by inject<FeedbackHelper>()

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        KTP.openScopes(ApplicationScope::class, this)
            .installModules(module {
                bind(Activity::class).toInstance(this@AboutActivity)
            })
            .closeOnDestroy(this)
            .inject(this)

        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.labelAppVersion.text = BuildConfig.VERSION_NAME

        binding.imageDeveloperLogo.setOnClickListener { goToWebsite() }

        binding.buttonShare.setOnClickListener { shareAppLink() }

        binding.buttonFeedback.setOnClickListener { feedbackHelper.sendFeedbackEmail() }
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
}
