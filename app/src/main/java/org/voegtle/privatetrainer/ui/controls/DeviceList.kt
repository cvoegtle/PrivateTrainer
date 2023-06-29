package org.voegtle.privatetrainer.ui.controls

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.voegtle.privatetrainer.R
import org.voegtle.privatetrainer.business.PrivateTrainerDevice
import org.voegtle.privatetrainer.business.PrivateTrainerDeviceContainer
import org.voegtle.privatetrainer.ui.BluetoothDeviceRow
import org.voegtle.privatetrainer.ui.updateAndStoreDevices

@Composable
fun DeviceControlView(
    messageId: Int,
    showHidden: Boolean?,
    onSearchClicked: () -> Unit,
    onToggleHiddenState: () -> Unit
) {
    val context = LocalContext.current

    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            context.getString(messageId),
            color = MaterialTheme.colorScheme.inverseOnSurface,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(Dp(8f))
        )

        Spacer(modifier = Modifier.width(8.dp))

        PrivateIconButton(
            imageVector = Icons.Filled.Replay,
            onClick = onSearchClicked,
            id = R.string.search_device,
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterVertically)
        )

        if (showHidden != null) {
            val textId = if (showHidden) R.string.active_devices else R.string.all_devices

            Button(onClick = onToggleHiddenState) {
                Text(context.getString(textId))
            }
        }
    }

}

@Composable
fun BluetoothDeviceList(devices: MutableState<PrivateTrainerDeviceContainer>) {
    val deviceInEdit: MutableState<PrivateTrainerDevice?> = remember { mutableStateOf(null) }
    val privateTrainerDevices = devices.value.copy()
    Column() {
        privateTrainerDevices.devices.values.sortedBy { device -> !device.available }
            .filter { device -> privateTrainerDevices.showHiddenDevices == true || !device.hidden }
            .forEach { device ->
                BluetoothDeviceRow(device = device,
                    onEditClicked = {
                        deviceInEdit.value = it.copy()
                    }
                )
            }
    }

    deviceInEdit.value?.let { it ->
        val context = LocalContext.current
        val givenName = it.givenName ?: context.getString(R.string.unknown_device)
        val nameUnderConstruction = remember { mutableStateOf(givenName) }
        val autoConnectUnderConstruction = remember { mutableStateOf(it.autoConnect) }
        val hiddenUnderConstruction = remember { mutableStateOf(it.hidden) }

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
                        it.autoConnect =
                            autoConnectUnderConstruction.value && !hiddenUnderConstruction.value
                        it.hidden = hiddenUnderConstruction.value
                        updateAndStoreDevices(context, privateTrainerDevices, it)

                        deviceInEdit.value = null
                        devices.value = privateTrainerDevices
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
                            onCheckedChange = { changedAutoConnect ->
                                autoConnectUnderConstruction.value = changedAutoConnect
                            })
                        Text(context.getString(R.string.auto_connect))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = hiddenUnderConstruction.value,
                            onCheckedChange = { changedHidden ->
                                hiddenUnderConstruction.value = changedHidden
                            })
                        Text(context.getString(R.string.hide))
                    }
                }
            },
        )
    }
}
