package com.zeafen.petwalker.ui.posts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zeafen.petwalker.domain.models.api.other.AttachmentType
import com.zeafen.petwalker.domain.models.ui.PostModel
import com.zeafen.petwalker.ui.channel.AttachmentCell
import com.zeafen.petwalker.ui.standard.elements.HintWithIcon
import com.zeafen.petwalker.ui.standard.elements.PetWalkerAsyncImage
import com.zeafen.petwalker.ui.standard.elements.PetWalkerExpandableText
import com.zeafen.petwalker.ui.standard.elements.UserInfoHeader
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.ic_message

@Composable
fun PostCard(
    modifier: Modifier = Modifier,
    post: PostModel,
    onGoToPostClick: (id: String) -> Unit,
    onLoadAttachment: (ref: String, name: String) -> Unit,
    onPlayAttachment: ((ref: String) -> Unit)? = null
) {
    val imageAttachments = remember(post.attachments) {
        post.attachments.filter {
            it.type == AttachmentType.Image
        }
    }
    val attachments = remember(post.attachments) {
        post.attachments.filter {
            it.type != AttachmentType.Image
        }
    }
    var selectedImageIndex by remember {
        mutableIntStateOf(0)
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp, pressedElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        onClick = { onGoToPostClick(post.id) }
    ) {
        val pagerState = rememberPagerState {
            imageAttachments.size
        }

        LaunchedEffect(selectedImageIndex) {
            pagerState.animateScrollToPage(selectedImageIndex)
        }
        LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
            if (!pagerState.isScrollInProgress)
                selectedImageIndex = pagerState.currentPage
        }

        //post sender info
        UserInfoHeader(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, top = 8.dp),
            walkerFullName = post.senderName,
            walkerImageUrl = post.senderImageUrl
        )
        Text(
            modifier = Modifier
                .padding(start = 8.dp),
            text = post.dateSent.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Light
        )
        HorizontalDivider(thickness = 4.dp)

        //post header
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            text = post.topic,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier
                .padding(start = 12.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(vertical = 6.dp, horizontal = 12.dp),
            text = stringResource(post.type.displayName),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Start
        )

        //image attachments
        Spacer(Modifier.height(12.dp))
        HorizontalPager(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth()
                .sizeIn(maxWidth = 300.dp, maxHeight = 300.dp)
                .clip(RoundedCornerShape(12.dp)),
            state = pagerState
        ) { page ->
            PetWalkerAsyncImage(
                asyncImageModifier = Modifier
                    .fillMaxSize(),
                imageUrl = imageAttachments.getOrNull(page)?.reference
            )
        }

        //post body
        Spacer(Modifier.height(12.dp))
        post.body?.let {
            PetWalkerExpandableText(
                modifier = Modifier
                    .padding(horizontal = 8.dp),
                text = it,
            )
        }

        //post attachments
        attachments.forEach {
            AttachmentCell(
                modifier = Modifier
                    .padding(
                        vertical = 8.dp
                    ),
                attachment = it,
                onPlayAttachment = onPlayAttachment,
                onLoadAttachment = onLoadAttachment
            )
        }

        //post statistics info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HintWithIcon(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        onGoToPostClick(post.id)
                    }
                    .padding(8.dp),
                hint = post.commentsCount.toString(),
                leadingIcon = painterResource(Res.drawable.ic_message),
            )
        }
    }
}

@Composable
fun PostCard(
    modifier: Modifier = Modifier,
    post: PostModel,
    onLoadAttachment: (ref: String, name: String) -> Unit,
    onPlayAttachment: ((ref: String) -> Unit)? = null,
    onCommentsClick: () -> Unit
) {
    val imageAttachments = remember(post.attachments) {
        post.attachments.filter {
            it.type == AttachmentType.Image
        }
    }
    val attachments = remember(post.attachments) {
        post.attachments.filter {
            it.type != AttachmentType.Image
        }
    }
    var selectedImageIndex by remember {
        mutableIntStateOf(0)
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp, pressedElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
    ) {
        val pagerState = rememberPagerState {
            imageAttachments.size
        }

        LaunchedEffect(selectedImageIndex) {
            pagerState.animateScrollToPage(selectedImageIndex)
        }
        LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
            if (!pagerState.isScrollInProgress)
                selectedImageIndex = pagerState.currentPage
        }

        //post sender info
        UserInfoHeader(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, top = 8.dp),
            walkerFullName = post.senderName,
            walkerImageUrl = post.senderImageUrl
        )
        Text(
            modifier = Modifier
                .padding(start = 8.dp),
            text = post.dateSent.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Light
        )
        HorizontalDivider(thickness = 4.dp)

        //post header
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            text = post.topic,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier
                .padding(start = 12.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(vertical = 6.dp, horizontal = 12.dp),
            text = stringResource(post.type.displayName),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Start
        )

        //image attachments
        Spacer(Modifier.height(12.dp))
        HorizontalPager(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth()
                .sizeIn(maxWidth = 300.dp, maxHeight = 300.dp)
                .clip(RoundedCornerShape(12.dp)),
            state = pagerState
        ) { page ->
            PetWalkerAsyncImage(
                asyncImageModifier = Modifier
                    .fillMaxSize(),
                imageUrl = imageAttachments.getOrNull(page)?.reference
            )
        }

        //post body
        Spacer(Modifier.height(12.dp))
        post.body?.let {
            PetWalkerExpandableText(
                modifier = Modifier
                    .padding(horizontal = 8.dp),
                text = it,
            )
        }

        //post attachments
        attachments.forEach {
            AttachmentCell(
                modifier = Modifier
                    .padding(
                        vertical = 8.dp
                    ),
                attachment = it,
                onPlayAttachment = onPlayAttachment,
                onLoadAttachment = onLoadAttachment
            )
        }

        //post statistics info
        PostStatisticsRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .horizontalScroll(rememberScrollState()),
            commentsCount = post.commentsCount,
            onCommentsClick = onCommentsClick
        )
    }
}

@Composable
fun PostStatisticsRow(
    modifier: Modifier = Modifier,
    commentsCount: Long,
    onCommentsClick: () -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        HintWithIcon(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable {
                    onCommentsClick()
                }
                .padding(8.dp),
            hint = commentsCount.toString(),
            leadingIcon = painterResource(Res.drawable.ic_message),
        )
    }
}
