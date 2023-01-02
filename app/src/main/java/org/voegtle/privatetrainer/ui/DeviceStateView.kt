package org.voegtle.privatetrainer.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import org.voegtle.privatetrainer.R
import org.voegtle.privatetrainer.business.DeviceSettings
import org.voegtle.privatetrainer.ui.controls.ControlRow

@Composable
fun DeviceStateView(deviceSettings: DeviceSettings) {
    val context = LocalContext.current
    Column {
        ControlRow(context.getString(R.string.setting_mode), deviceSettings.mode.toString(), true)
        ControlRow(
            context.getString(R.string.setting_strength),
            renderPercent(deviceSettings.strength),
            false
        )
        ControlRow(
            context.getString(R.string.setting_interval),
            renderSeconds(deviceSettings.interval),
            false
        )
    }
}

fun renderSeconds(interval: Float) = "${interval}s"
fun renderPercent(value: Float) = String.format("%.0f", value * 100) + "%"
