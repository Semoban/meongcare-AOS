package com.project.meongcare.excreta.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.White
import com.project.meongcare.excreta.model.entities.Excreta
import java.util.Calendar

@Composable
fun ExcretaAddEditScreen(
    imageModel: Any?,
    dateText: String?,
    initialExcretaType: Excreta,
    initialHour: Int?,
    initialMinute: Int?,
    onBack: () -> Unit,
    onImageClick: () -> Unit,
    onDateClick: () -> Unit,
    onComplete: (excretaType: Excreta, hour: Int, minute: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var excretaType by remember { mutableStateOf(initialExcretaType) }
    var dateError by remember { mutableStateOf(false) }
    val calendar = remember { Calendar.getInstance() }
    var hour by remember { mutableIntStateOf(initialHour ?: calendar.get(Calendar.HOUR_OF_DAY)) }
    var minute by remember { mutableIntStateOf(initialMinute ?: calendar.get(Calendar.MINUTE)) }

    LaunchedEffect(dateText) {
        if (dateText != null) dateError = false
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(White),
    ) {
        ExcretaTopBar(onBack = onBack)
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
        ) {
            ExcretaImageCard(
                imageModel = imageModel,
                contentPadding = PaddingValues(horizontal = 76.dp, vertical = 46.dp),
                placeholderText = "사진을 첨부해주세요",
                onClick = onImageClick,
                modifier =
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp),
            )
            EssentialLabel(
                text = "날짜",
                modifier = Modifier.padding(start = 8.dp, top = 24.dp),
            )
            ExcretaDateBox(
                dateText = dateText,
                isError = dateError,
                onClick = {
                    dateError = false
                    onDateClick()
                },
                modifier = Modifier.padding(top = 8.dp),
            )
            ExcretaTypeSelector(
                excretaType = excretaType,
                onTypeChange = { excretaType = it },
                modifier = Modifier.padding(start = 8.dp, top = 24.dp),
            )
            EssentialLabel(
                text = "시각",
                style = SemobanTypography.title3SemiBold,
                modifier = Modifier.padding(start = 8.dp, top = 24.dp),
            )
            SpinnerTimePicker(
                initialHour = initialHour,
                initialMinute = initialMinute,
                onTimeChanged = { h, m ->
                    hour = h
                    minute = m
                },
                modifier = Modifier.padding(top = 8.dp),
            )
            ExcretaCompleteButton(
                onClick = {
                    dateError = dateText == null
                    if (!dateError) {
                        onComplete(excretaType, hour, minute)
                    }
                },
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 24.dp, bottom = 36.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ExcretaAddEditScreenPreview() {
    SemobanTheme {
        ExcretaAddEditScreen(
            imageModel = null,
            dateText = null,
            initialExcretaType = Excreta.URINE,
            initialHour = null,
            initialMinute = null,
            onBack = {},
            onImageClick = {},
            onDateClick = {},
            onComplete = { _, _, _ -> },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ExcretaAddEditScreenFilledPreview() {
    SemobanTheme {
        ExcretaAddEditScreen(
            imageModel = null,
            dateText = "2024년 01월 01일",
            initialExcretaType = Excreta.FECES,
            initialHour = 8,
            initialMinute = 30,
            onBack = {},
            onImageClick = {},
            onDateClick = {},
            onComplete = { _, _, _ -> },
        )
    }
}
