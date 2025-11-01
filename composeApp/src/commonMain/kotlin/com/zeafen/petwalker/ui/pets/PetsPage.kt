package com.zeafen.petwalker.ui.pets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.zeafen.petwalker.data.helpers.DeviceConfiguration
import com.zeafen.petwalker.domain.models.api.other.PagedResult
import com.zeafen.petwalker.domain.models.api.pets.Pet
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.presentation.pets.petsList.PetsPageUiEvent
import com.zeafen.petwalker.presentation.pets.petsList.PetsPageUiState
import com.zeafen.petwalker.ui.standard.elements.ErrorInfoHint
import com.zeafen.petwalker.ui.standard.elements.PageSelectionRow
import com.zeafen.petwalker.ui.standard.elements.PetWalkerTextInput
import com.zeafen.petwalker.ui.theme.PetWalker_theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.ic_add
import petwalker.composeapp.generated.resources.ic_search
import petwalker.composeapp.generated.resources.name_search_field_hint
import petwalker.composeapp.generated.resources.pets_screen_header
import petwalker.composeapp.generated.resources.species_search_field_hint

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetsPage(
    modifier: Modifier = Modifier,
    state: PetsPageUiState,
    onEvent: (PetsPageUiEvent) -> Unit,
    onPetClick: (String) -> Unit,
    onAddPetClick: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(Res.string.pets_screen_header),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                actions = {
                    IconButton(
                        onClick = onAddPetClick
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(48.dp),
                            painter = painterResource(Res.drawable.ic_add),
                            contentDescription = "Add pet"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        val deviceConfig =
            DeviceConfiguration.fromWindowSizeClass(currentWindowAdaptiveInfo().windowSizeClass)

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
            if (deviceConfig !in listOf(
                    DeviceConfiguration.MOBILE_PORTRAIT,
                    DeviceConfiguration.TABLET_PORTRAIT
                )
            ) Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth()
                    .fillMaxWidth(0.8f),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                PetWalkerTextInput(
                    modifier = Modifier
                        .weight(1f)
                        .padding(
                            horizontal = 12.dp, vertical = 8.dp
                        ),
                    value = state.searchPetsName,
                    hint = stringResource(Res.string.name_search_field_hint),
                    onValueChanged = { onEvent(PetsPageUiEvent.SetSearchName(it)) },
                    leadingIcon = painterResource(Res.drawable.ic_search)
                )
                PetWalkerTextInput(
                    modifier = Modifier
                        .weight(1f)
                        .padding(
                            horizontal = 12.dp, vertical = 8.dp
                        ),
                    value = state.searchPetsSpecies,
                    hint = stringResource(Res.string.species_search_field_hint),
                    onValueChanged = { onEvent(PetsPageUiEvent.SetSearchSpecies(it)) },
                    leadingIcon = painterResource(Res.drawable.ic_search)
                )
            }
            else Column {
                PetWalkerTextInput(
                    modifier = Modifier
                        .padding(
                            horizontal = 12.dp, vertical = 8.dp
                        ),
                    value = state.searchPetsName,
                    hint = stringResource(Res.string.name_search_field_hint),
                    onValueChanged = { onEvent(PetsPageUiEvent.SetSearchName(it)) },
                    leadingIcon = painterResource(Res.drawable.ic_search)
                )
                PetWalkerTextInput(
                    modifier = Modifier
                        .padding(
                            horizontal = 12.dp, vertical = 8.dp
                        ),
                    value = state.searchPetsSpecies,
                    hint = stringResource(Res.string.species_search_field_hint),
                    onValueChanged = { onEvent(PetsPageUiEvent.SetSearchSpecies(it)) },
                    leadingIcon = painterResource(Res.drawable.ic_search)
                )
            }

            when (state.pets) {
                is APIResult.Downloading -> CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth()
                        .fillMaxWidth(0.4f),
                    strokeWidth = 4.dp
                )

                is APIResult.Error -> ErrorInfoHint(
                    errorInfo = "${
                        stringResource(state.pets.info.infoResource())
                    }: ${state.pets.additionalInfo}",
                    onReloadPage = { onEvent(PetsPageUiEvent.LoadOwnPets()) }
                )

                is APIResult.Succeed -> {
                    LazyVerticalStaggeredGrid(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        columns = StaggeredGridCells.Adaptive(minSize = 240.dp)
                    ) {
                        items(
                            items = state.pets.data!!.result,
                            key = { pet -> pet.id }
                        ) { pet ->
                            PetCard(
                                modifier = Modifier.padding(12.dp),
                                pet = pet,
                                onClick = { onPetClick(pet.id) }
                            )
                        }
                        item(span = StaggeredGridItemSpan.FullLine) {
                            PageSelectionRow(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                totalPages = state.pets.data.totalPages,
                                currentPage = state.pets.data.currentPage,
                                onPageClick = { onEvent(PetsPageUiEvent.LoadOwnPets(it)) }
                            )
                        }
                    }
                }
            }
        }
    }
}