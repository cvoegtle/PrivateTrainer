package org.voegtle.privatetrainer.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.voegtle.privatetrainer.BluetoothStatus
import org.voegtle.privatetrainer.business.PrivateTrainerState

@Composable
fun OverviewScreen(privateTrainerState: PrivateTrainerState) {
    Column (horizontalAlignment = Alignment.CenterHorizontally){
        BluetoothStateView(bluetoothState = privateTrainerState.bluetoothState)
        Spacer(Modifier.height(5.dp))
        DeviceStateView(deviceSettings = privateTrainerState.deviceSetting)

    }
}
