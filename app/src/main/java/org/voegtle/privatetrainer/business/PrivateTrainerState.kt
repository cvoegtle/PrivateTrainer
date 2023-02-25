package org.voegtle.privatetrainer.business

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.voegtle.privatetrainer.business.bluetooth.BleDevice
import java.util.UUID

data class BluetoothState(
    var selectedDevice: BleDevice? = null,
    var connectionStatus: BluetoothConnectionStatus = BluetoothConnectionStatus.not_connected,
    var characteristics : MutableMap<UUID, ByteArray> = HashMap(),
    var  notificationsEnabled: Boolean = true,
    var powerOn: Boolean = false
)

enum class BluetoothConnectionStatus {
    not_supported, disabled, permission_denied, not_connected, device_found
}

enum class PrivateTrainerCommand {
    on, off, update, toggleNotification, requestBatteryStatus, readBattery
}

fun ByteArray.toHex(): String = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }
class CommandSequence {
    val on = byteArrayOf(0x04, 0x51)
    val off = byteArrayOf(0x04, 0x50)
    val battery = byteArrayOf(0x41, 0x54, 0x2b, 0x56, 0x4f, 0x4c, 0x0d, 0x0a)
}

@Parcelize
data class DeviceSettings(
    var id: String? = null,
    var name: String = "",
    var mode: Int = 1, // 1 - 10
    var strength: Int = 8, // Stufe 1 - 10
    var interval: Int = 3 // 1 - 200ss
) : Parcelable {
    fun isFavorite() = id != null
}



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
    val mode: SettingsSteps<Int> = SettingsSteps(
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
    val strength: SettingsSteps<Int> = SettingsSteps(
        SettingType.strength,
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
    val interval: SettingsSteps<Int> = SettingsSteps(
        SettingType.interval,
        1,
        2,
        3,
        5,
        7,
        10,
        14,
        20,
        40,
        100,
        200
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



