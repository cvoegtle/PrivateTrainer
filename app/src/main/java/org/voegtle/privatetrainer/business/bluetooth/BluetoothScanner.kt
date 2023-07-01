package org.voegtle.privatetrainer.business.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import androidx.compose.runtime.MutableState
import org.voegtle.privatetrainer.business.BluetoothConnectionStatus
import org.voegtle.privatetrainer.business.BluetoothState
import org.voegtle.privatetrainer.business.PrivateTrainerDevice

class BluetoothScanner(private val bluetoothManager: BluetoothManager,
                       private val bluetoothState: BluetoothState) {

    @SuppressLint("MissingPermission")
    fun scanForPrivateTrainer(callback: (BluetoothDevice) -> Unit) {
        if (bluetoothState.connectionStatus > BluetoothConnectionStatus.permission_denied) {
            BleScanManager(
                bluetoothManager,
                scanPeriod = 5000,
                scanCallback = BleScanCallback(onScanResultAction = { it ->
                    val name = it?.device?.name
                    if (name.isNullOrBlank()) return@BleScanCallback

                    if (name == PrivateTrainerDevice.TECHNICAL_NAME) {
                        callback(it.device)
                    }
                })
            ).scanBleDevices()
        }
    }
}
