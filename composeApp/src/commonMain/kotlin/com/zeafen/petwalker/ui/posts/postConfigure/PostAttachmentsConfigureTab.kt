package com.zeafen.petwalker.ui.posts.postConfigure

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.zeafen.petwalker.domain.models.api.other.Attachment
import com.zeafen.petwalker.domain.models.api.other.AttachmentType
import com.zeafen.petwalker.ui.channel.AttachmentCell
import com.zeafen.petwalker.ui.standard.elements.PetWalkerAsyncImage
import com.zeafen.petwalker.ui.standard.elements.PetWalkerButton
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.add_btn_txt
import petwalker.composeapp.generated.resources.ic_add
import petwalker.composeapp.generated.resources.ic_clear


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PostAttachmentsConfigureTab(
    modifier: Modifier = Modifier,
    images: List<Attachment>,
    onAddAttachmentClick: () -> Unit,
    onAttachmentRemoved: (id: String) -> Unit
) {
    Column(modifier = modifier) {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            images.forEach {
                if (it.type == AttachmentType.Image)
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .padding(8.dp)
                    ) {
                        PetWalkerAsyncImage(
                            asyncImageModifier = Modifier
                                .fillMaxSize(),
                            imageUrl = it.reference
                        )
                        FilledIconButton(
                            modifier = Modifier
                                .align(Alignment.TopEnd),
                            onClick = { onAttachmentRemoved(it.id) },
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_clear),
                                contentDescription = "Remove image"
                            )
                        }
                    }
                else
                    AttachmentCell(
                        modifier = Modifier
                            .widthIn(max = 180.dp)
                            .padding(8.dp),
                        attachment = it,
                        onRemoveClick = { onAttachmentRemoved(it.id) }
                    )
            }
        }
        Spacer(Modifier.height(12.dp))
        PetWalkerButton(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth()
                .fillMaxWidth(0.9f),
            text = stringResource(Res.string.add_btn_txt),
            trailingIcon = painterResource(Res.drawable.ic_add),
            onClick = onAddAttachmentClick
        )
    }
}