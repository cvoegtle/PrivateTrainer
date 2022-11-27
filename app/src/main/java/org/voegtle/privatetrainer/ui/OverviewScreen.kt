package org.voegtle.privatetrainer.ui

import androidx.compose.runtime.Composable
import org.voegtle.privatetrainer.BluetoothStatus
import org.voegtle.privatetrainer.business.PrivateTrainerState

@Composable
fun OverviewScreen(privateTrainerState: PrivateTrainerState) {
    BluetoothStateView(bluetoothState = privateTrainerState.bluetoothState)
}
