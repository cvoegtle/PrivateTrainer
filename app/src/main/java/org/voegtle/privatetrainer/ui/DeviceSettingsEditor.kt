package org.voegtle.privatetrainer.ui

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import org.voegtle.privatetrainer.R
import org.voegtle.privatetrainer.business.DeviceSettings
import org.voegtle.privatetrainer.business.PrivateTrainerStore
import org.voegtle.privatetrainer.business.SettingType
import org.voegtle.privatetrainer.business.SettingsRanges
import org.voegtle.privatetrainer.ui.controls.ControlRow
import org.voegtle.privatetrainer.ui.controls.FloatSliderRow
import org.voegtle.privatetrainer.ui.controls.IntSliderRow
import org.voegtle.privatetrainer.ui.controls.NameEditRow
import java.util.*

@Composable
fun DeviceSettingsEditor() {
    val context = LocalContext.current

    var deviceSettings: DeviceSettings by remember {
        mutableStateOf(retrieveCurrentDeviceSettings(context))
    }
    var selectedType by remember { mutableStateOf(SettingType.mode) }
    val settingsRanges = SettingsRanges()

    Column {
        NameEditRow(
            name = deviceSettings.name,
            favorite = deviceSettings.id != null,
            onNameChange = { name -> deviceSettings = deviceSettings.copy(name = name) },
            onFocusLost = { storeCurrentDeviceSettings(context, deviceSettings) },
            onFavoriteClicked = { favorite ->
                deviceSettings = deviceSettings.copy(
                    id = if (favorite) UUID.randomUUID().toString() else null
                )
                storeCurrentDeviceSettings(context, deviceSettings)
            })
        ControlRow(
            SettingType.mode,
            R.string.setting_mode,
            deviceSettings.mode.toString(),
            selectedType,
            { type -> selectedType = type }
        )
        ControlRow(
            SettingType.strength,
            R.string.setting_strength,
            renderPercent(deviceSettings.strength),
            selectedType,
            { type -> selectedType = type }
        )
        ControlRow(
            SettingType.interval,
            R.string.setting_interval,
            renderSeconds(deviceSettings.interval),
            selectedType,
            { type -> selectedType = type }
        )
        when (selectedType) {
            SettingType.mode -> IntSliderRow(
                value = deviceSettings.mode,
                range = settingsRanges.mode,
                onChange = { mode ->
                    deviceSettings = deviceSettings.copy(mode = mode)
                    storeCurrentDeviceSettings(context, deviceSettings)
                })
            SettingType.strength -> FloatSliderRow(
                value = deviceSettings.strength,
                range = settingsRanges.strength,
                onChange = { strength ->
                    deviceSettings = deviceSettings.copy(strength = strength)
                    storeCurrentDeviceSettings(context, deviceSettings)
                })
            SettingType.interval -> FloatSliderRow(
                value = deviceSettings.interval,
                range = settingsRanges.interval,
                onChange = { interval ->
                    deviceSettings = deviceSettings.copy(interval = interval)
                    storeCurrentDeviceSettings(context, deviceSettings)
                }
            )
        }
    }
}

private fun retrieveCurrentDeviceSettings(context: Context): DeviceSettings {
    return PrivateTrainerStore(context).retrieveCurrentSettings()
}

private fun storeCurrentDeviceSettings(context: Context, currentDeviceSettings: DeviceSettings) {
    PrivateTrainerStore(context).storeCurrentSettings(currentDeviceSettings)
}

fun renderSeconds(interval: Float) = "${interval}s"
fun renderPercent(value: Float?) =
    if (value == null) "- %" else String.format("%.0f", value * 100) + "%"
