package com.zeafen.petwalker.ui.standard.elements

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun HintWithIcon(
    modifier: Modifier = Modifier,
    hint: String,
    leadingIcon: Painter,
    textColor: Color? = null,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    maxLines: Int = 2
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = leadingIcon,
            contentDescription = null,
            tint = textColor ?: LocalContentColor.current
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = hint,
            style = textStyle,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            color = textColor ?: Color.Unspecified,
        )
    }
}