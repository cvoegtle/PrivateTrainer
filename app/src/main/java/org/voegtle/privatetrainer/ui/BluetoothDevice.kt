package org.voegtle.privatetrainer.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.voegtle.privatetrainer.R
import org.voegtle.privatetrainer.business.BluetoothState

@SuppressLint("MissingPermission")
@Composable
fun BluetoothDevice(bluetoothState: BluetoothState) {
    val context = LocalContext.current

    Column() {
        val selectedDevice = bluetoothState.selectedDevice
        selectedDevice?.let {
            val connectionText =
                context.getString(if (it.connected) R.string.connected else R.string.not_connected)
            val lastErrorText =
                context.getString(R.string.last_error) + " " + (bluetoothState.lastStatus ?: "-")
            val lastErrorColor =
                if (bluetoothState.lastStatus == 0) MaterialTheme.colorScheme.inverseOnSurface else MaterialTheme.colorScheme.onError
            val lastWrittenText =
                context.getString(R.string.last_text) + ": " + (bluetoothState.lastWrittenValue ?: "-")
            val lastNotification =
                context.getString(R.string.last_notification) + ": " + (bluetoothState.lastReceivedNotifications() ?: "-")

            Text(
                it.name,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.inverseOnSurface
            )
            Row() {
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
            Row() {
                Text(
                    lastWrittenText,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )
            }
            Row() {
                Text(
                    lastNotification,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )
            }
        }
    }
}
