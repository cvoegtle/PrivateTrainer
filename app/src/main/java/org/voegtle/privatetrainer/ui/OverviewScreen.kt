package org.voegtle.privatetrainer.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.voegtle.privatetrainer.business.PrivateTrainerViewModel

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalLifecycleComposeApi::class)
@Composable
fun OverviewScreen(privateTrainerViewModel: PrivateTrainerViewModel) {
    val bluetoothState by privateTrainerViewModel.bluetoothState.collectAsStateWithLifecycle()
    val deviceSettings by privateTrainerViewModel.deviceSettings.collectAsStateWithLifecycle()
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        BluetoothStateView(bluetoothState = bluetoothState)
        Spacer(Modifier.height(5.dp))
        DeviceStateView(
            deviceSettings = deviceSettings,
            { changedSetting -> privateTrainerViewModel.updateDeviceSetting(changedSetting) })

    }
}
