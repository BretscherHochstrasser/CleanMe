package ch.bretscherhochstrasser.cleanme.overlay

import androidx.annotation.DrawableRes
import ch.bretscherhochstrasser.cleanme.R

enum class ParticleType(@DrawableRes val icon: Int) {
    GREEN(R.drawable.ic_bug_green),
    RED(R.drawable.ic_bug_red)
}