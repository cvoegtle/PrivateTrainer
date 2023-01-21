package org.voegtle.privatetrainer.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.voegtle.privatetrainer.business.BluetoothState

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun OverviewScreen(bluetoothState: BluetoothState, onSearchDeviceClicked: () -> Unit) {

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        BluetoothStateView(
            bluetoothState = bluetoothState,
            onSearchDeviceClicked = onSearchDeviceClicked
        )
        Spacer(Modifier.height(5.dp))
        DeviceStateView()

    }
}
