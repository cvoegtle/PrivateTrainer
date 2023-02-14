package org.voegtle.privatetrainer.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.voegtle.privatetrainer.R
import org.voegtle.privatetrainer.business.DeviceSettings
import org.voegtle.privatetrainer.business.SettingType
import org.voegtle.privatetrainer.business.SettingsRanges
import org.voegtle.privatetrainer.ui.controls.ControlRow
import org.voegtle.privatetrainer.ui.controls.FloatSliderRow
import org.voegtle.privatetrainer.ui.controls.IntSliderRow
import org.voegtle.privatetrainer.ui.controls.NameEditRow

@Composable
fun DeviceSettingsView(deviceSettings: DeviceSettings) {
    val context = LocalContext.current
    Column (modifier = Modifier.padding(4.dp)){
        Row (){
            Text(deviceSettings.name, fontWeight = FontWeight.Bold)
        }
        Row (modifier = Modifier.padding(bottom = 8.dp)){
            val deviceDescription =
                "${context.getString(R.string.setting_mode)}: ${deviceSettings.mode}, " +
                "${context.getString(R.string.setting_strength)}: ${renderLevel(deviceSettings.strength)}, " +
                "${context.getString(R.string.setting_interval)}: ${renderSeconds(deviceSettings.interval)}"
            Text(deviceDescription)
        }
        Divider(color = MaterialTheme.colorScheme.outline)
    }
}

@Preview
@Composable
fun DeviceSettingsViewPreview() {
    val deviceSettings = DeviceSettings(name = "Preview", mode = 2, strength = 2, interval = 120.0f)
    DeviceSettingsView(deviceSettings = deviceSettings)
}
