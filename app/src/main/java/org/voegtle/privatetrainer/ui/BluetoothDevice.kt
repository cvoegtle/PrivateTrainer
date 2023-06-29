package org.voegtle.privatetrainer.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BluetoothSearching
import androidx.compose.material.icons.outlined.Grade
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.voegtle.privatetrainer.R
import org.voegtle.privatetrainer.business.BluetoothState
import org.voegtle.privatetrainer.business.DeviceStore
import org.voegtle.privatetrainer.business.PrivateTrainerDevice
import org.voegtle.privatetrainer.ui.controls.PrivateIconButton

@Composable
fun BluetoothDeviceRow(
    device: PrivateTrainerDevice,
    onEditClicked: (device: PrivateTrainerDevice) -> Unit
) {
    val context = LocalContext.current
    val givenName = device.givenName ?: context.getString(R.string.unknown_device)
    val autoConnectImage = if (device.autoConnect) Icons.Filled.Grade else Icons.Outlined.Grade
    val color =
        if (device.available) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
    val background =
        if (device.available) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant


    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = background
    ) {
        Row {
            PrivateIconButton(
                imageVector = Icons.Outlined.BluetoothSearching,
                onClick = {
                },
                id = R.string.connect_device,
                enabled = device.available
            )

            Button(
                onClick = { onEditClicked(device) },
                colors = ButtonDefaults.filledTonalButtonColors(containerColor = background)
            ) {
                Row() {
                    Icon(
                        imageVector = autoConnectImage,
                        contentDescription = stringResource(
                            id = R.string.auto_connect
                        ),
                        modifier = Modifier.height(45.dp).padding(end = 5.dp)
                    )

                    Column() {
                        Row() {
                            Text(
                                givenName,
                                style = MaterialTheme.typography.titleLarge,
                                color = color
                            )
                        }
                        Row() {
                            Text(
                                device.address,
                                style = MaterialTheme.typography.bodyMedium,
                                color = color
                            )
                        }
                    }
                }

            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun BluetoothDeviceStatus(bluetoothState: BluetoothState) {
    val context = LocalContext.current

    Column {
        val selectedDevice = bluetoothState.selectedDevice
        selectedDevice?.let {
            val connectionText =
                context.getString(if (it.connected) R.string.connected else R.string.not_connected)
            val lastErrorText =
                context.getString(R.string.last_error) + " " + (bluetoothState.lastStatus ?: "-")
            val lastErrorColor =
                if (bluetoothState.lastStatus == 0) MaterialTheme.colorScheme.inverseOnSurface else MaterialTheme.colorScheme.onError
            val lastWrittenText =
                context.getString(R.string.last_text) + ": " + (bluetoothState.lastWrittenValue
                    ?: "-")
            val lastNotification =
                context.getString(R.string.last_notification) + ": " + (bluetoothState.lastReceivedNotifications()
                    ?: "-")

            Row {
                Text(
                    it.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )
            }
            Row() {
                Text(
                    it.address,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )
            }
            Divider(
                color = MaterialTheme.colorScheme.inverseOnSurface,
                thickness = 1.dp
            )
            Row {
                Text(
                    context.getString(R.string.battery) + ": " + it.batteryLevel,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    connectionText,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    lastErrorText,
                    style = MaterialTheme.typography.titleSmall,
                    color = lastErrorColor
                )
            }
            Row {
                Text(
                    lastWrittenText,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )
            }
            Row {
                Text(
                    lastNotification,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )
            }
        }
    }
}
