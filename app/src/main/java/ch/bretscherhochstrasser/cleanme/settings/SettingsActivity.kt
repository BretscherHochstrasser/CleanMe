package ch.bretscherhochstrasser.cleanme.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import ch.bretscherhochstrasser.cleanme.R
import ch.bretscherhochstrasser.cleanme.annotation.ApplicationScope
import ch.bretscherhochstrasser.cleanme.databinding.ActivitySettingsBinding
import ch.bretscherhochstrasser.cleanme.helper.OverlayPermissionHelper
import ch.bretscherhochstrasser.cleanme.service.CleanMeService
import ch.bretscherhochstrasser.cleanme.service.ServiceState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import toothpick.ktp.KTP
import toothpick.ktp.binding.bind
import toothpick.ktp.binding.module
import toothpick.ktp.delegate.inject
import toothpick.smoothie.lifecycle.closeOnDestroy

class SettingsActivity : AppCompatActivity() {

    private val serviceState: ServiceState by inject()
    private val overlayPermissionHelper: OverlayPermissionHelper by inject()
    private val appSettings: AppSettings by inject()

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

        serviceState.observingDeviceUsage.observe(this, Observer {
            binding.switchTrackDeviceUsage.isChecked = it
        })
        binding.switchTrackDeviceUsage.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                CleanMeService.start(this)
            } else {
                CleanMeService.stop(this)
            }
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
                CleanMeService.refresh(this)
                dialog.dismiss()
            }.show()
        }

        binding.switchOverlayEnabled.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                overlayPermissionHelper.checkDrawOverlayPermission()
            } else {
                appSettings.overlayEnabled = false
                CleanMeService.refresh(this)
            }
        }
        overlayPermissionHelper.onPermissionGranted = {
            appSettings.overlayEnabled = true
            CleanMeService.refresh(this)
        }
        overlayPermissionHelper.onPermissionDenied = {
            binding.switchOverlayEnabled.isChecked = false
            appSettings.overlayEnabled = false
            CleanMeService.refresh(this)
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
                CleanMeService.refresh(this@SettingsActivity)
            }
        })
        binding.sliderMaxOverlayParticles.setLabelFormatter {
            it.toInt().toString()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.switchOverlayEnabled.isChecked = appSettings.overlayEnabled
        setCleanIntervalLabel(appSettings.cleanInterval)
        binding.sliderMaxOverlayParticles.value = appSettings.maxOverlayParticleCount.toFloat()
        setMaxParticleLabel(appSettings.maxOverlayParticleCount)
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
