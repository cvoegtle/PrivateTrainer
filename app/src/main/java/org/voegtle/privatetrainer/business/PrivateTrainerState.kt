package org.voegtle.privatetrainer.business

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class BluetoothState(
    var bondedDevices: MutableSet<BluetoothDevice> = HashSet(),
    var selectedDevice: BluetoothDevice? = null,
    var connectionStatus: BluetoothConnectionStatus = BluetoothConnectionStatus.not_supported
) {
    fun copyFrom(bluetoothState: BluetoothState) {
        this.bondedDevices = bluetoothState.bondedDevices
        this.selectedDevice = bluetoothState.selectedDevice
        this.connectionStatus = bluetoothState.connectionStatus
    }
}

enum class BluetoothConnectionStatus {
    not_supported, disabled, permission_denied, not_connected, connected
}

data class DeviceSettings(
    var mode: Int = 1, // 1 - 10
    var strength: Float = 0.8f, // 10 - 100%
    var interval: Float = 2f,// 0,1 - 120s
)

class PrivateTrainerViewModel() :
    ViewModel() {
    private val _bluetoothState = MutableStateFlow(BluetoothState())
    var bluetoothState: StateFlow<BluetoothState> = _bluetoothState

    private val _deviceSettings = MutableStateFlow(DeviceSettings())
    var deviceSettings: StateFlow<DeviceSettings> = _deviceSettings

    fun closeDetailScreen() {
    }
}
