package ch.bretscherhochstrasser.cleanme.helper

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import ch.bretscherhochstrasser.cleanme.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import toothpick.InjectConstructor

@InjectConstructor
class OverlayPermissionHelper(
    private val activity: AppCompatActivity,
    private val permissionWrapper: OverlayPermissionWrapper
) {

    private val overlaySettings = activity.registerForActivityResult(object : ActivityResultContract<Void?, Void?>() {
        @RequiresApi(Build.VERSION_CODES.M)
        override fun createIntent(context: Context, input: Void?): Intent {
            // construct intent to request permission
            return Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            )
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Void? {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // if so check once again if we have permission
                if (permissionWrapper.canDrawOverlay) {
                    onPermissionGranted?.invoke()
                } else {
                    onPermissionDenied?.invoke()
                }
            }
            return null
        }
    }) {}

    var onPermissionGranted: (() -> Unit)? = null
    var onPermissionDenied: (() -> Unit)? = null

    fun checkDrawOverlayPermission() {
        // check if we already  have permission to draw over other apps
        if (permissionWrapper.canDrawOverlay) {
            onPermissionGranted?.invoke()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // if not show the info dialog
            showPermissionInfoDialog()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showPermissionInfoDialog() {
        MaterialAlertDialogBuilder(activity)
            .setTitle(R.string.settings_dialog_overlay_permission_info_title)
            .setMessage(R.string.settings_dialog_overlay_permission_info_message)
            .setPositiveButton(R.string.settings_dialog_overlay_permission_info_open_settings_button) { _: DialogInterface, _: Int ->
                openDeviceOverlaySettings()
            }
            .setNegativeButton(android.R.string.cancel) { _: DialogInterface, _: Int ->
                onPermissionDenied?.invoke()
            }
            .setOnCancelListener { onPermissionDenied?.invoke() }
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun openDeviceOverlaySettings() {
        overlaySettings.launch(null)
    }

}