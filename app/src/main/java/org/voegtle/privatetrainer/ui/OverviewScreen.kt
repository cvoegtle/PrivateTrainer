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

@Composable
fun OverviewScreen(onSearchDeviceClicked: (state: MutableState<BluetoothState>) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        BluetoothStateView(
            onSearchDeviceClicked = onSearchDeviceClicked
        )
        Spacer(Modifier.height(5.dp))
        DeviceSettingsEditor()

    }
}
