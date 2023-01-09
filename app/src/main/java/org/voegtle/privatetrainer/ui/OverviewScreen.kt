package org.voegtle.privatetrainer.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.voegtle.privatetrainer.business.BluetoothState

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalLifecycleComposeApi::class)
@Composable
fun OverviewScreen(bluetoothState : BluetoothState) {

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        BluetoothStateView(bluetoothState = bluetoothState)
        Spacer(Modifier.height(5.dp))
        DeviceStateView()

    }
}
