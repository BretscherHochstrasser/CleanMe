package ch.bretscherhochstrasser.cleanme.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ch.bretscherhochstrasser.cleanme.R
import ch.bretscherhochstrasser.cleanme.annotation.ApplicationScope
import ch.bretscherhochstrasser.cleanme.databinding.ActivitySettingsBinding
import ch.bretscherhochstrasser.cleanme.deviceusage.DeviceUsageStatsManager
import ch.bretscherhochstrasser.cleanme.helper.OverlayPermissionHelper
import ch.bretscherhochstrasser.cleanme.service.ServiceHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import toothpick.ktp.KTP
import toothpick.ktp.binding.bind
import toothpick.ktp.binding.module
import toothpick.ktp.delegate.inject
import toothpick.smoothie.lifecycle.closeOnDestroy

class SettingsActivity : AppCompatActivity() {

    private val serviceHelper: ServiceHelper by inject()
    private val overlayPermissionHelper: OverlayPermissionHelper by inject()
    private val appSettings: AppSettings by inject()
    private val usageStatsManager: DeviceUsageStatsManager by inject()

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        KTP.openScopes(ApplicationScope::class.java, this)
            .installModules(module {
                bind(Activity::class).toInstance(this@SettingsActivity)
            })
            .closeOnDestroy(this)
            .inject(this)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.switchTrackDeviceUsage.setOnCheckedChangeListener { _, isChecked ->
            appSettings.serviceEnabled = isChecked
            if (isChecked) {
                serviceHelper.startObserveDeviceUsage()
            } else {
                serviceHelper.stopObserveDeviceUsage()
            }
        }

        binding.switchStartOnBoot.setOnCheckedChangeListener { _, isChecked ->
            appSettings.startOnBoot = isChecked
        }

        binding.buttonEditCleanInterval.setOnClickListener {
            MaterialAlertDialogBuilder(this).setSingleChoiceItems(
                CleanInterval.getTextValues(
                    this
                ),
                appSettings.cleanInterval.ordinal
            ) { dialog, which ->
                val selectedCleanInterval = CleanInterval.values()[which]
                appSettings.cleanInterval = selectedCleanInterval
                setCleanIntervalLabel(selectedCleanInterval)
                triggerUiRefresh()
                dialog.dismiss()
            }.show()
        }

        binding.switchOverlayEnabled.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                overlayPermissionHelper.checkDrawOverlayPermission()
            } else {
                appSettings.overlayEnabled = false
                triggerUiRefresh()
            }
        }
        overlayPermissionHelper.onPermissionGranted = {
            appSettings.overlayEnabled = true
            triggerUiRefresh()
        }
        overlayPermissionHelper.onPermissionDenied = {
            binding.switchOverlayEnabled.isChecked = false
            appSettings.overlayEnabled = false
            triggerUiRefresh()
        }

        binding.sliderMaxOverlayParticles.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                setMaxParticleLabel(value.toInt())
            }
        }
        binding.sliderMaxOverlayParticles.addOnSliderTouchListener(object :
            Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {}

            override fun onStopTrackingTouch(slider: Slider) {
                appSettings.maxOverlayParticleCount = slider.value.toInt()
                triggerUiRefresh()
            }
        })
        binding.sliderMaxOverlayParticles.setLabelFormatter {
            it.toInt().toString()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.switchTrackDeviceUsage.isChecked = appSettings.serviceEnabled
        binding.switchOverlayEnabled.isChecked = appSettings.overlayEnabled
        binding.switchStartOnBoot.isChecked = appSettings.startOnBoot
        setCleanIntervalLabel(appSettings.cleanInterval)
        binding.sliderMaxOverlayParticles.value = appSettings.maxOverlayParticleCount.toFloat()
        setMaxParticleLabel(appSettings.maxOverlayParticleCount)
    }

    private fun triggerUiRefresh() {
        usageStatsManager.updateUsageStats()
    }

    private fun setCleanIntervalLabel(cleanInterval: CleanInterval) {
        binding.labelCleanInterval.text =
            getString(R.string.settings_label_clean_interval, getString(cleanInterval.text))
    }

    private fun setMaxParticleLabel(maxParticleCount: Int) {
        binding.labelMaxOverlayParticles.text =
            getString(R.string.settings_label_max_particles, maxParticleCount)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        overlayPermissionHelper.onActivityResult(requestCode)
    }

}
