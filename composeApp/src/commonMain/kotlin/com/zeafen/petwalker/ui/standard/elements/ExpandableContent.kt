package com.zeafen.petwalker.ui.standard.elements

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.ic_arrow_up

@Composable
fun ExpandableContent(
    modifier: Modifier = Modifier,
    borderColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    defaultContent: @Composable () -> Unit,
    expandableContent: @Composable () -> Unit
) {
    var openAvailableOptions by remember {
        mutableStateOf(false)
    }
    val rotation by animateFloatAsState(
        if (openAvailableOptions) 0f else 180f
    )

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(2.dp, borderColor, RoundedCornerShape(12.dp))
                .clickable {
                    openAvailableOptions = !openAvailableOptions
                }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(modifier = Modifier.weight(1f)) {
                defaultContent()
            }
            IconButton(
                onClick = {
                    openAvailableOptions = !openAvailableOptions
                }
            ) {
                Icon(
                    modifier = Modifier
                        .rotate(rotation),
                    painter = painterResource(Res.drawable.ic_arrow_up),
                    contentDescription = null
                )
            }
        }
        AnimatedVisibility(
            visible = openAvailableOptions,
            content = {
                expandableContent()
            }
        )
    }
}