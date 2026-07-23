package com.project.meongcare.symptom.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.meongcare.R
import com.project.meongcare.designsystem.theme.Gray1
import com.project.meongcare.designsystem.theme.Gray4
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.White
import java.util.Calendar

@Composable
fun SymptomEditScreen(
    dogName: String?,
    dateText: String?,
    timeText: String,
    selectedItemImgRes: Int,
    selectedItemTitle: String?,
    initialHour: Int?,
    initialMinute: Int?,
    onDateClick: () -> Unit,
    onSelectSymptomClick: () -> Unit,
    onTimeChanged: (hour: Int, minute: Int) -> Unit,
    onCustomItemEntered: (String) -> Unit,
    onCancel: () -> Unit,
    onComplete: (usePicker: Boolean, hour: Int, minute: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var dateError by remember { mutableStateOf(false) }
    var itemError by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(initialHour != null && initialMinute != null) }
    val calendar = remember { Calendar.getInstance() }
    var hour by remember { mutableIntStateOf(initialHour ?: calendar.get(Calendar.HOUR_OF_DAY)) }
    var minute by remember { mutableIntStateOf(initialMinute ?: calendar.get(Calendar.MINUTE)) }
    var customText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(dateText) {
        if (dateText != null) dateError = false
    }
    LaunchedEffect(selectedItemTitle) {
        if (!selectedItemTitle.isNullOrEmpty()) itemError = false
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(White),
    ) {
        SymptomTopBar(
            title = if (dogName.isNullOrBlank()) "" else stringResource(R.string.symptom_title_format, dogName),
        )
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
        ) {
            Text(text = stringResource(R.string.symptom_date_label), style = SemobanTypography.body1SemiBold)
            SymptomDateBox(
                dateText = dateText,
                isError = dateError,
                onClick = onDateClick,
                modifier = Modifier.padding(top = 10.dp),
            )
            Text(
                text = stringResource(R.string.symptom_time_label),
                style = SemobanTypography.body1SemiBold,
                modifier = Modifier.padding(top = 25.dp),
            )
            if (showTimePicker) {
                SpinnerTimePicker(
                    initialHour = initialHour,
                    initialMinute = initialMinute,
                    onTimeChanged = { h, m ->
                        hour = h
                        minute = m
                        onTimeChanged(h, m)
                    },
                    modifier = Modifier.padding(top = 10.dp),
                )
            } else {
                SymptomDateBox(
                    dateText = timeText,
                    isError = false,
                    onClick = { showTimePicker = true },
                    modifier = Modifier.padding(top = 10.dp),
                )
            }
            Text(
                text = stringResource(R.string.symptom_label),
                style = SemobanTypography.body1SemiBold,
                modifier = Modifier.padding(top = 25.dp),
            )
            SelectSymptomBox(
                isError = itemError,
                onClick = onSelectSymptomClick,
                modifier = Modifier.padding(top = 8.dp),
            )
            CustomSymptomTextField(
                value = customText,
                onValueChange = { customText = it },
                onDone = {
                    if (customText.trim().isNotEmpty()) {
                        onCustomItemEntered(customText.trim())
                        customText = ""
                        keyboardController?.hide()
                    }
                },
                modifier = Modifier.padding(top = 8.dp),
            )
            if (!selectedItemTitle.isNullOrEmpty()) {
                SelectedSymptomItem(
                    imgRes = selectedItemImgRes,
                    title = selectedItemTitle.trim(),
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
        CancelCompleteButtons(
            completeText = stringResource(R.string.all_completion),
            onCancel = onCancel,
            onComplete = {
                dateError = dateText == null
                itemError = selectedItemTitle.isNullOrEmpty()
                if (!dateError && !itemError) {
                    onComplete(showTimePicker, hour, minute)
                }
            },
            modifier = Modifier.padding(start = 22.dp, end = 22.dp, bottom = 20.dp, top = 10.dp),
        )
    }
}

@Composable
private fun CustomSymptomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = SemobanTypography.body2Medium.copy(textAlign = TextAlign.Center),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        modifier =
            modifier
                .fillMaxWidth()
                .height(46.dp)
                .background(Gray1, RoundedCornerShape(5.dp)),
        decorationBox = { innerTextField ->
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = stringResource(R.string.symptom_custom_edit_hint),
                        style = SemobanTypography.body2Medium.copy(textAlign = TextAlign.Center),
                        color = Gray4,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                innerTextField()
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun SymptomEditScreenPreview() {
    SemobanTheme {
        SymptomEditScreen(
            dogName = "몽실이",
            dateText = "2024-01-01",
            timeText = "오전 8:00",
            selectedItemImgRes = R.drawable.symptom_cough,
            selectedItemTitle = "기침을 한다",
            initialHour = null,
            initialMinute = null,
            onDateClick = {},
            onSelectSymptomClick = {},
            onTimeChanged = { _, _ -> },
            onCustomItemEntered = {},
            onCancel = {},
            onComplete = { _, _, _ -> },
        )
    }
}
