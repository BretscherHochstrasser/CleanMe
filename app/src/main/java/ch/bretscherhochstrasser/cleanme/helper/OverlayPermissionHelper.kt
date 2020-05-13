package ch.bretscherhochstrasser.cleanme.helper

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import toothpick.InjectConstructor

@InjectConstructor
class OverlayPermissionHelper(
    private val activity: Activity,
    private val permissionWrapper: OverlayPermissionWrapper
) {

    companion object {
        private const val REQUEST_CODE_OVERLAY_SETTINGS = 2352
    }

    var onPermissionGranted: (() -> Unit)? = null
    var onPermissionDenied: (() -> Unit)? = null

    fun checkDrawOverlayPermission() {
        // check if we already  have permission to draw over other apps
        if (permissionWrapper.canDrawOverlay) {
            onPermissionGranted?.invoke()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // if not construct intent to request permission
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${activity.packageName}")
            )
            // request permission via start activity for result
            activity.startActivityForResult(intent, REQUEST_CODE_OVERLAY_SETTINGS)
        }
    }

    fun onActivityResult(requestCode: Int) {
        // check if received result code
        // is equal our requested code for draw permission
        if (requestCode == REQUEST_CODE_OVERLAY_SETTINGS && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // if so check once again if we have permission
            if (permissionWrapper.canDrawOverlay) {
                onPermissionGranted?.invoke()
            } else {
                onPermissionDenied?.invoke()
            }
        }
    }

}