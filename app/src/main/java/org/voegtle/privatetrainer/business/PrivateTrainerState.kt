package org.voegtle.privatetrainer.business

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

data class BluetoothState(
    var selectedDevice: ConnectedPrivateTrainerDevice? = null,
    var connectionStatus: BluetoothConnectionStatus = BluetoothConnectionStatus.not_initialised,
    var characteristics: MutableMap<UUID, String> = HashMap(),
    var powerOn: Boolean = false,
    var lastStatus: Int? = null,
    var lastWrittenValue: String? = null,
    var changeCounter: Int = 0
) {
    fun lastReceivedNotifications(): String {
        return characteristics.map { e -> e.key.toString().substring(4, 8) + "=" + e.value }
            .joinToString(separator = "\n")
    }

    fun clear(includingBattery: Boolean) {
        characteristics.clear()
        if (includingBattery) {
            selectedDevice?.let { it.batteryLevel = BatteryConstants.UNKNOWN_STATUS }
        }
    }

    fun incrementChangeCounter() {
        changeCounter++
    }
}

class BatteryConstants {
    companion object {
        val UNKNOWN_STATUS = "-"
        val PREFIX = "+VOL"
    }
}

enum class BluetoothConnectionStatus {
    not_initialised, not_supported, disabled, permission_denied, not_connected, device_found, device_bound
}

enum class PrivateTrainerCommand {
    on, off, update, requestBatteryStatus
}

fun paddedByteArray(vararg input: Byte): ByteArray {
    val bytes = ArrayList<Byte>()
    for (b in input) {
        bytes.add(b)
    }

    while (bytes.size < 6) {
        bytes.add(0x01)
    }
//    bytes.add('\r'.toByte())
//    bytes.add('\n'.toByte())
    return bytes.toByteArray()
}

fun ByteArray.toHex(): String =
    joinToString(separator = ":") { eachByte -> "%02x".format(eachByte) }

class CommandSequence {
    companion object {
        val on = paddedByteArray(0x04, 0x51)
        val off = paddedByteArray(0x04, 0x50, 0x01, 0x01, 0x01, 0x01)
        val battery: ByteArray = "AT+VOL\r\n".toByteArray(Charsets.US_ASCII)
    }
}

//    val battery = byteArrayOf(0x41, 0x54, 0x2b, 0x56, 0x4f, 0x4c, 0x0d, 0x0a)
class CommandType {
    companion object {
        val strength: Byte = 0x01
        val mode: Byte = 0x02
        val interval: Byte = 0x03

        val MODE_OFFSET: Int = 16
    }
}

class CharacteristicUuid(val name: String, val uuid: UUID) {

    companion object {
        val battery = CharacteristicUuid(
            name = "ff03",
            uuid = UUID.fromString("0000ff03-0000-1000-8000-00805f9b34fb")
        )
        val command = CharacteristicUuid(
            name = "ff02",
            uuid = UUID.fromString("0000ff02-0000-1000-8000-00805f9b34fb")
        )
    }
}

@Parcelize
data class DeviceSettings(
    var id: String? = null,
    var name: String = "",
    var mode: Int = 1, // 1 - 10
    var strength: Int = 8, // Stufe 1 - 10
    var interval: Int = 3, // 1 - 200s
) : Parcelable {
    fun isFavorite() = id != null
}

@Parcelize
data class PrivateTrainerDevice(
    var givenName: String? = null,
    var address: String,
    var available: Boolean = false,
    var autoConnect: Boolean = false,
    var hidden: Boolean = false
) : Parcelable {
    fun isUnnamed() = givenName == null

    companion object {
        val TECHNICAL_NAME = "TD5322A_V2.1.3BLE"
    }
}

data class ConnectedPrivateTrainerDevice(
    var givenName: String,
    var address: String,
    var connected: Boolean = false,
    var batteryLevel: String = "-"
) {
    companion object {
        fun fromPrivateTrainer(privateTrainerDevice: PrivateTrainerDevice): ConnectedPrivateTrainerDevice {
            return ConnectedPrivateTrainerDevice(
                givenName = privateTrainerDevice.givenName ?: PrivateTrainerDevice.TECHNICAL_NAME,
                address = privateTrainerDevice.address
            )
        }
    }
}

@Parcelize
data class PrivateTrainerDeviceContainer(
    val devices: MutableMap<String, PrivateTrainerDevice>,
    var showHiddenDevices: Boolean = false,
    var updateCounter: Int = 0
) :
    Parcelable {
    fun found(address: String): PrivateTrainerDevice {
        var device = devices.get(address)
        if (device == null) {
            device = PrivateTrainerDevice(address = address)
        }
        device.available = true
        put(device)

        return device
    }

    fun put(device: PrivateTrainerDevice) {
        updateCounter++
        devices.put(device.address, device)
    }

    fun resetAvailabilty() {
        devices.forEach { entry -> entry.value.available = false }
        updateCounter++
    }

    fun containsUnnamedDevices() = devices.any { device -> device.value.isUnnamed() }
    fun containsHiddenDevices() = devices.any { device -> device.value.hidden }
    fun unnamedDevices() =
        devices.map { entry -> entry.value }.filter { device -> device.isUnnamed() }.toList()

    fun isEmpty() = devices.isEmpty()
    fun size() = devices.size
    fun showHiddenDevices(): Boolean? {
        if (containsHiddenDevices()) {
            return showHiddenDevices
        } else {
            return null
        }
    }

    fun toggleShowHidden() {
        showHiddenDevices = !showHiddenDevices
    }
}


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



