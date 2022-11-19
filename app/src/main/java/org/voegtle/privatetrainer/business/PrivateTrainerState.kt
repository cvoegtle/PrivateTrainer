package org.voegtle.privatetrainer.business

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class BluetoothState(
    var bondedDevices: MutableSet<BluetoothDevice> = HashSet(),
    var selectedDevice: BluetoothDevice? = null,
    var connected: Boolean = false,
    var permissionGranted: Boolean = false
)

data class PrivateTrainerState(var bluetoothState: BluetoothState = BluetoothState())

class PrivateTrainerViewModel() :
    ViewModel() {
    private val _privateTrainerState = MutableStateFlow(PrivateTrainerState())
    val privateTrainerState: StateFlow<PrivateTrainerState> = _privateTrainerState

    fun closeDetailScreen() {
    }
}
