package org.voegtle.privatetrainer.ui

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import org.voegtle.privatetrainer.business.DeviceSettings
import org.voegtle.privatetrainer.business.PrivateTrainerStore

@Composable
fun FavoriteSettingsManagementScreen(settingsList: List<DeviceSettings>) {
    val context = LocalContext.current

    var favoriteSettingsList: List<DeviceSettings> by remember {
        mutableStateOf(retrieveFavoriteDeviceSettings(context))
    }
    Column () {
        favoriteSettingsList.forEach { settings -> DeviceSettingsView(settings) }
    }
}

fun retrieveFavoriteDeviceSettings(context: Context): List<DeviceSettings> {
    return PrivateTrainerStore(context).retrieveFavoriteSettings()
}

@Preview
@Composable
fun SettingsManagementScreenPreview() {
    val deviceSettings = listOf(
        DeviceSettings(name = "Soft and Slow", mode = 2, strength = 2, interval = 120),
        DeviceSettings(name = "Strong and Slow", mode = 2, strength = 9, interval = 120),
        DeviceSettings(name = "Full Power", mode = 2, strength = 10, interval = 2)
    )
    FavoriteSettingsManagementScreen(settingsList = deviceSettings)
}
