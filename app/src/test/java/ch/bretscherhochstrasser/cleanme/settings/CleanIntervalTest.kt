package ch.bretscherhochstrasser.cleanme.settings

import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.hamcrest.Matchers.`is` as Is

class CleanIntervalTest {

    @Test
    fun testFromMinutes() {
        assertThat(CleanInterval.fromMinutes(0), Is(CleanInterval.THIRTY_MIN))
        assertThat(CleanInterval.fromMinutes(12), Is(CleanInterval.THIRTY_MIN))
        assertThat(CleanInterval.fromMinutes(30), Is(CleanInterval.THIRTY_MIN))
        assertThat(CleanInterval.fromMinutes(34), Is(CleanInterval.THIRTY_MIN))
        assertThat(CleanInterval.fromMinutes(60), Is(CleanInterval.ONE_HOUR))
        assertThat(CleanInterval.fromMinutes(61), Is(CleanInterval.ONE_HOUR))
        assertThat(CleanInterval.fromMinutes(120), Is(CleanInterval.TWO_HOURS))
        assertThat(CleanInterval.fromMinutes(180), Is(CleanInterval.TWO_HOURS))
        assertThat(CleanInterval.fromMinutes(240), Is(CleanInterval.FOUR_HOURS))
        assertThat(CleanInterval.fromMinutes(344), Is(CleanInterval.FOUR_HOURS))
        assertThat(CleanInterval.fromMinutes(360), Is(CleanInterval.SIX_HOURS))
        assertThat(CleanInterval.fromMinutes(479), Is(CleanInterval.SIX_HOURS))
        assertThat(CleanInterval.fromMinutes(479), Is(CleanInterval.SIX_HOURS))
        assertThat(CleanInterval.fromMinutes(480), Is(CleanInterval.EIGHT_HOURS))
        assertThat(CleanInterval.fromMinutes(235875), Is(CleanInterval.EIGHT_HOURS))
    }

}