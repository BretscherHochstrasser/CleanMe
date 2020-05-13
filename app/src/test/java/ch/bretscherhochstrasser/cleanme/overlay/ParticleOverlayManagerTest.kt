package ch.bretscherhochstrasser.cleanme.overlay

import android.view.WindowManager
import ch.bretscherhochstrasser.cleanme.helper.OverlayPermissionWrapper
import ch.bretscherhochstrasser.cleanme.settings.AppSettings
import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

/**
 *
 */
class ParticleOverlayManagerTest {

    @Mock
    private lateinit var mockParticleOverlay: ParticleOverlayView

    @Mock
    private lateinit var mockParticleGenerator: ParticleGenerator

    @Mock
    private lateinit var mockAppSettings: AppSettings

    @Mock
    private lateinit var mockOverlayPermissionWrapper: OverlayPermissionWrapper

    @Mock
    private lateinit var mockWindowManager: WindowManager

    private lateinit var overlayManager: ParticleOverlayManager

    private val testParticle = Particle(ParticleType.BACTERIUM, 15.0, 42.0, 45)

    private var overlayParticleCount = 0

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        // default mock behavior
        whenever(mockOverlayPermissionWrapper.canDrawOverlay).thenReturn(true)
        whenever(mockParticleGenerator.generateParticle()).thenReturn(testParticle)
        whenever(mockAppSettings.maxOverlayParticleCount).thenReturn(10)
        whenever(mockAppSettings.overlayParticleSize).thenReturn(34)
        whenever(mockAppSettings.overlayParticleAlpha).thenReturn(200)

        // mock particle stack behavior
        whenever(mockParticleOverlay.particleCount).then { overlayParticleCount }
        whenever(mockParticleOverlay.pushParticle(any())).then { overlayParticleCount++ }
        whenever(mockParticleOverlay.popParticle()).then {
            overlayParticleCount--
            testParticle
        }

        overlayManager = ParticleOverlayManager(
            mockParticleOverlay,
            mockParticleGenerator,
            mockAppSettings,
            mockOverlayPermissionWrapper,
            mockWindowManager
        )
    }

    @Test
    fun update_InitialRunAtZeroDoesNotAddParticles() {
        // initially there are no particles, at t=0 no particles are required
        overlayManager.update(0, 30000)
        verify(mockParticleOverlay).alpha = 200
        verify(mockParticleOverlay).particleSize = 34
        verify(mockParticleOverlay, never()).pushParticle(any())
        verify(mockParticleOverlay, never()).popParticle()
    }

    @Test
    fun update_NoAddedParticlesIfAlreadyMatchingCount() {
        // 5/10 particles showing, half-time -> particle count matches
        overlayParticleCount = 5

        overlayManager.update(50000, 100000)
        verify(mockParticleOverlay).alpha = 200
        verify(mockParticleOverlay).particleSize = 34
        verify(mockParticleOverlay, never()).pushParticle(any())
        verify(mockParticleOverlay, never()).popParticle()
    }

    @Test
    fun update_AddParticlesToMatchTime() {
        // 5/10 particles showing, 72% of time done, add 2
        overlayParticleCount = 5

        overlayManager.update(72000, 100000)
        verify(mockParticleOverlay).alpha = 200
        verify(mockParticleOverlay).particleSize = 34
        verify(mockParticleOverlay, times(2)).pushParticle(eq(testParticle))
        verify(mockParticleOverlay, never()).popParticle()
    }

    @Test
    fun update_AddParticlesToMatchMaxCount() {
        // 5 particles showing, max count increased to 18, add 4 to reach 9 (50%)
        overlayParticleCount = 5
        whenever(mockAppSettings.maxOverlayParticleCount).thenReturn(18)

        overlayManager.update(50000, 100000)
        verify(mockParticleOverlay).alpha = 200
        verify(mockParticleOverlay).particleSize = 34
        verify(mockParticleOverlay, times(4)).pushParticle(eq(testParticle))
        verify(mockParticleOverlay, never()).popParticle()
    }

    @Test
    fun update_RemoveParticlesToMatchTime() {
        // 5/10 particles showing, but only 21% of time done, remove 3
        overlayParticleCount = 5

        overlayManager.update(21000, 100000)
        verify(mockParticleOverlay).alpha = 200
        verify(mockParticleOverlay).particleSize = 34
        verify(mockParticleOverlay, never()).pushParticle(any())
        verify(mockParticleOverlay, times(3)).popParticle()
    }

    @Test
    fun update_RemoveParticlesToMatchMaxCount() {
        // 5 particles showing, max count decreased to 6, remove 2 to reach 3 (50%)
        overlayParticleCount = 5
        whenever(mockAppSettings.maxOverlayParticleCount).thenReturn(6)

        overlayManager.update(50000, 100000)
        verify(mockParticleOverlay).alpha = 200
        verify(mockParticleOverlay).particleSize = 34
        verify(mockParticleOverlay, never()).pushParticle(any())
        verify(mockParticleOverlay, times(2)).popParticle()
    }

    @Test
    fun showOverlay_NotYetShowingAddsOverlay() {
        overlayManager.showOverlay()
        verify(mockWindowManager).addView(eq(mockParticleOverlay), any())
    }

    @Test
    fun showOverlay_AlreadyShowingDoesNotAddOverlay() {
        overlayManager.showOverlay()
        overlayManager.showOverlay()
        verify(mockWindowManager, times(1)).addView(eq(mockParticleOverlay), any())
    }

    @Test
    fun showOverlay_DoesNothingWhenNoPermission() {
        whenever(mockOverlayPermissionWrapper.canDrawOverlay).thenReturn(false)

        overlayManager.showOverlay()
        verify(mockWindowManager, never()).addView(any(), any())
    }

    @Test
    fun hideOverlay_RemovesOverlayWhenShowing() {
        overlayManager.showOverlay()
        overlayManager.hideOverlay()
        verify(mockWindowManager).removeView(eq(mockParticleOverlay))
    }

    @Test
    fun hideOverlay_DoesNothingIfNotShowing() {
        overlayManager.hideOverlay()
        verify(mockWindowManager, never()).removeViewImmediate(any())
    }

}