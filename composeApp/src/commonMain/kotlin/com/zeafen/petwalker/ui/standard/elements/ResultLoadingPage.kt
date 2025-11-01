package com.zeafen.petwalker.ui.standard.elements

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.Error
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.ic_go_back
import petwalker.composeapp.generated.resources.ic_success
import petwalker.composeapp.generated.resources.loading_label
import petwalker.composeapp.generated.resources.success_label

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingResultPage(
    modifier: Modifier = Modifier,
    state: APIResult<Unit, Error>,
    onSuccessResult: () -> Unit,
    onReloadAfterError: (Error) -> Unit,
    onGoBackClick: (() -> Unit)? = null
) {
    LaunchedEffect(state) {
        if (state is APIResult.Succeed) {
            delay(3000)
            onSuccessResult()
        }
    }
    Scaffold(
        modifier = modifier,
        topBar = {
            TwoLayerTopAppBar(
                title = {},
                navigationIcon = onGoBackClick?.let {
                    {
                        IconButton(
                            onClick = onGoBackClick
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_go_back),
                                contentDescription = "Go back"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        AnimatedContent(
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
                .verticalScroll(rememberScrollState()),
            targetState = state
        ) { state ->
            when (state) {
                is APIResult.Downloading -> {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .size(128.dp),
                            strokeWidth = 4.dp,
                            strokeCap = StrokeCap.Butt
                        )

                        Text(
                            text = stringResource(Res.string.loading_label),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineMedium,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.W500
                        )
                    }
                }

                is APIResult.Error<*> -> {
                    ErrorInfoPage(
                        errorInfo = stringResource(state.info.infoResource()),
                        onReloadPage = { onReloadAfterError(state.info) }
                    )
                }

                is APIResult.Succeed<*> -> {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .fillMaxWidth(0.8f),
                            painter = painterResource(Res.drawable.ic_success),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                        )

                        Text(
                            text = stringResource(Res.string.success_label),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineMedium,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.W500
                        )
                    }
                }
            }
        }
    }
}