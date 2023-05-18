package org.voegtle.privatetrainer.ui.controls

import androidx.annotation.StringRes
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource

@Composable
fun PrivateIconButton(imageVector: ImageVector, onClick: () -> Unit, @StringRes id: Int, modifier: Modifier = Modifier) {
    IconButton(
        onClick = onClick,
        colors = IconButtonDefaults.filledIconButtonColors()
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = stringResource(
                id = id
            ),
            modifier = modifier
        )

    }

}
