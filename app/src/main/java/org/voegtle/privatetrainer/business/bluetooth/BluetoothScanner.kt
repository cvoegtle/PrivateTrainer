package org.voegtle.privatetrainer.business.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import androidx.compose.runtime.MutableState
import org.voegtle.privatetrainer.business.BluetoothConnectionStatus
import org.voegtle.privatetrainer.business.BluetoothState

class BluetoothScanner(private val bluetoothManager: BluetoothManager,
                       private val bluetoothState: MutableState<BluetoothState>) {

    @SuppressLint("MissingPermission")
    fun scanForPrivateTrainer(callback: (BluetoothDevice) -> Unit) {
        if (bluetoothState.value.connectionStatus > BluetoothConnectionStatus.permission_denied) {
            BleScanManager(
                bluetoothManager,
                scanPeriod = 5000,
                scanCallback = BleScanCallback({ it ->
                    val name = it?.device?.name
                    if (name.isNullOrBlank()) return@BleScanCallback

                    if (name == BleDevice.NAME_PRIVATETRAINER) {
                        callback(it.device)
                    }
                })
            ).scanBleDevices()
        }
    }
}
