package com.zeafen.petwalker.ui.reviews

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zeafen.petwalker.domain.models.ui.ReviewCardModel
import com.zeafen.petwalker.ui.standard.elements.PetWalkerAsyncImage
import com.zeafen.petwalker.ui.standard.elements.PetWalkerExpandableText
import com.zeafen.petwalker.ui.standard.elements.PetWalkerLinkTextButton
import com.zeafen.petwalker.ui.standard.elements.RatingRow
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.see_assignment_btn_txt
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Composable
fun ReviewCard(
    modifier: Modifier = Modifier,
    review: ReviewCardModel,
    onGoToAssignmentClick: (String) -> Unit
) {
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
        ReviewCellHeader(
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 8.dp),
            reviewerImageUrl = review.senderImageUrl,
            reviewerFullName = review.senderFullName,
            dateSent = review.datePosted,
            rating = review.rating
        )
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp)
        ) {
            PetWalkerExpandableText(
                modifier = Modifier
                    .fillMaxWidth(),
                text = review.text
            )
            PetWalkerLinkTextButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.End),
                text = stringResource(Res.string.see_assignment_btn_txt),
                onClick = { onGoToAssignmentClick(review.assignmentId) }
            )
        }
    }
}

@Composable
fun ReviewCellHeader(
    modifier: Modifier = Modifier,
    reviewerImageUrl: String?,
    reviewerFullName: String,
    dateSent: LocalDateTime,
    rating: Int
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PetWalkerAsyncImage(
            imageUrl = reviewerImageUrl,
            asyncImageModifier = Modifier
                .size(64.dp)
                .clip(CircleShape),
        )
        Column(
            modifier = modifier.weight(1f)
        ) {
            Text(
                text = reviewerFullName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = dateSent.toString(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Light
            )
        }
        RatingRow(
            currentRating = rating.toFloat(),
            starSize = 16.dp,
            itemsPadding = PaddingValues(0.dp)
        )
    }

}
