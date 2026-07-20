package io.loginid.auth.models

import io.loginid.client.model.DeviceInfo

/**
 * Information about the device.
 * All of these attributes are optional and should be provided on a best-effort basis.
 * If provided, they may be taken into consideration in order to improve user experience.
 */
data class DeviceDetails(
    /**
     * Client name (e.g. "Safari", "Chrome", "MyApp").
     */
    val clientName: String?,

    /**
     * Client type (e.g. "browser" or "other").
     */
    val clientType: String?,

    /**
     * Client version (e.g. "15.4", "100.0").
     */
    val clientVersion: String?,

    /**
     * A unique identifier for this device.
     */
    val deviceId: String?,

    /**
     * Indicates whether the client has access to Bluetooth.
     */
    val hasBluetooth: Boolean,

    /**
     * Last usage timestamp in RFC3339 format.
     */
    val lastUsedAt: String?,

    /**
     * OS architecture (e.g. "x86_64", "arm64").
     */
    val osArch: String?,

    /**
     * OS name (e.g. "iOS", "macOS", "Windows").
     */
    val osName: String?,

    /**
     * OS version (e.g. "16.0", "13.5").
     */
    val osVersion: String?,

    /**
     * Screen height in pixels.
     */
    val screenHeight: Long?,

    /**
     * Screen width in pixels.
     */
    val screenWidth: Long?,

    /**
     * JSON string containing client WebAuthn capabilities.
     */
    val webauthnCapabilities: String?
) {
    /**
     * Initializes the device info from a [DeviceInfo] model.
     *
     * @param deviceInfo The source model used to populate this instance.
     */
    internal constructor(deviceInfo: DeviceInfo) : this(
        clientName = deviceInfo.clientName,
        clientType = deviceInfo.clientType?.value,
        clientVersion = deviceInfo.clientVersion,
        deviceId = deviceInfo.deviceId,
        hasBluetooth = deviceInfo.hasBluetooth ?: false,
        lastUsedAt = deviceInfo.lastUsedAt,
        osArch = deviceInfo.osArch,
        osName = deviceInfo.osName,
        osVersion = deviceInfo.osVersion,
        screenHeight = deviceInfo.screenHeight,
        screenWidth = deviceInfo.screenWidth,
        webauthnCapabilities = deviceInfo.webauthnCapabilities
    )
}
