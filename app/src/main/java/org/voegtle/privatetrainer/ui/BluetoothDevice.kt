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
fun BluetoothDevice(bluetoothState: BluetoothState, modifier: Modifier) {
    val context = LocalContext.current

    Column(modifier = modifier) {
        val selectedDevice = bluetoothState.selectedDevice
        selectedDevice?.let {
            val connectionText =
                context.getString(if (it.connected) R.string.connected else R.string.not_connected)
            Text(
                it.name,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.inverseOnSurface
            )
            Row() {
                Text(
                    context.getString(R.string.battery) + ": " + renderPercent(it.batteryLevel),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    connectionText,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )
            }
        }
    }
}
