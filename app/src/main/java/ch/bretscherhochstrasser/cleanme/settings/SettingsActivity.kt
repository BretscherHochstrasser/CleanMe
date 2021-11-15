package ch.bretscherhochstrasser.cleanme.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import ch.bretscherhochstrasser.cleanme.R
import ch.bretscherhochstrasser.cleanme.annotation.ApplicationScope
import ch.bretscherhochstrasser.cleanme.databinding.ActivitySettingsBinding
import ch.bretscherhochstrasser.cleanme.deviceusage.DeviceUsageStatsManager
import ch.bretscherhochstrasser.cleanme.helper.OverlayPermissionHelper
import ch.bretscherhochstrasser.cleanme.helper.OverlayPermissionWrapper
import ch.bretscherhochstrasser.cleanme.helper.formatCleanInterval
import ch.bretscherhochstrasser.cleanme.overlay.ParticleGrowthModel
import ch.bretscherhochstrasser.cleanme.service.ServiceHelper
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.slider.Slider
import com.google.android.material.switchmaterial.SwitchMaterial
import mobi.upod.timedurationpicker.TimeDurationPicker
import mobi.upod.timedurationpicker.TimeDurationPickerDialog
import org.threeten.bp.Duration
import toothpick.ktp.KTP
import toothpick.ktp.binding.bind
import toothpick.ktp.binding.module
import toothpick.ktp.delegate.inject
import toothpick.smoothie.lifecycle.closeOnDestroy

class SettingsActivity : AppCompatActivity() {

    private val serviceHelper: ServiceHelper by inject<ServiceHelper>()
    private val overlayPermissionHelper: OverlayPermissionHelper by inject<OverlayPermissionHelper>()
    private val overlayPermissionWrapper: OverlayPermissionWrapper by inject<OverlayPermissionWrapper>()
    private val appSettings: AppSettings by inject<AppSettings>()
    private val usageStatsManager: DeviceUsageStatsManager by inject<DeviceUsageStatsManager>()

    private lateinit var binding: ActivitySettingsBinding

    private lateinit var switchTrackDeviceUsageListener: CompoundButton.OnCheckedChangeListener
    private lateinit var switchOverlayEnabledListener: CompoundButton.OnCheckedChangeListener
    private lateinit var switchStartOnBootListener: CompoundButton.OnCheckedChangeListener
    private lateinit var buttonGroupGrowthModelListener: MaterialButtonToggleGroup.OnButtonCheckedListener

    private val overlaySwitchEnabled: Boolean
        get() {
            return appSettings.overlayEnabled && overlayPermissionWrapper.canDrawOverlay
        }

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

        switchTrackDeviceUsageListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
            appSettings.serviceEnabled = isChecked
            setDependentSettingsEnabled(isChecked, overlaySwitchEnabled)
            if (isChecked) {
                serviceHelper.startObserveDeviceUsage()
            } else {
                serviceHelper.stopObserveDeviceUsage()
            }
        }
        binding.switchTrackDeviceUsage.setOnCheckedChangeListener(switchTrackDeviceUsageListener)

        switchStartOnBootListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
            appSettings.startOnBoot = isChecked
        }
        binding.switchStartOnBoot.setOnCheckedChangeListener(switchStartOnBootListener)

        binding.buttonEditCleanInterval.setOnClickListener {
            TimeDurationPickerDialog(
                this,
                { _: TimeDurationPicker, interval: Long ->
                    val newCleanInterval = Duration.ofMillis(interval)
                    appSettings.cleanInterval = newCleanInterval
                    setCleanIntervalLabel(newCleanInterval)
                    triggerOverlayRefresh()
                },
                appSettings.cleanInterval.toMillis(),
                TimeDurationPicker.HH_MM
            ).show()
        }

        if (appSettings.overlaySupported) {
            binding.descriptionOverlayNotSupported.visibility = View.GONE
            setAllOverlaySettingsVisibility(View.VISIBLE)
        } else {
            binding.descriptionOverlayNotSupported.visibility = View.VISIBLE
            setAllOverlaySettingsVisibility(View.GONE)
        }

        switchOverlayEnabledListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                overlayPermissionHelper.checkDrawOverlayPermission()
            } else {
                appSettings.overlayEnabled = false
                setDependentSettingsEnabled(appSettings.serviceEnabled, false)
                triggerOverlayRefresh()
            }
        }
        binding.switchOverlayEnabled.setOnCheckedChangeListener(switchOverlayEnabledListener)
        overlayPermissionHelper.onPermissionGranted = {
            appSettings.overlayEnabled = true
            setDependentSettingsEnabled(appSettings.serviceEnabled, true)
            triggerOverlayRefresh()
        }
        overlayPermissionHelper.onPermissionDenied = {
            setCheckedWithDisabledListener(
                binding.switchOverlayEnabled,
                false,
                switchOverlayEnabledListener
            )
            appSettings.overlayEnabled = false
            setDependentSettingsEnabled(appSettings.serviceEnabled, false)
            triggerOverlayRefresh()
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
                triggerOverlayRefresh()
            }
        })
        binding.sliderMaxOverlayParticles.setLabelFormatter {
            it.toInt().toString()
        }

        buttonGroupGrowthModelListener =
            MaterialButtonToggleGroup.OnButtonCheckedListener { _, checkedId, isChecked ->
                if (isChecked) {
                    when (checkedId) {
                        R.id.button_growth_model_linear -> appSettings.overlayParticleGrowthModel =
                            ParticleGrowthModel.LINEAR
                        R.id.button_growth_model_exponential -> appSettings.overlayParticleGrowthModel =
                            ParticleGrowthModel.EXPONENTIAL
                    }
                    triggerOverlayRefresh()
                }
            }
        binding.buttonGroupGrowthModel.addOnButtonCheckedListener(buttonGroupGrowthModelListener)

        binding.sliderParticleSize.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                setParticleSizeLabel(value.toInt())
            }
        }
        binding.sliderParticleSize.addOnSliderTouchListener(object :
            Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {}

            override fun onStopTrackingTouch(slider: Slider) {
                appSettings.overlayParticleSize = slider.value.toInt()
                triggerOverlayRefresh()
            }

        })
        binding.sliderParticleSize.setLabelFormatter {
            "${it.toInt()}"
        }

        binding.sliderParticleTransparency.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                setParticleTransparencyLabel(value.toInt())
            }
        }
        binding.sliderParticleTransparency.addOnSliderTouchListener(object :
            Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {}

            override fun onStopTrackingTouch(slider: Slider) {
                appSettings.overlayParticleTransparency = slider.value.toInt()
                triggerOverlayRefresh()
            }
        })
        binding.sliderParticleTransparency.setLabelFormatter {
            "${it.toInt()}%"
        }
    }

    override fun onResume() {
        super.onResume()
        setCheckedWithDisabledListener(
            binding.switchTrackDeviceUsage,
            appSettings.serviceEnabled,
            switchTrackDeviceUsageListener
        )
        setCheckedWithDisabledListener(
            binding.switchOverlayEnabled,
            overlaySwitchEnabled,
            switchOverlayEnabledListener
        )
        setCheckedWithDisabledListener(
            binding.switchStartOnBoot,
            appSettings.startOnBoot,
            switchStartOnBootListener
        )
        setCleanIntervalLabel(appSettings.cleanInterval)
        binding.sliderMaxOverlayParticles.value = appSettings.maxOverlayParticleCount.toFloat()
        setMaxParticleLabel(appSettings.maxOverlayParticleCount)
        val growthModelCheckedButtonId =
            when (appSettings.overlayParticleGrowthModel) {
                ParticleGrowthModel.LINEAR -> R.id.button_growth_model_linear
                else -> R.id.button_growth_model_exponential
            }
        setCheckedWithDisabledListener(
            binding.buttonGroupGrowthModel,
            growthModelCheckedButtonId,
            buttonGroupGrowthModelListener
        )
        binding.sliderParticleSize.value = appSettings.overlayParticleSize.toFloat()
        setParticleSizeLabel(appSettings.overlayParticleSize)
        binding.sliderParticleTransparency.value = appSettings.overlayParticleTransparency.toFloat()
        setParticleTransparencyLabel(appSettings.overlayParticleTransparency)
        setDependentSettingsEnabled(appSettings.serviceEnabled, overlaySwitchEnabled)
    }

    /**
     * Helper to set a switch checked state without triggering the OnCheckedChanged listener.
     */
    private fun setCheckedWithDisabledListener(
        switchMaterial: SwitchMaterial,
        isChecked: Boolean,
        listener: CompoundButton.OnCheckedChangeListener
    ) {
        switchMaterial.setOnCheckedChangeListener(null) // remove listener
        switchMaterial.isChecked = isChecked // apply value
        switchMaterial.setOnCheckedChangeListener(listener) // set listener again
    }

    private fun setCheckedWithDisabledListener(
        buttonToggleGroup: MaterialButtonToggleGroup,
        @IdRes checkedId: Int,
        listener: MaterialButtonToggleGroup.OnButtonCheckedListener
    ) {
        buttonToggleGroup.removeOnButtonCheckedListener(listener)
        buttonToggleGroup.check(checkedId)
        buttonToggleGroup.addOnButtonCheckedListener(listener)
    }

    private fun setDependentSettingsEnabled(serviceEnabled: Boolean, overlayEnabled: Boolean) {
        binding.switchStartOnBoot.isEnabled = serviceEnabled
        binding.buttonEditCleanInterval.isEnabled = serviceEnabled
        binding.switchOverlayEnabled.isEnabled = serviceEnabled
        binding.sliderMaxOverlayParticles.isEnabled = serviceEnabled && overlayEnabled
        binding.buttonGrowthModelLinear.isEnabled = serviceEnabled && overlayEnabled
        binding.buttonGrowthModelExponential.isEnabled = serviceEnabled && overlayEnabled
        binding.sliderParticleSize.isEnabled = serviceEnabled && overlayEnabled
        binding.sliderParticleTransparency.isEnabled = serviceEnabled && overlayEnabled
    }

    private fun setAllOverlaySettingsVisibility(visibility: Int) {
        binding.switchOverlayEnabled.visibility = visibility
        binding.descriptionEnableOverlay.visibility = visibility
        binding.labelMaxOverlayParticles.visibility = visibility
        binding.sliderMaxOverlayParticles.visibility = visibility
        binding.descriptionMaxOverlayParticles.visibility = visibility
        binding.labelParticleGrowthModel.visibility = visibility
        binding.buttonGroupGrowthModel.visibility = visibility
        binding.descriptionParticleGrowthModel.visibility = visibility
        binding.labelParticleSize.visibility = visibility
        binding.sliderParticleSize.visibility = visibility
        binding.descriptionParticleSize.visibility = visibility
        binding.labelParticleTransparency.visibility = visibility
        binding.sliderParticleTransparency.visibility = visibility
        binding.descriptionParticleTransparency.visibility = visibility
    }

    private fun triggerOverlayRefresh() {
        usageStatsManager.updateUsageStats()
    }

    private fun setCleanIntervalLabel(cleanInterval: Duration) {
        binding.labelCleanInterval.text =
            getString(
                R.string.settings_label_clean_interval,
                formatCleanInterval(cleanInterval, this)
            )
    }

    private fun setMaxParticleLabel(maxParticleCount: Int) {
        binding.labelMaxOverlayParticles.text =
            getString(R.string.settings_label_max_particles, maxParticleCount)
    }

    private fun setParticleSizeLabel(particleSize: Int) {
        binding.labelParticleSize.text =
            getString(R.string.settings_label_particle_size, particleSize)
    }

    private fun setParticleTransparencyLabel(particleTransparency: Int) {
        binding.labelParticleTransparency.text =
            getString(R.string.settings_label_particle_transparency, particleTransparency)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        overlayPermissionHelper.onActivityResult(requestCode)
    }

}
