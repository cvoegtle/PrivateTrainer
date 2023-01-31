package org.voegtle.privatetrainer.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.voegtle.privatetrainer.business.BluetoothState

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun OverviewScreen(onSearchDeviceClicked: (state: MutableState<BluetoothState>) -> Unit) {
    val bluetoothState: MutableState<BluetoothState> =
        remember { mutableStateOf(BluetoothState()) }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        BluetoothStateView(
            bluetoothState = bluetoothState.value,
            onSearchDeviceClicked = { onSearchDeviceClicked(bluetoothState) }
        )
        Spacer(Modifier.height(5.dp))
        DeviceStateView()

    }
}
