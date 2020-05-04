package ch.bretscherhochstrasser.cleanme

import android.content.Intent
import android.os.Bundle
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import ch.bretscherhochstrasser.cleanme.annotation.ApplicationScope
import ch.bretscherhochstrasser.cleanme.databinding.ActivityMainBinding
import ch.bretscherhochstrasser.cleanme.deviceusage.DeviceUsageStats
import ch.bretscherhochstrasser.cleanme.deviceusage.DeviceUsageStatsManager
import ch.bretscherhochstrasser.cleanme.helper.formatCountdownHoursAndMinutes
import ch.bretscherhochstrasser.cleanme.service.ReminderManager
import ch.bretscherhochstrasser.cleanme.settings.AppSettings
import ch.bretscherhochstrasser.cleanme.settings.SettingsActivity
import ch.bretscherhochstrasser.cleanme.welcome.WelcomeWizardActivity
import com.stephentuso.welcome.WelcomeHelper
import toothpick.ktp.KTP
import toothpick.ktp.delegate.inject
import toothpick.smoothie.lifecycle.closeOnDestroy

class MainActivity : AppCompatActivity() {

    private val deviceUsageStatsManager: DeviceUsageStatsManager by inject()
    private val reminderManager: ReminderManager by inject()
    private val appSettings: AppSettings by inject()

    private val welcomeScreen = WelcomeHelper(this, WelcomeWizardActivity::class.java)

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        KTP.openScopes(ApplicationScope::class.java, this)
            .closeOnDestroy(this)
            .inject(this)

        welcomeScreen.show(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        deviceUsageStatsManager.deviceUsageStats.observe(this, Observer {
            onUpdateDeviceStats(it)
        })

        binding.buttonReset.setOnClickListener {
            deviceUsageStatsManager.resetStats()
            reminderManager.reset()
        }

        binding.buttonSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        welcomeScreen.onSaveInstanceState(outState)
    }

    private fun onUpdateDeviceStats(stats: DeviceUsageStats) {
        val timeUntilNextClean = appSettings.cleanInterval.durationMillis - stats.deviceUseDuration
        binding.textTimeUntilClean.text = formatCountdownHoursAndMinutes(timeUntilNextClean)

        val remainingPercent =
            (timeUntilNextClean.toFloat() / appSettings.cleanInterval.durationMillis * 100)
                .coerceAtMost(100f)

        val waveProgress = (100 - remainingPercent.toInt()).coerceAtLeast(0)
        binding.progressHourglass.setProgress(waveProgress)

        if (remainingPercent > 0) {
            binding.progressCircle.setProgressWithAnimation(remainingPercent, 750L)
            binding.progressCircle.backgroundProgressBarColor =
                getCompatColor(android.R.color.transparent)
            binding.labelTimeUntilClean.setText(R.string.main_label_time_until_clean)
            binding.progressHourglass.setAmplitudeRatio(0.03f)
        } else {
            binding.progressCircle.progress = 0f
            binding.progressCircle.backgroundProgressBarColor =
                getCompatColor(R.color.errorColor)
            binding.labelTimeUntilClean.setText(R.string.main_label_time_since_interval)
            binding.progressHourglass.setAmplitudeRatio(0f)
        }

        if (remainingPercent < 50) {
            binding.textTimeUntilClean.setTextColor(getCompatColor(R.color.onPrimaryColor))
        } else {
            binding.textTimeUntilClean.setTextColor(getCompatColor(R.color.onSecondaryColor))
        }

        if (remainingPercent < 25) {
            binding.labelTimeUntilClean.setTextColor(getCompatColor(R.color.onPrimaryColor))
        } else {
            binding.labelTimeUntilClean.setTextColor(getCompatColor(R.color.onSecondaryColor))
        }
    }

    private fun getCompatColor(@ColorRes color: Int): Int {
        return ContextCompat.getColor(this, color)
    }
}