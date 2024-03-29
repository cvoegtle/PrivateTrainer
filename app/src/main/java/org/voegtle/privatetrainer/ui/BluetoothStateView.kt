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
import androidx.compose.ui.unit.dp
import org.voegtle.privatetrainer.R
import org.voegtle.privatetrainer.business.BluetoothConnectionStatus.*
import org.voegtle.privatetrainer.business.BluetoothState
import org.voegtle.privatetrainer.business.DeviceStore
import org.voegtle.privatetrainer.business.PrivateTrainerCommand
import org.voegtle.privatetrainer.business.PrivateTrainerDevice
import org.voegtle.privatetrainer.business.PrivateTrainerDeviceContainer
import org.voegtle.privatetrainer.ui.controls.BluetoothDeviceList
import org.voegtle.privatetrainer.ui.controls.DeviceControlView
import org.voegtle.privatetrainer.ui.controls.ErrorView
import org.voegtle.privatetrainer.ui.controls.PrivateIconButton

@Composable
fun BluetoothStateView(
    onSearchDeviceClicked: (MutableState<BluetoothState>, MutableState<PrivateTrainerDeviceContainer>) -> Unit,
    onBindDeviceClicked: (bluetoothState: MutableState<BluetoothState>, device: PrivateTrainerDevice) -> Unit,
    onSendToDeviceClicked: (command: PrivateTrainerCommand) -> Unit
) {
    val context = LocalContext.current

    val bluetoothMutableState: MutableState<BluetoothState> =
        remember { mutableStateOf(BluetoothState()) }

    val devices: MutableState<PrivateTrainerDeviceContainer> = remember {
        mutableStateOf(retrieveDevices(context))
    }

    var bluetoothState = bluetoothMutableState.value
    if (bluetoothState.connectionStatus == not_initialised) {
        bluetoothState = bluetoothState.copy(connectionStatus = not_connected)
        bluetoothMutableState.value = bluetoothState
        onSearchDeviceClicked(bluetoothMutableState, devices)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.inverseSurface
    ) {
        Column {
            Row {
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
                } else {
                    val messsageId =
                        if (bluetoothState.connectionStatus == device_found) R.string.device_found else R.string.search_device
                    DeviceControlView(
                        messageId = messsageId,
                        showHidden = devices.value.showHiddenDevices(),
                        onSearchClicked = { onSearchDeviceClicked(bluetoothMutableState, devices) },
                        onToggleHiddenState = {
                            val updatedDevices = devices.value.copy()
                            updatedDevices.toggleShowHidden()
                            devices.value = updatedDevices
                        })
                }
            }
            Row {
                BluetoothDeviceList(
                    devices,
                    { device -> onBindDeviceClicked(bluetoothMutableState, device) })
            }
            if (bluetoothState.connectionStatus == device_bound) {
                BluetoothDeviceState(
                    bluetoothState,
                    onButtonClick = fun(command) {
                        onSendToDeviceClicked(command)
                    })
            }
        }

    }
}


@Composable
private fun BluetoothDeviceState(
    bluetoothState: BluetoothState,
    onButtonClick: (command: PrivateTrainerCommand) -> Unit
) {
    val context = LocalContext.current
    val colorOnOffIndicator = if (bluetoothState.powerOn) Color.Yellow else Color.Gray
    val powerCommand =
        if (bluetoothState.powerOn) PrivateTrainerCommand.off else PrivateTrainerCommand.on
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(start = 5.dp)) {
            BluetoothDeviceStatus(bluetoothState = bluetoothState)
        }
        Row(Modifier.fillMaxWidth().padding(start = 5.dp)) {
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

            Spacer(modifier = Modifier.width(2.dp))

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

            TextButton(colors = ButtonDefaults.filledTonalButtonColors(),
                onClick = {
                    onButtonClick(PrivateTrainerCommand.update)
                }) {
                Text(text = context.getString(R.string.update_device_settings))
            }
        }
        Row(Modifier.fillMaxWidth().padding(start = 5.dp)) {
            TextButton(colors = ButtonDefaults.filledTonalButtonColors(),
                onClick = {
                    onButtonClick(PrivateTrainerCommand.requestBatteryStatus)
                }) {
                Text(text = context.getString(R.string.request_battery_status))
            }
        }
    }

}

fun updateAndStoreDevices(
    context: Context,
    privateTrainerDevices: PrivateTrainerDeviceContainer,
    changedDevice: PrivateTrainerDevice,
) {
    if (changedDevice.autoConnect) {
        privateTrainerDevices.devices.filterValues { it.autoConnect }
            .forEach { (key, device) ->
                device.autoConnect = false
                storeDevice(context, device)
            }
    }
    privateTrainerDevices.put(changedDevice)
    storeDevice(context, changedDevice)
}

fun retrieveDevices(context: Context): PrivateTrainerDeviceContainer {
    return DeviceStore(context).retrieveDevices()
}

fun storeDevice(context: Context, device: PrivateTrainerDevice) {
    DeviceStore(context).store(device)
}
