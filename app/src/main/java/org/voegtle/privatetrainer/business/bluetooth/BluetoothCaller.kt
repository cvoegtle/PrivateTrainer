package org.voegtle.privatetrainer.business.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import org.voegtle.privatetrainer.business.BluetoothState
import org.voegtle.privatetrainer.business.DeviceSettings
import org.voegtle.privatetrainer.business.PrivateTrainerCommand
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.US_ASCII
import java.util.*

@SuppressLint("MissingPermission")

class BluetoothCaller(
    val context: Context,
    val privateTrainerDevice: BluetoothDevice,
    val bluetoothState: MutableState<BluetoothState>
) {
    private val uuid_genericAccess = UUID.fromString("00001800-0000-1000-8000-00805f9b34fb")
    private val uuid_deviceInformation = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb")
    private val uuid_privatetrainer_service =
        UUID.fromString("0000ff00-0000-1000-8000-00805f9b34fb")
    private val uuid_privatetrainer_characteristic =
        UUID.fromString("0000ff02-0000-1000-8000-00805f9b34fb")
    private val uuid_battery_characteristic =
        UUID.fromString("0000ff03-0000-1000-8000-00805f9b34fb")

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

            if (connected) {
                commandQueue.runNext()
            }
        }

        override fun onServicesDiscovered(
            gatt: BluetoothGatt,
            status: Int
        ) {
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
            Log.e("PrivateTrainer", "write characteristic status: ${status}")
            commandQueue.runNext()
        }

        override fun onDescriptorRead(
            gatt: BluetoothGatt?,
            descriptor: BluetoothGattDescriptor?,
            status: Int
        ) {
            super.onDescriptorRead(gatt, descriptor, status)
            commandQueue.runNext()
        }

        override fun onDescriptorRead(
            gatt: BluetoothGatt,
            descriptor: BluetoothGattDescriptor,
            status: Int,
            value: ByteArray
        ) {
            super.onDescriptorRead(gatt, descriptor, status, value)
            commandQueue.runNext()
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt?,
            descriptor: BluetoothGattDescriptor?,
            status: Int
        ) {
            super.onDescriptorWrite(gatt, descriptor, status)
            commandQueue.runNext()
        }

        override fun onReliableWriteCompleted(gatt: BluetoothGatt?, status: Int) {
            super.onReliableWriteCompleted(gatt, status)
            commandQueue.runNext()
        }

        override fun onReadRemoteRssi(gatt: BluetoothGatt?, rssi: Int, status: Int) {
            super.onReadRemoteRssi(gatt, rssi, status)
            commandQueue.runNext()
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            status: Int
        ) {
            extractBatteryLevel(characteristic, value)
            commandQueue.runNext()
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            Log.e("PrivateTrainer", "characteristic value: ${value}")
            extractBatteryLevel(characteristic, value)

            commandQueue.runNext()
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            Log.e("PrivateTrainer", "characteristic value: ${characteristic!!.value}")
            val currentState = bluetoothState.value.copy()
            currentState.characteristics[characteristic!!.uuid] = characteristic.value
            bluetoothState.value = currentState

            commandQueue.runNext()
        }

        override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
            super.onMtuChanged(gatt, mtu, status)
            commandQueue.runNext()
        }

        override fun onPhyUpdate(gatt: BluetoothGatt?, txPhy: Int, rxPhy: Int, status: Int) {
            super.onPhyUpdate(gatt, txPhy, rxPhy, status)
            commandQueue.runNext()
        }

        override fun onPhyRead(gatt: BluetoothGatt?, txPhy: Int, rxPhy: Int, status: Int) {
            super.onPhyRead(gatt, txPhy, rxPhy, status)
            commandQueue.runNext()
        }
    }

    private fun extractBatteryLevel(
        characteristic: BluetoothGattCharacteristic,
        value: ByteArray
    ) {
        val currentState = bluetoothState.value.copy()
        currentState.characteristics[characteristic.uuid] = value
        if (characteristic.uuid == uuid_battery_characteristic) {
            currentState.selectedDevice?.batteryLevel = value.toString(US_ASCII)
        }
        bluetoothState.value = currentState
    }

    fun sendToDevice(
        command: PrivateTrainerCommand,
        settings: DeviceSettings,
    ) {
        Log.e("BluetoothCaller", "sendToDevice()")
        commandQueue.clear()
        commandQueue.scheduleDeferred { discoverServices() }
        privateTrainerDevice.connectGatt(context, false, bluetoothGattCallback)

        when (command) {
            PrivateTrainerCommand.on -> commandQueue.scheduleDeferred { switchOn() }
            PrivateTrainerCommand.requestBatteryStatus -> commandQueue.scheduleDeferred { askForBatteryStatus() }
            PrivateTrainerCommand.toggleNotification -> commandQueue.scheduleDeferred {
                requestCharacteristicsNotification() }
            PrivateTrainerCommand.readBattery -> commandQueue.scheduleDeferred { readBatteryStatus() }
        }
    }

    fun discoverServices() {
        gatt?.discoverServices()
    }

    private fun switchOn() {
        findPrivateTrainerCharacteristic()?.let {
            gatt!!.writeCharacteristic(
                it,
                byteArrayOf(0x04, 0x51), BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            )
        }
    }

    private fun askForBatteryStatus() {
        findBatteryCharacteristic()?.let {
            gatt!!.writeCharacteristic(
                it,
                byteArrayOf(0x41, 0x54, 0x2b, 0x56, 0x4f, 0x4c, 0x0d, 0x0a),
                BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            )
        }
    }

    private fun readBatteryStatus() {
        findBatteryCharacteristic()?.let {
            gatt!!.readCharacteristic(it)
        }
    }

    private fun sendCommandsToCharacteristics() {
        findPrivateTrainerCharacteristic()?.let {
            commandQueue.schedule {
            }
            commandQueue.schedule {
                gatt!!.writeCharacteristic(
                    it,
                    byteArrayOf(0x01, 0x08), BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                )
            }
            commandQueue.schedule {
                gatt!!.writeCharacteristic(
                    it,
                    byteArrayOf(0x02, 0x1a), BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                )
            }
            commandQueue.schedule {
                gatt!!.writeCharacteristic(
                    it,
                    byteArrayOf(0x03, 0x0a), BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                )
            }
        }

    }

    private fun findPrivateTrainerCharacteristic(): BluetoothGattCharacteristic? {
        return findCharacteristic(uuid_privatetrainer_characteristic)
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

    private fun requestCharacteristicsNotification() {
        gatt?.let {
            val enable = !bluetoothState.value.notificationsEnabled

            val privateTrainerService = it.getService(uuid_privatetrainer_service)
            for (characteristic in privateTrainerService.characteristics) {
                if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY != 0) {
                    toggleNotifications(it, characteristic, enable)
                }
            }

            bluetoothState.value = bluetoothState.value.copy(notificationsEnabled = enable)
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
