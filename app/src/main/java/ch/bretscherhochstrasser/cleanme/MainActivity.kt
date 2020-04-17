package ch.bretscherhochstrasser.cleanme

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import ch.bretscherhochstrasser.cleanme.helper.formatHoursAndMinutes
import ch.bretscherhochstrasser.cleanme.service.CleanMeService

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_OVERLAY_SETTINGS = 2345
    }

    private val useTime: TextView?
        get() = findViewById(R.id.text_use_time)

    private val enableObserverSwitch: Switch?
        get() = findViewById(R.id.switch_observer_enabled)

    private val enableOverlaySwitch: Switch?
        get() = findViewById(R.id.switch_overlay_enabled)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        serviceState.observingDeviceUsage.observe(this, Observer {
            enableObserverSwitch?.isChecked = it
        })

        deviceUsageStatsManager.deviceUsageStats.observe(this, Observer {
            useTime?.text = formatHoursAndMinutes(it.deviceUseDuration)
        })

        findViewById<Button>(R.id.button_reset_stats).setOnClickListener {
            deviceUsageStatsManager.resetStats()
        }

        enableObserverSwitch?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                invokeService(CleanMeService.ACTION_START_OBSERVE_DEVICE_STATE)
            } else {
                invokeService(CleanMeService.ACTION_STOP_OBSERVE_DEVICE_STATE)
            }
        }

        enableOverlaySwitch?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkDrawOverlayPermission {
                    appSettings.overlayEnabled = true
                    invokeService(CleanMeService.ACTION_REFRESH_OVERLAY)
                }
            } else {
                appSettings.overlayEnabled = false
                invokeService(CleanMeService.ACTION_REFRESH_OVERLAY)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        enableOverlaySwitch?.isChecked = appSettings.overlayEnabled
    }

    private fun invokeService(action: String) {
        val intent = Intent(this, CleanMeService::class.java)
        intent.action = action
        startService(intent)
    }

    private fun checkDrawOverlayPermission(onPermissionGranted: () -> Unit) {
        // check if we already  have permission to draw over other apps
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
            Settings.canDrawOverlays(this)
        ) {
            onPermissionGranted()
        } else {
            // if not construct intent to request permission
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            // request permission via start activity for result
            startActivityForResult(intent, REQUEST_CODE_OVERLAY_SETTINGS)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // check if received result code
        // is equal our requested code for draw permission
        if (requestCode == REQUEST_CODE_OVERLAY_SETTINGS && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // if so check once again if we have permission
            val canDrawOverlays = Settings.canDrawOverlays(this)
            appSettings.overlayEnabled = canDrawOverlays
            enableOverlaySwitch?.isChecked = canDrawOverlays
            invokeService(CleanMeService.ACTION_REFRESH_OVERLAY)
        }
    }

}