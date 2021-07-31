package ch.bretscherhochstrasser.cleanme

import org.junit.After
import org.junit.Before
import org.mockito.MockitoAnnotations


/**
 * Handles initialization and closing of annotated mockito mocks
 */
open class MockitoTest {

    private var closeable: AutoCloseable? = null

    @Before
    fun openMocks() {
        closeable = MockitoAnnotations.openMocks(this)
    }

    @After
    @Throws(Exception::class)
    fun releaseMocks() {
        closeable!!.close()
    }
}