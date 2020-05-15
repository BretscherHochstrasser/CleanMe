package ch.bretscherhochstrasser.cleanme.helper

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.widget.Toast
import ch.bretscherhochstrasser.cleanme.BuildConfig
import ch.bretscherhochstrasser.cleanme.R
import toothpick.InjectConstructor

/**
 * Helper to send a feedback email.
 */
@InjectConstructor
class FeedbackHelper(private val activity: Activity) {

    companion object {
        private const val EMAIL_ADDRESS = "bretscherhochstrasser@gmail.com"
    }

    fun sendFeedbackEmail() {
        val subject = activity.getString(R.string.feedback_subject)
        val body = "\n\n\n\n\n${deviceInfo}"

        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.putExtra(
            Intent.EXTRA_EMAIL,
            arrayOf(EMAIL_ADDRESS)
        )
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        emailIntent.putExtra(Intent.EXTRA_TEXT, body)
        emailIntent.type = "message/rfc822"
        try {
            activity.startActivity(
                Intent.createChooser(
                    emailIntent,
                    activity.getText(R.string.feedback_choose_email_app)
                )
            )
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                activity,
                R.string.feedback_no_email_app,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private val deviceInfo: String = """
            Device Information:
            Manufacturer: ${Build.MANUFACTURER}
            Model: ${Build.MODEL}
            Device: ${Build.DEVICE}
            Android API: ${Build.VERSION.SDK_INT}
            App version: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})
            """.trimIndent()

}
