package ch.bretscherhochstrasser.cleanme.about

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ch.bretscherhochstrasser.cleanme.BuildConfig
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

        binding.buttonFeedback.setOnClickListener { feedbackHelper.sendFeedbackEmail() }
    }

    private fun goToWebsite() {
        val websiteIntent = Intent(Intent.ACTION_VIEW)
        websiteIntent.data = Uri.parse(WEB_SITE_URL)
        startActivity(websiteIntent)
    }
}
