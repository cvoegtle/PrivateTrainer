package org.voegtle.privatetrainer.ui

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.voegtle.privatetrainer.business.BluetoothState
import org.voegtle.privatetrainer.business.DeviceSettings
import org.voegtle.privatetrainer.business.PrivateTrainerCommand
import org.voegtle.privatetrainer.business.PrivateTrainerStore

@Composable
fun OverviewScreen(
    onSearchDeviceClicked: (state: MutableState<BluetoothState>) -> Unit,
    onSendToDeviceClicked: (command: PrivateTrainerCommand,
                            settings: DeviceSettings) -> Unit
) {
    val context = LocalContext.current

    val deviceSettings: MutableState<DeviceSettings> = remember {
        mutableStateOf(retrieveCurrentDeviceSettings(context))
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        BluetoothStateView(
            onSearchDeviceClicked = onSearchDeviceClicked,
            onSendToDeviceClicked = fun (command: PrivateTrainerCommand) {onSendToDeviceClicked(command, deviceSettings.value)})
        Spacer(Modifier.height(5.dp))
        DeviceSettingsEditor(deviceSettings.value, onValueChange = fun(updatedSettings: DeviceSettings) {
            deviceSettings.value = updatedSettings
            storeCurrentDeviceSettings(context, updatedSettings)
        })
    }
}

fun retrieveCurrentDeviceSettings(context: Context): DeviceSettings {
    return PrivateTrainerStore(context).retrieveCurrentSettings()
}

private fun storeCurrentDeviceSettings(context: Context, currentDeviceSettings: DeviceSettings) {
    PrivateTrainerStore(context).storeSettings(currentDeviceSettings)
}
