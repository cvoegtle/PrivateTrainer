package org.voegtle.privatetrainer.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.PowerInput
import androidx.compose.material.icons.filled.PowerOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
    onSendToDeviceClicked: (command: PrivateTrainerCommand) -> Unit
) {
    val bluetoothMutableState: MutableState<BluetoothState> =
        remember { mutableStateOf(BluetoothState()) }
    val bluetoothState = bluetoothMutableState.value
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
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
                onButtonClick = fun(command) {
                    onSendToDeviceClicked(command)
                })
        }

    }
}

@Composable
private fun BluetoothDeviceRows(
    bluetoothState: BluetoothState,
    onButtonClick: (command: PrivateTrainerCommand) -> Unit = {}
) {
    val context = LocalContext.current
    val powerIcon = if (bluetoothState.powerOn) Icons.Filled.PowerOff else Icons.Filled.Power
    val powerCommand = if (bluetoothState.powerOn) PrivateTrainerCommand.off else PrivateTrainerCommand.on
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            BluetoothDevice(
                bluetoothState = bluetoothState,
                modifier = Modifier.fillMaxWidth(0.85f)
            )
            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    onButtonClick(powerCommand)
                },
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Icon(
                    imageVector = powerIcon,
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
            TextButton( colors = ButtonDefaults.filledTonalButtonColors(),
                onClick = {
                    onButtonClick(PrivateTrainerCommand.update)
                }) {
                Text(text = context.getString(R.string.update_device_settings))
            }
        }
        Row(Modifier.fillMaxWidth()) {
            TextButton( colors = ButtonDefaults.filledTonalButtonColors(),
                onClick = {
                    onButtonClick(PrivateTrainerCommand.requestBatteryStatus)
                }) {
                Text(text = context.getString(R.string.request_battery_status))
            }
        }
        Row(Modifier.fillMaxWidth()) {
            val textId = if (bluetoothState.notificationsEnabled) R.string.disable_notifications else R.string.enable_notifications
            TextButton( colors = ButtonDefaults.filledTonalButtonColors(),
                onClick = {
                    onButtonClick(PrivateTrainerCommand.toggleNotification)
                }) {
                Text(text = context.getString(textId))
            }
        }
    }

}
