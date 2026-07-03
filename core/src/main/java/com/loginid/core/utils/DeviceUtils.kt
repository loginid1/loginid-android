package com.loginid.core.utils

import com.loginid.client.model.DeviceInfo
import android.app.Activity
import android.bluetooth.BluetoothManager
import android.os.Build
import androidx.core.content.getSystemService

/**
 * Utility methods for retrieving device-related metadata to be used in Multi-Factor Authentication (MFA) flows.
 */
class DeviceUtils {
    companion object {
        /**
         * Creates a [DeviceInfo] object populated with the current device's information.
         *
         * @param activity The current activity, used to access system services and resources.
         * @param deviceId An optional unique identifier for the device, if available.
         * @return A [DeviceInfo] instance containing:
         * - `clientName`: Always `"Android"`.
         * - `clientType`: Set to `.OTHER`.
         * - `osArch`: The device's OS architecture.
         * - `osName`: Always `"Android"`.
         * - `deviceId`: Passed from the parameter.
         * - `hasBluetooth`: A boolean indicating if Bluetooth is enabled.
         * - `osVersion`: The current Android version string.
         * - `screenHeight`: The device screen height in pixels.
         * - `screenWidth`: The device screen width in pixels.
         */
        fun getDeviceInfo(activity: Activity, deviceId: String?): DeviceInfo {
            return DeviceInfo(
                clientName = "Android",
                clientType = DeviceInfo.ClientType.OTHER,
                osArch = System.getProperty("os.arch"),
                osName = "Android",
                deviceId = deviceId,
                hasBluetooth = isBluetoothEnabled(activity),
                osVersion = Build.VERSION.RELEASE, //might not be 100% accurate
                screenHeight = activity.resources.displayMetrics.heightPixels.toLong(),
                screenWidth = activity.resources.displayMetrics.widthPixels.toLong()
            )
        }

        /**
         * Checks whether Bluetooth is currently enabled on the device.
         *
         * @return true if Bluetooth hardware exists and is enabled, false or null otherwise.
         *
         * Note: Returns false if Bluetooth is not supported or currently turned off.
         */
        fun isBluetoothEnabled(activity: Activity): Boolean? {
            val bluetoothManager = activity.getSystemService<BluetoothManager>()
            val bluetoothAdapter = bluetoothManager?.adapter
            return bluetoothAdapter?.isEnabled
        }

        /**
         * Creates a `User-Agent` string suitable for inclusion in HTTP requests.
         *
         * This method gathers the device’s operating system name, version,
         * and hardware model to form a simple, standardized `User-Agent` header.
         *
         * This format allows backend services to identify the client
         * environment for logging, debugging, or compatibility checks.
         *
         * @return A `String` containing the `User-Agent` value.
         */
        fun getUserAgent(): String {
            val osVersion = Build.VERSION.RELEASE
            val deviceModel = Build.MODEL
            return "Android/$osVersion; $deviceModel"
        }
    }
}
