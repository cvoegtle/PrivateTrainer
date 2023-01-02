package org.voegtle.privatetrainer.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.voegtle.privatetrainer.R
import org.voegtle.privatetrainer.business.BluetoothConnectionStatus
import org.voegtle.privatetrainer.business.BluetoothConnectionStatus.*
import org.voegtle.privatetrainer.business.BluetoothState
import org.voegtle.privatetrainer.ui.controls.ErrorMessage

@Composable
fun BluetoothStateView(bluetoothState: BluetoothState) {
    val context = LocalContext.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        color = MaterialTheme.colorScheme.inverseSurface
    ) {
        if (bluetoothState.connectionStatus == not_supported) {
            ErrorMessage(message = context.getString(R.string.error_bluetooth_not_supported))
        } else if (bluetoothState.connectionStatus == disabled) {
            ErrorMessage(message = context.getString(R.string.error_bluetooth_disabled))
        } else if (bluetoothState.connectionStatus == permission_denied) {
            ErrorMessage(message = context.getString(R.string.error_bluetooth_access_denied))
        } else if (bluetoothState.selectedDevice == null) {
            ErrorMessage(message = context.getString(R.string.error_bluetooth_device_not_connected))
        } else {
            Row(modifier = Modifier.fillMaxWidth()) {
                BluetoothDevice(
                    bluetoothState = bluetoothState,
                    modifier = Modifier.fillMaxWidth(0.85f)
                )
                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { /*TODO*/ },
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
        }

    }
}
