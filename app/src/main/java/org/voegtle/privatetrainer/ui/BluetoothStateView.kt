package org.voegtle.privatetrainer.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.voegtle.privatetrainer.R
import org.voegtle.privatetrainer.business.BluetoothConnectionStatus.*
import org.voegtle.privatetrainer.business.BluetoothState
import org.voegtle.privatetrainer.business.DeviceStore
import org.voegtle.privatetrainer.business.PrivateTrainerCommand
import org.voegtle.privatetrainer.business.PrivateTrainerDevice
import org.voegtle.privatetrainer.business.PrivateTrainerDeviceContainer
import org.voegtle.privatetrainer.ui.controls.ErrorView
import org.voegtle.privatetrainer.ui.controls.PrivateIconButton

@Composable
fun BluetoothStateView(
    onSearchDeviceClicked: (MutableState<BluetoothState>, MutableState<PrivateTrainerDeviceContainer>) -> Unit,
    onSendToDeviceClicked: (command: PrivateTrainerCommand) -> Unit
) {
    val context = LocalContext.current

    val bluetoothMutableState: MutableState<BluetoothState> =
        remember { mutableStateOf(BluetoothState()) }

    val devices: MutableState<PrivateTrainerDeviceContainer> = remember {
        mutableStateOf(retrieveDevices(context))
    }

    val bluetoothState = bluetoothMutableState.value
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.inverseSurface
    ) {
        if (bluetoothState.connectionStatus == not_supported) {
            ErrorView(messageId = R.string.error_bluetooth_not_supported)
        } else if (bluetoothState.connectionStatus == disabled) {
            ErrorView(
                messageId = R.string.error_bluetooth_disabled,
                onButtonClick = { onSearchDeviceClicked(bluetoothMutableState, devices) }
            )
        } else if (bluetoothState.connectionStatus == permission_denied) {
            ErrorView(
                messageId = R.string.error_bluetooth_access_denied,
                onButtonClick = { onSearchDeviceClicked(bluetoothMutableState, devices) }
            )
        } else if (devices.value.isEmpty()) {
            ErrorView(
                messageId = R.string.error_bluetooth_device_not_connected,
                onButtonClick = { onSearchDeviceClicked(bluetoothMutableState, devices) }
            )
        } else {
            BluetoothDeviceList(devices)
            /*            BluetoothDeviceState(
                            bluetoothState,
                            onButtonClick = fun(command) {
                                onSendToDeviceClicked(command)
                            },
                            onSearchClicked = { onSearchDeviceClicked(bluetoothMutableState, devices) })

             */
        }

    }
}

@Composable
private fun BluetoothDeviceList(devices: MutableState<PrivateTrainerDeviceContainer>) {
    val deviceInEdit: MutableState<PrivateTrainerDevice?> = remember { mutableStateOf(null) }
    val privateTrainerDevices = devices.value
    Column() {
        privateTrainerDevices.devices.values.forEach { device ->
            BluetoothDeviceRow(device = device,
                onEditClicked = {
                    deviceInEdit.value = it
                }
            )
        }
    }

    deviceInEdit.value?.let { it ->
        val context = LocalContext.current
        val givenName = it.givenName ?: context.getString(R.string.unknown_device)
        val nameUnderConstruction = remember { mutableStateOf(givenName) }
        val autoConnectUnderConstruction = remember { mutableStateOf(it.autoConnect) }

        AlertDialog(
            onDismissRequest = { deviceInEdit.value = null },
            dismissButton = {
                Button(onClick = { deviceInEdit.value = null }) {
                    Text(context.getString(R.string.cancel))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        it.givenName = nameUnderConstruction.value
                        it.autoConnect = autoConnectUnderConstruction.value
                        privateTrainerDevices.put(it)
                        storeDevice(context, it)
                        deviceInEdit.value = null
                        devices.value = privateTrainerDevices.copy()
                    }) {
                    Text(context.getString(R.string.submit))
                }
            },
            title = { Text(it.address) },
            text = {
                Column {
                    Row {
                        TextField(
                            value = nameUnderConstruction.value,
                            onValueChange = { changedName ->
                                nameUnderConstruction.value = changedName
                            })
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = autoConnectUnderConstruction.value,
                            onCheckedChange = { changedFavorite ->
                                autoConnectUnderConstruction.value = changedFavorite
                            })
                        Text(context.getString(R.string.auto_connect))
                    }
                }
            },
        )
    }
}

@Composable
private fun BluetoothDeviceState(
    bluetoothState: BluetoothState,
    onButtonClick: (command: PrivateTrainerCommand) -> Unit,
    onSearchClicked: () -> Unit
) {
    val context = LocalContext.current
    val colorOnOffIndicator = if (bluetoothState.powerOn) Color.Yellow else Color.Gray
    val powerCommand =
        if (bluetoothState.powerOn) PrivateTrainerCommand.off else PrivateTrainerCommand.on
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            BluetoothDeviceStatus(bluetoothState = bluetoothState)
        }
        Row(Modifier.fillMaxWidth()) {
            bluetoothState.selectedDevice?.let {
                if (!it.connected) {
                    Spacer(modifier = Modifier.width(8.dp))
                    PrivateIconButton(
                        imageVector = Icons.Filled.Replay,
                        id = R.string.search_device,
                        onClick = onSearchClicked,
                        modifier = Modifier
                            .fillMaxHeight()
                            .align(Alignment.CenterVertically)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.width(45.dp),
                shape = RoundedCornerShape(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Lightbulb,
                    contentDescription = stringResource(
                        id = R.string.search_device
                    ),
                    tint = colorOnOffIndicator,
                    modifier = Modifier.height(45.dp)
                )
            }

            Spacer(modifier = Modifier.width(2.dp))

            PrivateIconButton(
                imageVector = Icons.Outlined.PowerSettingsNew,
                onClick = {
                    onButtonClick(powerCommand)
                },
                id = R.string.search_device,
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterVertically)
            )
        }
        Row(Modifier.fillMaxWidth()) {
            TextButton(colors = ButtonDefaults.filledTonalButtonColors(),
                onClick = {
                    onButtonClick(PrivateTrainerCommand.update)
                }) {
                Text(text = context.getString(R.string.update_device_settings))
            }
        }
        Row(Modifier.fillMaxWidth()) {
            TextButton(colors = ButtonDefaults.filledTonalButtonColors(),
                onClick = {
                    onButtonClick(PrivateTrainerCommand.requestBatteryStatus)
                }) {
                Text(text = context.getString(R.string.request_battery_status))
            }
        }
    }

}

fun retrieveDevices(context: Context): PrivateTrainerDeviceContainer {
    return DeviceStore(context).retrieveDevices()
}

fun storeDevice(context: Context, device: PrivateTrainerDevice) {
    DeviceStore(context).store(device)
}
