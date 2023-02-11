package org.voegtle.privatetrainer.ui.controls

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.voegtle.privatetrainer.R
import java.util.logging.Level
import java.util.logging.Logger

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameEditRow(name: String,
                favorite: Boolean,
                onNameChange: (String) -> Unit,
                onFocusLost: () -> Unit,
                onFavoriteClicked: (Boolean) -> Unit) {
    val context = LocalContext.current

    Row(modifier = Modifier.height(60.dp)) {
        TextField(
            value = name,
            modifier= Modifier.onFocusChanged { focusState ->
                if (!focusState.isFocused) {
                    onFocusLost()
                }
            },
            onValueChange = onNameChange,
            label = { Text(context.getString(R.string.settings_name)) }
        )
        IconButton(
            onClick = { onFavoriteClicked(!favorite) },
            colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
        ) {
            Icon(
                imageVector = if (favorite) { Icons.Filled.ThumbUp } else {Icons.Outlined.ThumbUp},
                contentDescription = stringResource(
                    id = R.string.device_start
                ),
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterVertically)
            )
        }
    }

}
