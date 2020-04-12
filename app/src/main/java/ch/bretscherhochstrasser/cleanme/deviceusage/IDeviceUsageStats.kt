package ch.bretscherhochstrasser.cleanme.deviceusage

/**
 * Interface to read device usage stats gathered by [DeviceUsageObserver]
 */
interface IDeviceUsageStats {
    val screenOnCount: Int
    val deviceUseDuration: Long

    override fun toString(): String
}