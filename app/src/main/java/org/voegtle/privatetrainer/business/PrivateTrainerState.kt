package org.voegtle.privatetrainer.business

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class BluetoothState(
    var bondedDevices: MutableSet<BluetoothDevice> = HashSet(),
    var selectedDevice: BluetoothDevice? = null,
    var connectionStatus: BluetoothConnectionStatus = BluetoothConnectionStatus.not_supported,
)

enum class BluetoothConnectionStatus {
    not_supported, disabled, permission_denied, not_connected, connected
}

data class DeviceSettings(
    var mode: Int = 1, // 1 - 10
    var strength: Float = 0.8f, // 10 - 100%
    var interval: Float = 2f,// 0,1 - 120s
)

data class PrivateTrainerState(
    var bluetoothState: BluetoothState = BluetoothState(),
    var deviceSetting: DeviceSettings = DeviceSettings()
)

class PrivateTrainerViewModel() :
    ViewModel() {
    private val _privateTrainerState = MutableStateFlow(PrivateTrainerState())
    val privateTrainerState: StateFlow<PrivateTrainerState> = _privateTrainerState

    fun closeDetailScreen() {
    }
}
