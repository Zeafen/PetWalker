package com.zeafen.petwalker.ui.standard.elements

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

@Composable
fun PetWalkerLinkTextButton(
    modifier: Modifier = Modifier,
    text: String,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    enabled: Boolean = true,
    contentColor: Color = MaterialTheme.colorScheme.secondary,
    containerColor: Color = Color.Transparent,
    onClick: () -> Unit
) {
    TextButton(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        contentPadding = PaddingValues(horizontal = 4.dp),
        colors = ButtonDefaults.textButtonColors(
            contentColor = contentColor,
            containerColor = containerColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            textDecoration = TextDecoration.Underline,
            style = style
        )
    }
}