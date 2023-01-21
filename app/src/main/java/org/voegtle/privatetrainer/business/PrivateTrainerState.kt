package org.voegtle.privatetrainer.business

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.voegtle.privatetrainer.business.bluetooth.BleDevice
import java.util.UUID

data class BluetoothState(
    var selectedDevice: BleDevice? = null,
    var connectionStatus: BluetoothConnectionStatus = BluetoothConnectionStatus.not_supported,
    var characteristics : MutableMap<UUID, ByteArray> = HashMap()
) {
    fun copyFrom(bluetoothState: BluetoothState) {
        this.selectedDevice = bluetoothState.selectedDevice
        this.connectionStatus = bluetoothState.connectionStatus
    }
}

enum class BluetoothConnectionStatus {
    not_supported, disabled, permission_denied, not_connected, device_found
}

@Parcelize
data class DeviceSettings(
    var mode: Int = 1, // 1 - 10
    var strength: Float = 0.8f, // 10 - 100%
    var interval: Float = 2.0f // 0,1 - 120s
) : Parcelable


//val DeviceSettingsSaver = run {
//    mapSaver<DeviceSettings>(
//        save = {
//            mapOf<String, Any>(
//                SettingType.mode.toString() to it.mode,
//                SettingType.strength.toString() to it.strength,
//                SettingType.interval.toString() to it.interval
//            )
//        },
//        restore = {
//            DeviceSettings(
//                mode = it[SettingType.mode.toString()] as Int,
//                strength = it[SettingType.strength.toString()] as Float,
//                interval = it[SettingType.interval.toString()] as Float
//            )
//        }
//    )
//}

enum class SettingType {
    mode,
    strength,
    interval
}

class SettingsRanges(
    val mode: SettingsSteps<Int> = SettingsSteps<Int>(
        SettingType.mode,
        1,
        2,
        3,
        4,
        5,
        6,
        7,
        8,
        9,
        10
    ),
    val strength: SettingsSteps<Float> = SettingsSteps<Float>(
        SettingType.strength,
        0.1f,
        0.2f,
        0.3f,
        0.4f,
        0.5f,
        0.6f,
        0.7f,
        0.8f,
        0.9f,
        1.0f
    ),
    val interval: SettingsSteps<Float> = SettingsSteps<Float>(
        SettingType.interval,
        0.1f,
        0.5f,
        1.0f,
        2.0f,
        5.0f,
        8.0f,
        15.0f,
        30.0f,
        60.0f,
        90.0f,
        120.0f
    )
) {
    fun getRange(type: SettingType): SettingsSteps<*> = when (type) {
        SettingType.mode -> mode
        SettingType.strength -> strength
        SettingType.interval -> interval
    }
}

class SettingsSteps<T>(val type: SettingType, val steps: ArrayList<T> = ArrayList()) {
    constructor(type: SettingType, vararg inputSteps: T) : this(type) {
        steps.addAll(inputSteps)
    }

    fun numberOfSteps() = steps.size
    fun index2Value(floatIndex: Float): T = steps[floatIndex.toInt()]
    fun value2Index(value: T): Float {
        for (index in 0 until steps.size) {
            if (value == steps[index]) {
                return index.toFloat();
            }
        }
        throw java.lang.IndexOutOfBoundsException()
    }

    fun start(): Float = 0.0f
    fun end(): Float = (steps.size - 1).toFloat()
}



