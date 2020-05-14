package ch.bretscherhochstrasser.cleanme.about

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ch.bretscherhochstrasser.cleanme.BuildConfig
import ch.bretscherhochstrasser.cleanme.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.labelAppVersion.text = BuildConfig.VERSION_NAME
    }
}
