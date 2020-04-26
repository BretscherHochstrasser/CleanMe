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
import toothpick.ktp.KTP
import toothpick.ktp.delegate.inject
import toothpick.smoothie.lifecycle.closeOnDestroy

class MainActivity : AppCompatActivity() {

    private val deviceUsageStatsManager: DeviceUsageStatsManager by inject()
    private val reminderManager: ReminderManager by inject()
    private val appSettings: AppSettings by inject()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        KTP.openScopes(ApplicationScope::class.java, this)
            .closeOnDestroy(this)
            .inject(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        deviceUsageStatsManager.deviceUsageStats.observe(this, Observer {
            onUpdateDeviceStats(it)
        })

        binding.buttonResetStats.setOnClickListener {
            deviceUsageStatsManager.resetStats()
            reminderManager.reset()
        }

        binding.buttonSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun onUpdateDeviceStats(stats: DeviceUsageStats) {
        val timeUntilNextClean = appSettings.cleanInterval.durationMillis - stats.deviceUseDuration
        binding.textTimeUntilClean.text = formatCountdownHoursAndMinutes(timeUntilNextClean)

        val remainingPercent =
            (timeUntilNextClean.toFloat() / appSettings.cleanInterval.durationMillis * 100)
                .coerceAtMost(100f)

        val waveProgress = (100 - remainingPercent.toInt()).coerceAtLeast(0)
        binding.progressWaveTimeUntilClean.setProgress(waveProgress)

        if (remainingPercent > 0) {
            binding.progressCircleTimeUntilClean.setProgressWithAnimation(remainingPercent, 750L)
            binding.progressCircleTimeUntilClean.progressBarColor =
                getCompatColor(R.color.secondaryColor)
            binding.labelTimeUntilClean.setText(R.string.main_label_time_until_clean)
            binding.progressWaveTimeUntilClean.setAmplitudeRatio(0.03f)
        } else {
            binding.progressCircleTimeUntilClean.progress = 100f
            binding.progressCircleTimeUntilClean.progressBarColor =
                getCompatColor(R.color.design_default_color_error)
            binding.labelTimeUntilClean.setText(R.string.main_label_time_since_interval)
            binding.progressWaveTimeUntilClean.setAmplitudeRatio(0f)
        }
    }

    private fun getCompatColor(@ColorRes color: Int): Int {
        return ContextCompat.getColor(this, color)
    }
}