package com.zeafen.petwalker.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.zeafen.petwalker.ui.standard.elements.HintWithIcon
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.delete_account_label
import petwalker.composeapp.generated.resources.edit_info_label
import petwalker.composeapp.generated.resources.exit_account_label
import petwalker.composeapp.generated.resources.ic_account_box
import petwalker.composeapp.generated.resources.ic_exit
import petwalker.composeapp.generated.resources.ic_no_account
import petwalker.composeapp.generated.resources.ic_verified
import petwalker.composeapp.generated.resources.security_label
import petwalker.composeapp.generated.resources.show_statistics


@Composable
fun ProfileMainTab(
    modifier: Modifier = Modifier,
    onEditInfoClick: () -> Unit,
    onGoToStatisticsClick: () -> Unit,
    onGoToSecurityClick: () -> Unit,
    onExitAccountClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            HintWithIcon(
                modifier = Modifier
                    .padding(8.dp)
                    .width(300.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable {
                        onEditInfoClick()
                    }
                    .padding(16.dp),
                hint = stringResource(Res.string.edit_info_label),
                leadingIcon = painterResource(Res.drawable.ic_account_box),
                textColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
            HintWithIcon(
                modifier = Modifier
                    .padding(8.dp)
                    .width(300.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable {
                        onGoToStatisticsClick()
                    }
                    .padding(16.dp),
                hint = stringResource(Res.string.show_statistics),
                leadingIcon = painterResource(Res.drawable.ic_account_box),
                textColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
            HintWithIcon(
                modifier = Modifier
                    .padding(8.dp)
                    .width(300.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable {
                        onGoToSecurityClick()
                    }
                    .padding(16.dp),
                hint = stringResource(Res.string.security_label),
                leadingIcon = painterResource(Res.drawable.ic_verified),
                textColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
            HintWithIcon(
                modifier = Modifier
                    .padding(8.dp)
                    .width(300.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable {
                        onExitAccountClick()
                    }
                    .padding(16.dp),
                hint = stringResource(Res.string.exit_account_label),
                leadingIcon = painterResource(Res.drawable.ic_exit),
                textColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
            HintWithIcon(
                modifier = Modifier
                    .padding(8.dp)
                    .width(300.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable {
                        onDeleteAccountClick()
                    }
                    .padding(16.dp),
                hint = stringResource(Res.string.delete_account_label),
                leadingIcon = painterResource(Res.drawable.ic_no_account),
                textColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}