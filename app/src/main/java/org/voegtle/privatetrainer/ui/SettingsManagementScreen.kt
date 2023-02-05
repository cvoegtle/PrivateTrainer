package org.voegtle.privatetrainer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.voegtle.privatetrainer.business.BluetoothState
import org.voegtle.privatetrainer.business.DeviceSettings

@Composable
fun SettingsManagementScreen(settingsList: List<DeviceSettings>) {
    Column () {
        settingsList.forEach { settings -> DeviceSettingsView(settings) }
    }
}

@Preview
@Composable
fun SettingsManagementScreenPreview() {
    val deviceSettings = listOf(
        DeviceSettings(name = "Soft and Slow", mode = 2, strength = 0.2f, interval = 120.0f),
        DeviceSettings(name = "Strong and Slow", mode = 2, strength = 0.9f, interval = 120.0f),
        DeviceSettings(name = "Full Power", mode = 2, strength = 1.0f, interval = 2.0f)
    )
    SettingsManagementScreen(settingsList = deviceSettings)
}
