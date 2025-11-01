package com.zeafen.petwalker.ui.standard.elements

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.ic_arrow_up
import petwalker.composeapp.generated.resources.ordering_label


@Composable
fun <T> PetWalkerOrderingTab(
    modifier: Modifier = Modifier,
    selectedOrdering: T?,
    availableOrderings: List<T>,
    ascending: Boolean,
    onOrderingChanged: (T) -> Unit,
    orderingLabel: @Composable (T) -> String
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.ordering_label),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Row(
            modifier = Modifier
                .padding(start = 12.dp, bottom = 12.dp, top = 8.dp)
                .horizontalScroll(rememberScrollState()),
        ) {
            availableOrderings.forEach { ordering ->
                AssistChip(
                    modifier = Modifier
                        .padding(horizontal = 8.dp),
                    label = {
                        Text(
                            text = orderingLabel(ordering),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    trailingIcon = {
                        AnimatedVisibility(
                            visible = selectedOrdering == ordering,
                            enter = slideInHorizontally(
                                spring(stiffness = Spring.StiffnessMedium),
                                initialOffsetX = { offset -> offset }) + fadeIn(
                                spring(stiffness = Spring.StiffnessMedium)
                            ),
                            exit = slideOutHorizontally(
                                spring(stiffness = Spring.StiffnessMedium),
                                targetOffsetX = { offset -> offset }) + fadeOut(
                                spring(stiffness = Spring.StiffnessMedium)
                            )
                        ) {
                            val rotation by animateFloatAsState(
                                if (ascending) 0f
                                else 180f
                            )
                            Icon(
                                modifier = Modifier
                                    .rotate(rotation),
                                painter = painterResource(Res.drawable.ic_arrow_up),
                                contentDescription = null
                            )
                        }
                    },
                    onClick = {
                        onOrderingChanged(ordering)
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (selectedOrdering == ordering) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
        }
    }
}