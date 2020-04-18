package ch.bretscherhochstrasser.cleanme

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import ch.bretscherhochstrasser.cleanme.annotation.ApplicationScope
import ch.bretscherhochstrasser.cleanme.databinding.ActivityMainBinding
import ch.bretscherhochstrasser.cleanme.deviceusage.DeviceUsageStatsManager
import ch.bretscherhochstrasser.cleanme.helper.formatHoursAndMinutes
import ch.bretscherhochstrasser.cleanme.settings.SettingsActivity
import toothpick.ktp.KTP
import toothpick.ktp.delegate.inject
import toothpick.smoothie.lifecycle.closeOnDestroy

class MainActivity : AppCompatActivity() {

    private val deviceUsageStatsManager: DeviceUsageStatsManager by inject()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        KTP.openScopes(ApplicationScope::class.java, this)
            .closeOnDestroy(this)
            .inject(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        deviceUsageStatsManager.deviceUsageStats.observe(this, Observer {
            binding.textUseTime.text = formatHoursAndMinutes(it.deviceUseDuration)
        })

        binding.buttonResetStats.setOnClickListener {
            deviceUsageStatsManager.resetStats()
        }

        binding.buttonSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

}