package org.voegtle.privatetrainer.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import org.voegtle.privatetrainer.R
import org.voegtle.privatetrainer.business.DeviceSettings
import org.voegtle.privatetrainer.business.SettingType
import org.voegtle.privatetrainer.business.SettingsRanges
import org.voegtle.privatetrainer.ui.controls.ControlRow
import org.voegtle.privatetrainer.ui.controls.FloatSliderRow
import org.voegtle.privatetrainer.ui.controls.IntSliderRow

@Composable
fun DeviceStateView() {
    var deviceSettingsState: DeviceSettings by rememberSaveable {
        mutableStateOf(DeviceSettings())
    }
    var selectedType by remember { mutableStateOf(SettingType.mode) }
    val settingsRanges = SettingsRanges()

    Column {
        ControlRow(
            SettingType.mode,
            R.string.setting_mode,
            deviceSettingsState.mode.toString(),
            selectedType,
            { type -> selectedType = type }
        )
        ControlRow(
            SettingType.strength,
            R.string.setting_strength,
            renderPercent(deviceSettingsState.strength),
            selectedType,
            { type -> selectedType = type }
        )
        ControlRow(
            SettingType.interval,
            R.string.setting_interval,
            renderSeconds(deviceSettingsState.interval),
            selectedType,
            { type -> selectedType = type }
        )
        when (selectedType) {
            SettingType.mode -> IntSliderRow(
                value = deviceSettingsState.mode,
                range = settingsRanges.mode,
                onChange = { mode -> deviceSettingsState = deviceSettingsState.copy(mode = mode) })
            SettingType.strength -> FloatSliderRow(
                value = deviceSettingsState.strength,
                range = settingsRanges.strength,
                onChange = { strength ->
                    deviceSettingsState = deviceSettingsState.copy(strength = strength)
                })
            SettingType.interval -> FloatSliderRow(
                value = deviceSettingsState.interval,
                range = settingsRanges.interval,
                onChange = { interval ->
                    deviceSettingsState = deviceSettingsState.copy(interval = interval)
                }
            )
        }
    }
}

fun renderSeconds(interval: Float) = "${interval}s"
fun renderPercent(value: Float?) =
    if (value == null) "- %" else String.format("%.0f", value * 100) + "%"
