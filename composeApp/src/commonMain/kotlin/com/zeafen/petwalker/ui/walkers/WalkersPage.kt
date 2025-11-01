package com.zeafen.petwalker.ui.walkers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.zeafen.petwalker.domain.models.api.filtering.UsersOrdering
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.presentation.walkers.walkersList.WalkersPageUiEvent
import com.zeafen.petwalker.presentation.walkers.walkersList.WalkersPageUiState
import com.zeafen.petwalker.ui.standard.elements.ErrorInfoHint
import com.zeafen.petwalker.ui.standard.elements.PageSelectionRow
import com.zeafen.petwalker.ui.standard.elements.PetWalkerOrderingTab
import com.zeafen.petwalker.ui.standard.elements.PetWalkerTextInput
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.ic_filter
import petwalker.composeapp.generated.resources.ic_search
import petwalker.composeapp.generated.resources.search_field_hint
import petwalker.composeapp.generated.resources.walkers_page_header

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalkersPage(
    modifier: Modifier = Modifier,
    state: WalkersPageUiState,
    onEvent: (WalkersPageUiEvent) -> Unit,
    onWalkerCardClick: (walkerId: String) -> Unit
) {
    var openFiltersDialog by remember {
        mutableStateOf(false)
    }
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(Res.string.walkers_page_header),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                actions = {
                    IconButton(
                        onClick = { openFiltersDialog = true }
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(32.dp),
                            painter = painterResource(Res.drawable.ic_filter),
                            contentDescription = "Open filters"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .clip(
                    RoundedCornerShape(
                        topStart = 32.dp,
                        topEnd = 32.dp
                    )
                )
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .consumeWindowInsets(WindowInsets.systemBars)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            PetWalkerTextInput(
                modifier = Modifier
                    .padding(
                        horizontal = 12.dp, vertical = 8.dp
                    ),
                value = state.searchName,
                hint = stringResource(Res.string.search_field_hint),
                onValueChanged = { onEvent(WalkersPageUiEvent.EnterSearchTitle(it)) },
                leadingIcon = painterResource(Res.drawable.ic_search)
            )
            PetWalkerOrderingTab(
                modifier = Modifier
                    .padding(start = 12.dp),
                availableOrderings = UsersOrdering.entries.toList(),
                selectedOrdering = state.ordering,
                ascending = state.ascending,
                onOrderingChanged = { onEvent(WalkersPageUiEvent.SetUsersOrdering(it)) },
                orderingLabel = { stringResource(it.displayName) }
            )
            when (state.walkers) {
                is APIResult.Downloading -> CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth()
                        .fillMaxWidth(0.4f),
                    strokeWidth = 4.dp
                )

                is APIResult.Error -> ErrorInfoHint(
                    errorInfo = "${
                        stringResource(state.walkers.info.infoResource())
                    }: ${state.walkers.additionalInfo}",
                    onReloadPage = { onEvent(WalkersPageUiEvent.LoadWalkers()) }
                )

                is APIResult.Succeed -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(
                            items = state.walkers.data!!.result,
                            key = { walker -> walker.id }
                        ) { walker ->
                            WalkerCard(
                                modifier = Modifier.padding(vertical = 12.dp),
                                walker = walker,
                                onClick = { onWalkerCardClick(walker.id) }
                            )
                        }
                        item {
                            PageSelectionRow(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                totalPages = state.walkers.data.totalPages,
                                currentPage = state.walkers.data.currentPage,
                                onPageClick = { onEvent(WalkersPageUiEvent.LoadWalkers(it)) }
                            )
                        }
                    }
                }
            }
        }
        if (openFiltersDialog)
            WalkersFilteringDialog(
                onDismissRequest = { openFiltersDialog = false },
                onClearFiltersClick = { onEvent(WalkersPageUiEvent.ClearFilters) },
                onDoneFiltersClick = { services, maxComplaintsCount, accountStatus, showOnline ->
                    onEvent(
                        WalkersPageUiEvent.SetFilters(
                            services,
                            maxComplaintsCount,
                            accountStatus,
                            showOnline
                        )
                    )
                }
            )
    }
}