package org.voegtle.privatetrainer.ui.controls

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.voegtle.privatetrainer.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameEditRow(name: String, onNameChange: (String) -> Unit, onFocusLost: () -> Unit) {
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
    }
}
