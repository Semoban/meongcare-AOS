package com.project.meongcare.excreta.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.meongcare.R
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.White
import com.project.meongcare.excreta.model.entities.Excreta

@Composable
fun ExcretaInfoScreen(
    imageUrl: String?,
    dateText: String,
    excretaType: Excreta?,
    timeText: String,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hasImage = !imageUrl.isNullOrEmpty()
    var overlayVisible by remember(imageUrl) { mutableStateOf(hasImage) }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(White),
    ) {
        ExcretaTopBar(
            onBack = onBack,
            actions = {
                IconButton(onClick = onEdit) {
                    Image(
                        painter = painterResource(R.drawable.all_edit),
                        contentDescription = stringResource(R.string.all_modify),
                    )
                }
                IconButton(onClick = onDelete) {
                    Image(
                        painter = painterResource(R.drawable.all_delete),
                        contentDescription = stringResource(R.string.all_delete),
                    )
                }
            },
        )
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
        ) {
            ExcretaImageCard(
                imageModel = imageUrl.takeUnless { it.isNullOrEmpty() },
                contentPadding = PaddingValues(horizontal = 105.dp, vertical = 60.dp),
                onClick = if (hasImage) fun() { overlayVisible = true } else null,
                overlay =
                    if (overlayVisible) {
                        {
                            ExcretaPrivacyOverlay(onClick = { overlayVisible = false })
                        }
                    } else {
                        null
                    },
                modifier =
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp),
            )
            Text(
                text = stringResource(R.string.excreta_date_label),
                style = SemobanTypography.body1SemiBold,
                modifier = Modifier.padding(start = 8.dp, top = 24.dp),
            )
            ExcretaValueBox(
                text = dateText,
                modifier = Modifier.padding(top = 8.dp),
            )
            ExcretaTypeSelector(
                excretaType = excretaType,
                showEssential = false,
                modifier = Modifier.padding(start = 8.dp, top = 24.dp),
            )
            Text(
                text = stringResource(R.string.excreta_time_label),
                style = SemobanTypography.title3SemiBold,
                modifier = Modifier.padding(start = 8.dp, top = 24.dp),
            )
            ExcretaValueBox(
                text = timeText,
                modifier = Modifier.padding(top = 8.dp, bottom = 56.dp),
            )
        }
    }
}

@Composable
private fun BoxScope.ExcretaPrivacyOverlay(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .matchParentSize()
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF646468), Color(0xFFBDBDBE)),
                    ),
                    RoundedCornerShape(30.dp),
                )
                .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.excreta_visibility_off),
            contentDescription = null,
        )
        Text(
            text = stringResource(R.string.excreta_photo_info),
            style = SemobanTypography.body3Regular,
            color = White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ExcretaInfoScreenPreview() {
    SemobanTheme {
        ExcretaInfoScreen(
            imageUrl = null,
            dateText = "2024년 01월 01일",
            excretaType = Excreta.FECES,
            timeText = "오전 08:00",
            onBack = {},
            onEdit = {},
            onDelete = {},
        )
    }
}
