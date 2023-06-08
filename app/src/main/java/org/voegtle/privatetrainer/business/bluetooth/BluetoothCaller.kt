package org.voegtle.privatetrainer.business.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import org.voegtle.privatetrainer.business.*
import java.nio.charset.StandardCharsets.US_ASCII
import java.util.*

@SuppressLint("MissingPermission")

class BluetoothCaller(
    val context: Context,
    private val privateTrainerDevice: BluetoothDevice,
    val bluetoothState: MutableState<BluetoothState>
) {
    private val uuid_privatetrainer_service =
        UUID.fromString("0000ff00-0000-1000-8000-00805f9b34fb")
    private val uuid_command_characteristic = CharacteristicUuid.command.uuid
    private val uuid_battery_characteristic = CharacteristicUuid.battery.uuid

    val commandQueue = BluetoothCommandQueue()
    var gatt: BluetoothGatt? = null

    val bluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(
            gatt: BluetoothGatt,
            status: Int,
            newState: Int
        ) {
            val connected = newState == BluetoothProfile.STATE_CONNECTED
            this@BluetoothCaller.gatt = gatt
            val selectedDevice = bluetoothState.value.selectedDevice!!

            if (connected && !selectedDevice.connected) {
                commandQueue.scheduleDeferred { enableCharacteristicsNotification() }
            }

            if (connected != selectedDevice.connected) {
                bluetoothState.value =
                    bluetoothState.value.copy(
                        selectedDevice = selectedDevice.copy(connected = connected),
                        lastStatus = status
                    )
            }

            if (connected) {
                commandQueue.runNext()
            }
        }

        override fun onServicesDiscovered(
            gatt: BluetoothGatt,
            status: Int
        ) {
            updateStatus(status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                commandQueue.runNext()
            }
        }

        override fun onServiceChanged(gatt: BluetoothGatt) {
            commandQueue.runNext()
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            Log.e("PrivateTrainer", "write characteristic status: $status")
            updateStatus(status)
            commandQueue.runNext()
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            super.onCharacteristicChanged(gatt, characteristic, value)
            Log.e("PrivateTrainer", "characteristic value: ${value.toHex()} (\"${value.toString(US_ASCII)}\")")
            extractBatteryLevel(characteristic, value)

            commandQueue.runNext()
        }
    }

    private fun updateStatus(status: Int) {
        bluetoothState.value = bluetoothState.value.copy(lastStatus = status)
    }

    private fun extractBatteryLevel(
        characteristic: BluetoothGattCharacteristic,
        value: ByteArray
    ) {
        val currentState = bluetoothState.value.copy()
        currentState.characteristics[characteristic.uuid] = value.toHex()
        if (isBatteryCharacteristic(characteristic) && isBatteryVolume(value)) {
            currentState.selectedDevice?.batteryLevel = value.toString(US_ASCII)
        }
        bluetoothState.value = currentState
    }

    private fun isBatteryCharacteristic(characteristic: BluetoothGattCharacteristic) =
        characteristic.uuid == uuid_battery_characteristic

    private fun isBatteryVolume(value: ByteArray): Boolean {
        return value.toString(US_ASCII).startsWith("+VOL")
    }

    fun sendToDevice(
        command: PrivateTrainerCommand,
        settings: DeviceSettings,
    ) {
        Log.e("BluetoothCaller", "sendToDevice()")
        clearBluetoothState(command)
        commandQueue.clear()
        commandQueue.scheduleDeferred { discoverServices() }

        privateTrainerDevice.connectGatt(context, false, bluetoothGattCallback)
        when (command) {
            PrivateTrainerCommand.on -> commandQueue.scheduleDeferred { switchOn() }
            PrivateTrainerCommand.off -> commandQueue.scheduleDeferred { switchOff() }
            PrivateTrainerCommand.requestBatteryStatus -> commandQueue.scheduleDeferred { askForBatteryStatus() }
            PrivateTrainerCommand.update -> {
                commandQueue.scheduleDeferred {
                    sendSetting(
                        CommandType.strength,
                        settings.strength
                    )
                }
                commandQueue.scheduleDeferred {
                    sendSetting(
                        CommandType.mode,
                        settings.mode + CommandType.MODE_OFFSET
                    )
                }
                commandQueue.scheduleDeferred {
                    sendSetting(
                        CommandType.interval,
                        settings.interval
                    )
                }
            }
        }
    }

    private fun clearBluetoothState(command: PrivateTrainerCommand) {
        var clearedState = bluetoothState.value.copy();
        val clearBatteryStatus = command == PrivateTrainerCommand.requestBatteryStatus
        clearedState.clear(clearBatteryStatus)
        bluetoothState.value = clearedState
    }


    private fun sendSetting(type: Byte, value: Int) {
        findPrivateTrainerCharacteristic()?.let {
            val commandSequence = paddedByteArray(type, value.toByte())
            writeCharacteristic(it, commandSequence)
        }
    }

    private fun discoverServices() {
        gatt?.discoverServices()
    }

    private fun switchOn() {
        findPrivateTrainerCharacteristic()?.let {
            writeCharacteristic(it, CommandSequence.on)
            bluetoothState.value = bluetoothState.value.copy(powerOn = true)
        }
    }

    private fun switchOff() {
        findPrivateTrainerCharacteristic()?.let {
            writeCharacteristic(it, CommandSequence.off)
            bluetoothState.value = bluetoothState.value.copy(powerOn = false)
        }
    }

    private fun askForBatteryStatus() {
        findBatteryCharacteristic()?.let {
            writeCharacteristic(it, CommandSequence.battery)
        }
    }

    private fun writeCharacteristic(
        characteristic: BluetoothGattCharacteristic,
        byteSequence: ByteArray
    ) {
        Thread.sleep(1000)
        val valueAccepted = characteristic.setValue(byteSequence)
        gatt!!.writeCharacteristic(characteristic)
        if (valueAccepted) {
            bluetoothState.value =
                bluetoothState.value.copy(lastWrittenValue = byteSequence.toHex())
        }
        Log.w("BluetoothCaller", "write to ${characteristic.uuid} value=${byteSequence.toHex()} ${if (valueAccepted) "" else "not"} accepted")
    }

    private fun findPrivateTrainerCharacteristic(): BluetoothGattCharacteristic? {
        return findCharacteristic(uuid_command_characteristic)
    }

    private fun findBatteryCharacteristic(): BluetoothGattCharacteristic? {
        return findCharacteristic(uuid_battery_characteristic)
    }

    private fun findCharacteristic(characteristicUuid: UUID): BluetoothGattCharacteristic? {
        gatt?.let {
            val privateTrainerService = it.getService(uuid_privatetrainer_service)
            for (characteristic in privateTrainerService.characteristics) {
                if (characteristic.uuid == characteristicUuid) {
                    return characteristic
                }
            }
        }
        return null
    }

    private fun enableCharacteristicsNotification() {
        gatt?.let {
            val privateTrainerService = it.getService(uuid_privatetrainer_service)
            for (characteristic in privateTrainerService.characteristics) {
                if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY != 0) {
                    toggleNotifications(it, characteristic, true)
                }
            }

            bluetoothState.value = bluetoothState.value.copy(notificationsEnabled = true)
            commandQueue.runNext()
        }
    }

    private fun toggleNotifications(
        it: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        enable: Boolean
    ) {
        val success =
            it.setCharacteristicNotification(characteristic, enable)
        Log.w(
            "BluetoothCaller",
            "${characteristic.uuid} notification ${if (success && enable) "" else "not"} enabled"
        )
    }

    fun isConnected(): Boolean =
        gatt != null && bluetoothState.value.selectedDevice?.connected ?: false

}
