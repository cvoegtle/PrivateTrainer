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
fun DeviceStateView(deviceSettings: DeviceSettings) {
    var selectedType by remember { mutableStateOf(SettingType.strength) }
    val settingsRanges = SettingsRanges()

    Column {
        ControlRow(
            SettingType.mode,
            R.string.setting_mode,
            deviceSettings.mode.toString(),
            selectedType,
            { selectedType = it }
        )
        ControlRow(
            SettingType.strength,
            R.string.setting_strength,
            renderPercent(deviceSettings.strength),
            selectedType,
            { selectedType = it }
        )
        ControlRow(
            SettingType.interval,
            R.string.setting_interval,
            renderSeconds(deviceSettings.interval),
            selectedType,
            { selectedType = it }
        )
        when (selectedType) {
            SettingType.mode -> IntSliderRow(
                value = deviceSettings.mode,
                range = settingsRanges.mode,
                onChange = { deviceSettings.mode = it })
            SettingType.strength -> FloatSliderRow(
                value = deviceSettings.strength,
                range = settingsRanges.strength,
                onChange = { deviceSettings.strength = it })
            SettingType.interval -> FloatSliderRow(
                value = deviceSettings.interval,
                range = settingsRanges.interval,
                onChange = { deviceSettings.interval = it }
            )
        }
    }
}

fun renderSeconds(interval: Float) = "${interval}s"
fun renderPercent(value: Float) = String.format("%.0f", value * 100) + "%"
