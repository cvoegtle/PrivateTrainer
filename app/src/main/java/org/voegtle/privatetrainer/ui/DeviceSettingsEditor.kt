package org.voegtle.privatetrainer.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import org.voegtle.privatetrainer.R
import org.voegtle.privatetrainer.business.DeviceSettings
import org.voegtle.privatetrainer.business.SettingType
import org.voegtle.privatetrainer.business.SettingsRanges
import org.voegtle.privatetrainer.ui.controls.ControlRow
import org.voegtle.privatetrainer.ui.controls.FloatSliderRow
import org.voegtle.privatetrainer.ui.controls.IntSliderRow
import org.voegtle.privatetrainer.ui.controls.NameEditRow
import java.util.*

@Composable
fun DeviceSettingsEditor(deviceSettings: DeviceSettings, onValueChange: (deviceSettings: DeviceSettings) -> Unit) {
    val context = LocalContext.current

    var selectedType by remember { mutableStateOf(SettingType.mode) }
    val settingsRanges = SettingsRanges()

    Column {
        NameEditRow(
            name = deviceSettings.name,
            favorite = deviceSettings.id != null,
            onNameChange = { name -> onValueChange(deviceSettings.copy(name = name)) },
            onFocusLost = { onValueChange(deviceSettings) }, // pretend something changed
            onFavoriteClicked = { favorite ->onValueChange(deviceSettings.copy(
                    id = if (favorite) UUID.randomUUID().toString() else null
                ))
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
                    onValueChange(deviceSettings.copy(mode = mode))
                })
            SettingType.strength -> FloatSliderRow(
                value = deviceSettings.strength,
                range = settingsRanges.strength,
                onChange = { strength ->
                    onValueChange(deviceSettings.copy(strength = strength))
                })
            SettingType.interval -> FloatSliderRow(
                value = deviceSettings.interval,
                range = settingsRanges.interval,
                onChange = { interval ->
                    onValueChange(deviceSettings.copy(interval = interval))
                }
            )
        }
    }
}

fun renderSeconds(interval: Float) = "${interval}s"
fun renderPercent(value: Float?) =
    if (value == null) "- %" else String.format("%.0f", value * 100) + "%"
