package com.zeafen.petwalker.ui.standard.elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import kotlin.math.max

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PageSelectionRow(
    modifier: Modifier = Modifier,
    totalPages: Int,
    currentPage: Int,
    onPageClick: (page: Int) -> Unit,
) {
    FlowRow(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalArrangement = Arrangement.Center
    ) {
        val showPages = remember(currentPage, totalPages) {
            listOf(
                1, currentPage - 1, currentPage, currentPage + 1, totalPages
            ).map {
                it.coerceIn(minimumValue = 1, maximumValue = max(totalPages, 1))
            }.distinct()
        }

        showPages.forEachIndexed { index, pageNum ->
            if (index > 0 && pageNum - showPages[index - 1] > 1)
                Text(
                    text = "...",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            PetWalkerLinkTextButton(
                text = pageNum.toString(),
                containerColor = if (currentPage == pageNum)
                    MaterialTheme.colorScheme.secondaryContainer
                else Color.Transparent
            ) { onPageClick(pageNum) }
        }

    }
}