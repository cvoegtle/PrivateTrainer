package org.voegtle.privatetrainer.ui.controls

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.voegtle.privatetrainer.R


@Composable
fun ErrorView(
    messageId: Int,
    color: Color = MaterialTheme.colorScheme.inverseOnSurface,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    onButtonClick: () -> Unit = {}
) {
    val context = LocalContext.current

    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            context.getString(messageId),
            color = color,
            style = style,
            modifier = Modifier.padding(Dp(8f))
        )

        Spacer(modifier = Modifier.width(8.dp))

        PrivateIconButton(
            imageVector = Icons.Filled.Replay,
            onClick = onButtonClick,
            id = R.string.search_device,
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterVertically))
    }

}
