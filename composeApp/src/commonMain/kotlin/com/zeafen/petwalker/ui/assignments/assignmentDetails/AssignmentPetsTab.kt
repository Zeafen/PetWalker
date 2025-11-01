package com.zeafen.petwalker.ui.assignments.assignmentDetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zeafen.petwalker.data.helpers.DeviceConfiguration
import com.zeafen.petwalker.domain.models.api.assignments.Assignment
import com.zeafen.petwalker.domain.models.api.assignments.AssignmentState
import com.zeafen.petwalker.domain.models.api.other.PagedResult
import com.zeafen.petwalker.domain.models.api.other.ServiceType
import com.zeafen.petwalker.domain.models.api.pets.Pet
import com.zeafen.petwalker.domain.models.api.users.APILocation
import com.zeafen.petwalker.domain.models.api.users.AccountStatus
import com.zeafen.petwalker.domain.models.api.users.DesiredPayment
import com.zeafen.petwalker.domain.models.api.users.UserService
import com.zeafen.petwalker.domain.models.api.users.Walker
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.presentation.assignments.assignmentDetailsPage.AssignmentDetailsUiEvent
import com.zeafen.petwalker.presentation.assignments.assignmentDetailsPage.AssignmentDetailsUiState
import com.zeafen.petwalker.ui.pets.PetCard
import com.zeafen.petwalker.ui.standard.elements.ErrorInfoHint
import com.zeafen.petwalker.ui.standard.elements.PageSelectionRow
import com.zeafen.petwalker.ui.standard.elements.PetWalkerTextInput
import com.zeafen.petwalker.ui.theme.PetWalker_theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.ic_search
import petwalker.composeapp.generated.resources.name_search_field_hint
import petwalker.composeapp.generated.resources.species_search_field_hint

@Composable
fun AssignmentPetsTab(
    modifier: Modifier = Modifier,
    state: AssignmentDetailsUiState,
    onEvent: (AssignmentDetailsUiEvent) -> Unit
) {
    val deviceConfig =
        DeviceConfiguration.fromWindowSizeClass(currentWindowAdaptiveInfo().windowSizeClass)

    Column(modifier = modifier) {
        if (deviceConfig == DeviceConfiguration.MOBILE_PORTRAIT || deviceConfig == DeviceConfiguration.TABLET_PORTRAIT)
            Column {
                PetWalkerTextInput(
                    modifier = Modifier
                        .padding(
                            horizontal = 12.dp, vertical = 8.dp
                        ),
                    value = state.searchPetsName,
                    hint = stringResource(Res.string.name_search_field_hint),
                    onValueChanged = { onEvent(AssignmentDetailsUiEvent.SetSearchPetsName(it)) },
                    leadingIcon = painterResource(Res.drawable.ic_search)
                )
                PetWalkerTextInput(
                    modifier = Modifier
                        .padding(
                            horizontal = 12.dp, vertical = 8.dp
                        ),
                    value = state.searchPetsSpecies,
                    hint = stringResource(Res.string.species_search_field_hint),
                    onValueChanged = { onEvent(AssignmentDetailsUiEvent.SetSearchPetsSpecies(it)) },
                    leadingIcon = painterResource(Res.drawable.ic_search)
                )
            }
        else Row {
            PetWalkerTextInput(
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        horizontal = 12.dp, vertical = 8.dp
                    ),
                value = state.searchPetsName,
                hint = stringResource(Res.string.name_search_field_hint),
                onValueChanged = { onEvent(AssignmentDetailsUiEvent.SetSearchPetsName(it)) },
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
                onValueChanged = { onEvent(AssignmentDetailsUiEvent.SetSearchPetsSpecies(it)) },
                leadingIcon = painterResource(Res.drawable.ic_search)
            )
        }

        when (state.assignmentPets) {
            is APIResult.Downloading -> CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth()
                    .fillMaxWidth(0.5f)
                    .fillMaxHeight(0.3f),
            )

            is APIResult.Error -> ErrorInfoHint(
                errorInfo = "${
                    stringResource(state.assignmentPets.info.infoResource())
                }: ${state.assignmentPets.additionalInfo}",
                onReloadPage = { onEvent(AssignmentDetailsUiEvent.LoadPets()) }
            )

            is APIResult.Succeed -> {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Adaptive(240.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    items(
                        items = state.assignmentPets.data!!.result,
                        key = { pet -> pet.id }) {
                        PetCard(
                            modifier = Modifier
                                .padding(12.dp),
                            pet = it
                        )
                    }
                    item(span = StaggeredGridItemSpan.FullLine) {
                        PageSelectionRow(
                            modifier = Modifier
                                .fillMaxWidth(),
                            totalPages = state.assignmentPets.data.totalPages,
                            currentPage = state.assignmentPets.data.currentPage,
                            onPageClick = { onEvent(AssignmentDetailsUiEvent.LoadPets(it)) }
                        )
                    }
                }
            }
        }
    }
}