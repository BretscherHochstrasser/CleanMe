package ch.bretscherhochstrasser.cleanme.deviceusage

/**
 * Data class to hold the device usage stats
 */
data class DeviceUsageStats(
    val screenOnCount: Int,
    val deviceUseDuration: Long
)