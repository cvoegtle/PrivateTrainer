package org.voegtle.privatetrainer.ui.controls

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import org.voegtle.privatetrainer.R

@Composable
fun About() {
    val context = LocalContext.current
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            context.getString(R.string.about),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(Dp(4f))
        )
    }
}
