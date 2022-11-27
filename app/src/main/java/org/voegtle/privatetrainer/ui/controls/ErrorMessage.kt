package org.voegtle.privatetrainer.ui.controls

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp

@Composable
fun ErrorMessage(
    message: String, color: Color = MaterialTheme.colorScheme.inverseOnSurface,
    style: TextStyle = MaterialTheme.typography.bodyLarge
) {
    Text(message, color = color, style = style, modifier = Modifier.padding(Dp(8f)))
}
