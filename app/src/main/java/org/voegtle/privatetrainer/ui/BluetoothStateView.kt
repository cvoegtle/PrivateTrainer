package org.voegtle.privatetrainer.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.voegtle.privatetrainer.R
import org.voegtle.privatetrainer.business.BluetoothConnectionStatus.*
import org.voegtle.privatetrainer.business.BluetoothState
import org.voegtle.privatetrainer.business.PrivateTrainerCommand
import org.voegtle.privatetrainer.ui.controls.ErrorView

@Composable
fun BluetoothStateView(
    onSearchDeviceClicked: (MutableState<BluetoothState>) -> Unit,
    onSendToDeviceClicked: (command: PrivateTrainerCommand, MutableState<BluetoothState>) -> Unit
) {
    val bluetoothMutableState: MutableState<BluetoothState> =
        remember { mutableStateOf(BluetoothState()) }
    val bluetoothState = bluetoothMutableState.value
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        color = MaterialTheme.colorScheme.inverseSurface
    ) {
        if (bluetoothState.connectionStatus == not_supported) {
            ErrorView(messageId = R.string.error_bluetooth_not_supported)
        } else if (bluetoothState.connectionStatus == disabled) {
            ErrorView(
                messageId = R.string.error_bluetooth_disabled,
                onButtonClick = { onSearchDeviceClicked(bluetoothMutableState) }
            )
        } else if (bluetoothState.connectionStatus == permission_denied) {
            ErrorView(
                messageId = R.string.error_bluetooth_access_denied,
                onButtonClick = { onSearchDeviceClicked(bluetoothMutableState) }
            )
        } else if (bluetoothState.selectedDevice == null) {
            ErrorView(
                messageId = R.string.error_bluetooth_device_not_connected,
                onButtonClick = { onSearchDeviceClicked(bluetoothMutableState) }
            )
        } else {
            BluetoothDeviceRows(
                bluetoothState,
                onButtonClick = fun (command) {onSendToDeviceClicked(command, bluetoothMutableState) })
        }

    }
}

@Composable
private fun BluetoothDeviceRows(bluetoothState: BluetoothState, onButtonClick: (command: PrivateTrainerCommand) -> Unit = {}) {
    Row(modifier = Modifier.fillMaxWidth()) {
        BluetoothDevice(
            bluetoothState = bluetoothState,
            modifier = Modifier.fillMaxWidth(0.85f)
        )
        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = {
                onButtonClick(PrivateTrainerCommand.update)
            },
            colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = stringResource(
                    id = R.string.device_start
                ),
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterVertically)
            )
        }
    }
    Row(Modifier.fillMaxWidth()) {
        Button(onClick = {
            onButtonClick(PrivateTrainerCommand.enableNotification)
        }) {

        }
    }
}
