package org.voegtle.privatetrainer.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import org.voegtle.privatetrainer.R
import org.voegtle.privatetrainer.business.DeviceSettings
import org.voegtle.privatetrainer.ui.controls.ControlRow
import org.voegtle.privatetrainer.ui.controls.ControlType

@Composable
fun DeviceStateView(deviceSettings: DeviceSettings) {
    var selectedType by rememberSaveable { mutableStateOf(ControlType.strength) }
    Column {
        ControlRow(
            R.string.setting_mode,
            deviceSettings.mode.toString(),
            selectedType == ControlType.mode,
            { selectedType = ControlType.mode }
        )
        ControlRow(
            R.string.setting_strength,
            renderPercent(deviceSettings.strength),
            selectedType == ControlType.strength,
            { selectedType = ControlType.strength }
        )
        ControlRow(
            R.string.setting_interval,
            renderSeconds(deviceSettings.interval),
            selectedType == ControlType.intervall,
            { selectedType = ControlType.intervall }
        )
    }
}

fun renderSeconds(interval: Float) = "${interval}s"
fun renderPercent(value: Float) = String.format("%.0f", value * 100) + "%"
