package com.project.meongcare.symptom.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.meongcare.R
import com.project.meongcare.designsystem.theme.Main4
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.White
import java.util.Calendar

@Composable
fun SymptomAddScreen(
    dateText: String?,
    selectedItemImgRes: Int,
    selectedItemTitle: String?,
    initialHour: Int?,
    initialMinute: Int?,
    onBack: () -> Unit,
    onDateClick: () -> Unit,
    onSelectSymptomClick: () -> Unit,
    onTimeChanged: (hour: Int, minute: Int) -> Unit,
    onComplete: (hour: Int, minute: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var dateError by remember { mutableStateOf(false) }
    var itemError by remember { mutableStateOf(false) }
    val calendar = remember { Calendar.getInstance() }
    var hour by remember { mutableIntStateOf(initialHour ?: calendar.get(Calendar.HOUR_OF_DAY)) }
    var minute by remember { mutableIntStateOf(initialMinute ?: calendar.get(Calendar.MINUTE)) }

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
        SymptomTopBar(onBack = onBack)
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(32.dp),
        ) {
            EssentialLabel(text = stringResource(R.string.symptom_date_label))
            SymptomDateBox(
                dateText = dateText,
                isError = dateError,
                showCalendarIcon = true,
                onClick = onDateClick,
                modifier = Modifier.padding(top = 8.dp),
            )
            EssentialLabel(
                text = stringResource(R.string.symptom_time_label),
                modifier = Modifier.padding(top = 22.dp),
            )
            SpinnerTimePicker(
                initialHour = initialHour,
                initialMinute = initialMinute,
                onTimeChanged = { h, m ->
                    hour = h
                    minute = m
                    onTimeChanged(h, m)
                },
            )
            EssentialLabel(
                text = stringResource(R.string.symptom_label),
                modifier = Modifier.padding(top = 8.dp),
            )
            SelectSymptomBox(
                isError = itemError,
                onClick = onSelectSymptomClick,
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
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(start = 36.dp, end = 36.dp, bottom = 36.dp, top = 10.dp)
                    .height(44.dp)
                    .background(Main4, RoundedCornerShape(5.dp))
                    .clickable {
                        dateError = dateText == null
                        itemError = selectedItemTitle.isNullOrEmpty()
                        if (!dateError && !itemError) {
                            onComplete(hour, minute)
                        }
                    },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.all_completion),
                style = SemobanTypography.bottom1SemiBold,
                color = White,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SymptomAddScreenPreview() {
    SemobanTheme {
        SymptomAddScreen(
            dateText = null,
            selectedItemImgRes = 0,
            selectedItemTitle = null,
            initialHour = null,
            initialMinute = null,
            onBack = {},
            onDateClick = {},
            onSelectSymptomClick = {},
            onTimeChanged = { _, _ -> },
            onComplete = { _, _ -> },
        )
    }
}
