package org.voegtle.privatetrainer.business.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import androidx.compose.runtime.MutableState
import org.voegtle.privatetrainer.business.BluetoothConnectionStatus
import org.voegtle.privatetrainer.business.BluetoothState
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

@SuppressLint("MissingPermission")

class BluetoothCaller(
    val context: Context,
    val privateTrainerDevice: BluetoothDevice,
    val bluetoothState: MutableState<BluetoothState>
) {
    private val uuid_genericAccess = UUID.fromString("00001800-0000-1000-8000-00805f9b34fb")
    private val uuid_deviceInformation = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb")
    private val uuid_unknown = UUID.fromString("0000ff00-0000-1000-8000-00805f9b34fb")

    val commandQueue = BluetoothCommandQueue()
    var gatt: BluetoothGatt? = null

    val bluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(
            gatt: BluetoothGatt,
            status: Int,
            newState: Int
        ) {
            val connected = newState == BluetoothProfile.STATE_CONNECTED
            bluetoothState.value =
                bluetoothState.value.copy(
                    selectedDevice = BleDevice(
                        name = privateTrainerDevice.name, connected = connected
                    ), connectionStatus = BluetoothConnectionStatus.device_found
                )
            this@BluetoothCaller.gatt = gatt

            commandQueue.clear()
            if (connected) {
                discoverServices()
            }
            commandQueue.runNext()
        }

        override fun onServicesDiscovered(
            gatt: BluetoothGatt,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                readCharacteristics()
                requestCharacteristicsNotification()
            }
            commandQueue.runNext()
        }

        override fun onServiceChanged(gatt: BluetoothGatt) {
            commandQueue.runNext()
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
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
            val currentState = bluetoothState.value.copy()
            currentState.characteristics.put(characteristic.uuid, value)
            bluetoothState.value = currentState

            commandQueue.runNext()
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
            commandQueue.runNext()
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            val currentState = bluetoothState.value.copy()
            currentState.characteristics.put(characteristic.uuid, value)
            bluetoothState.value = currentState

            commandQueue.runNext()
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
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

    fun connect() {
        commandQueue.clear()
        privateTrainerDevice.connectGatt(context, true, bluetoothGattCallback)
    }

    fun discoverServices() {
        commandQueue.schedule { gatt?.discoverServices() }
    }

    private fun readCharacteristics() {
        gatt?.let {
            val privateTrainerService = it.getService(uuid_unknown)
            for (characteristic in privateTrainerService.characteristics) {
                commandQueue.schedule {
                    it.readCharacteristic(characteristic)
                }
            }
            Logger.getGlobal().log(
                Level.INFO,
                privateTrainerService.characteristics.joinToString(
                    separator = "\n"
                ) { "${it.uuid} - P=${it.properties}" })
        }
    }

    private fun requestCharacteristicsNotification() {
        gatt?.let {
            val privateTrainerService = it.getService(uuid_unknown)
            for (characteristic in privateTrainerService.characteristics) {
                commandQueue.schedule {
                    it.setCharacteristicNotification(characteristic, true)
                }
            }
            Logger.getGlobal().log(
                Level.INFO,
                privateTrainerService.characteristics.joinToString(
                    separator = "\n"
                ) { "${it.uuid} - P=${it.properties}" })
        }
    }

    private fun readBatteryLevel() {
        val batteryServiceUuid =
            UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb")
        val batteryLevelCharUuid =
            UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb")

        gatt?.let {
            commandQueue.schedule {
                val batteryLevelChar: BluetoothGattCharacteristic? = it
                    .getService(batteryServiceUuid)
                    ?.getCharacteristic(batteryLevelCharUuid)
                it.readCharacteristic(batteryLevelChar)
            }
        }
    }

}
