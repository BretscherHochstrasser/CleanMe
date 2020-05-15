package ch.bretscherhochstrasser.cleanme

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.google.android.material.slider.Slider
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

/**
 * Checks if image view has same drawable as the one with the given resource ID.
 */
fun withDrawable(@DrawableRes id: Int) = object : TypeSafeMatcher<View>() {

    override fun describeTo(description: Description) {
        description.appendText("ImageView with same drawable as with id $id")
    }

    override fun matchesSafely(view: View): Boolean {
        if (view is ImageView) {
            val context = view.context
            val expectedDrawable = context.getDrawable(id) ?: return false
            val expectedBitmap = getBitmap(expectedDrawable)
            val actualBitmap = getBitmap(view.drawable)
            return actualBitmap.sameAs(expectedBitmap)
        } else {
            return false
        }
    }

    private fun getBitmap(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}

/**
 * Checks if the slider has the given value
 */
fun withSliderValue(value: Float) = object : TypeSafeMatcher<View>() {

    override fun describeTo(description: Description) {
        description.appendText("Material Slider with value $value")
    }

    override fun matchesSafely(view: View): Boolean {
        return view is Slider && view.value == value
    }
}

fun withFormattedText(@StringRes resId: Int, vararg formatArgs: Any): Matcher<View> {
    val appContext = ApplicationProvider.getApplicationContext<App>()
    val expectedText = appContext.getString(resId, *formatArgs)
    return withText(expectedText)
}

fun chooser(intentMatcher: Matcher<Intent>): Matcher<Intent> {
    return allOf(
        hasAction(Intent.ACTION_CHOOSER),
        hasExtra(`is`(Intent.EXTRA_INTENT), intentMatcher)
    )
}

fun getString(@StringRes resId: Int, vararg formatArgs: Any): String {
    val appContext = ApplicationProvider.getApplicationContext<App>()
    return appContext.getString(resId, *formatArgs)
}