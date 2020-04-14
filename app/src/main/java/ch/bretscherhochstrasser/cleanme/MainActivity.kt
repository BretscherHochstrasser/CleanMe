package ch.bretscherhochstrasser.cleanme

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import ch.bretscherhochstrasser.cleanme.service.CleanMeService

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_OVERLAY_SETTINGS = 2345
    }

    private var settings = AppSettings(this)

    private val enableOverlaySwitch: Switch?
        get() = findViewById(R.id.switch_overlay_enabled)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button_start_service).setOnClickListener {
            invokeService(CleanMeService.ACTION_START_OBSERVE_DEVICE_STATE)
        }
        findViewById<Button>(R.id.button_stop_service).setOnClickListener {
            invokeService(CleanMeService.ACTION_STOP_OBSERVE_DEVICE_STATE)
        }
        findViewById<Button>(R.id.button_reset_stats).setOnClickListener {
            invokeService(CleanMeService.ACTION_RESET_USAGE_STATS)
        }
        enableOverlaySwitch?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkDrawOverlayPermission { settings.overlayEnabled = true }
            } else {
                settings.overlayEnabled = false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        enableOverlaySwitch?.isChecked = settings.overlayEnabled
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
            settings.overlayEnabled = canDrawOverlays
            enableOverlaySwitch?.isChecked = canDrawOverlays
        }
    }

}