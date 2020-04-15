package ch.bretscherhochstrasser.cleanme.overlay

import androidx.annotation.DrawableRes
import ch.bretscherhochstrasser.cleanme.R

enum class ParticleType(@DrawableRes val icon: Int) {
    VIRUS(R.drawable.particle_virus),
    BACTERIUM(R.drawable.particle_bacterium)
}