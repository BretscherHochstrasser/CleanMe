package ch.bretscherhochstrasser.cleanme.helper

import android.content.Context
import android.os.Build
import android.provider.Settings
import ch.bretscherhochstrasser.cleanme.annotation.AppContext
import toothpick.InjectConstructor

/**
 * Wrapper to read the draw overlay permission to allow mocking in tests
 */
@InjectConstructor
class OverlayPermissionWrapper(@AppContext private val context: Context) {

    val canDrawOverlay: Boolean
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Settings.canDrawOverlays(context)
            } else {
                true
            }
        }

}