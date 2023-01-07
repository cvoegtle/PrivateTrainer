package org.voegtle.privatetrainer.ui.controls

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.voegtle.privatetrainer.business.SettingType
import org.voegtle.privatetrainer.business.SettingsSteps

@Composable
fun ControlRow(
    type: SettingType,
    textId: Int,
    value: String,
    selectedType: SettingType,
    onActivation: (SettingType) -> Unit
) {
    val context = LocalContext.current
    Row(modifier = Modifier.height(60.dp)) {
        ElevatedButton(
            onClick = { onActivation(type) },
            modifier = Modifier
                .width(150.dp)
                .fillMaxHeight(),
            border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outline),
            colors = if (selectedType == type) ButtonDefaults.buttonColors() else ButtonDefaults.elevatedButtonColors()
        ) {
            Text(context.getString(textId))
        }
        Box(
            Modifier
                .width(150.dp)
                .fillMaxHeight()
                .border(width = 1.dp, color = MaterialTheme.colorScheme.outline),
            contentAlignment = Alignment.Center
        ) {
            Text(value, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun FloatSliderRow(value: Float, range: SettingsSteps<Float>, onChange: (Float) -> Unit) {
    Slider(
        value = range.value2Index(value),
        onValueChange = { onChange(range.index2Value(it)) },
        steps = range.numberOfSteps(),
        valueRange = range.start()..range.end()
    )
}

@Composable
fun IntSliderRow(value: Int, range: SettingsSteps<Int>, onChange: (Int) -> Unit) {
    Slider(
        value = range.value2Index(value),
        onValueChange = { onChange(range.index2Value(it)) },
        steps = range.numberOfSteps(),
        valueRange = range.start()..range.end()
    )
}
