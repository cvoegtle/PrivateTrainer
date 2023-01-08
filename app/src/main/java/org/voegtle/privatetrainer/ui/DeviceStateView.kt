package org.voegtle.privatetrainer.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import org.voegtle.privatetrainer.R
import org.voegtle.privatetrainer.business.DeviceSettings
import org.voegtle.privatetrainer.business.SettingType
import org.voegtle.privatetrainer.business.SettingsRanges
import org.voegtle.privatetrainer.ui.controls.ControlRow
import org.voegtle.privatetrainer.ui.controls.FloatSliderRow
import org.voegtle.privatetrainer.ui.controls.IntSliderRow

@Composable
fun DeviceStateView(deviceSettings: DeviceSettings, onChange: (DeviceSettings) -> Unit) {
    var selectedType by remember { mutableStateOf(SettingType.strength) }
    val settingsRanges = SettingsRanges()

    Column {
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
                onChange = { mode -> deviceSettings.mode = mode; onChange(deviceSettings) })
            SettingType.strength -> FloatSliderRow(
                value = deviceSettings.strength,
                range = settingsRanges.strength,
                onChange = { strength -> deviceSettings.strength = strength; onChange(deviceSettings) })
            SettingType.interval -> FloatSliderRow(
                value = deviceSettings.interval,
                range = settingsRanges.interval,
                onChange = { interval -> deviceSettings.interval = interval; onChange(deviceSettings) }
            )
        }
    }
}

fun renderSeconds(interval: Float) = "${interval}s"
fun renderPercent(value: Float) = String.format("%.0f", value * 100) + "%"
