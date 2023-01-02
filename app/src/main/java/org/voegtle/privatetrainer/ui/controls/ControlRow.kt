package org.voegtle.privatetrainer.ui.controls

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ControlRow(buttonText: String, value: String, selected: Boolean) {
    Row(modifier = Modifier.height(60.dp)) {
        ElevatedButton(
            onClick = {},
            modifier = Modifier
                .width(150.dp)
                .fillMaxHeight(),
            border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outline),
            colors = if (selected) ButtonDefaults.buttonColors() else ButtonDefaults.elevatedButtonColors()
        ) {
            Text(buttonText)
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
