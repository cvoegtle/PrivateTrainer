package org.voegtle.privatetrainer.business.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import androidx.compose.runtime.MutableState
import org.voegtle.privatetrainer.business.BluetoothConnectionStatus
import org.voegtle.privatetrainer.business.BluetoothState

class BluetoothScanner(private val bluetoothManager: BluetoothManager,
                       private val bluetoothState: BluetoothState) {

    @SuppressLint("MissingPermission")
    fun scanForPrivateTrainer(callback: (BluetoothDevice) -> Unit) {
        if (bluetoothState.connectionStatus > BluetoothConnectionStatus.permission_denied) {
            BleScanManager(
                bluetoothManager,
                scanPeriod = 5000,
                scanCallback = BleScanCallback({ it ->
                    val name = it?.device?.name
                    val address = it?.device?.address
                    if (name.isNullOrBlank()) return@BleScanCallback

                    if (name == BleDevice.NAME_PRIVATETRAINER && address != "24:6F:44:82:0E:76") {
                        callback(it.device)
                    }
                })
            ).scanBleDevices()
        }
    }
}
