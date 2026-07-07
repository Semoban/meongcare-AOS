package com.project.meongcare.medicalRecord.view

import android.graphics.Typeface
import android.view.LayoutInflater
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.archit.calendardaterangepicker.customviews.CalendarListener
import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView
import com.project.meongcare.R
import com.project.meongcare.designsystem.theme.Gray1
import com.project.meongcare.designsystem.theme.Gray2
import com.project.meongcare.designsystem.theme.Gray4
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.White
import com.project.meongcare.medicalRecord.model.entities.MedicalRecord
import com.project.meongcare.medicalRecord.model.utils.MedicalRecordDateUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun MedicalRecordScreen(
    dogName: String?,
    selectedDate: String?,
    records: List<MedicalRecord>,
    onDateSelected: (String?) -> Unit,
    onAddClick: () -> Unit,
    onEditClick: () -> Unit,
    onItemClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(White),
    ) {
        Text(
            text = stringResource(R.string.medicalrecord_header),
            style = SemobanTypography.title2SemiBold,
            modifier = Modifier.padding(start = 24.dp, top = 14.dp),
        )
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 16.dp),
        ) {
            MedicalRecordCalendar(
                onDateSelected = onDateSelected,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .padding(top = 24.dp),
            )
            HorizontalDivider(
                thickness = 8.dp,
                color = Gray1,
                modifier = Modifier.padding(top = 20.dp),
            )
            Text(
                text = selectedDate?.let { convertToDisplayDate(it) }.orEmpty(),
                style = SemobanTypography.body2Regular,
                color = Gray4,
                modifier = Modifier.padding(start = 26.dp, top = 24.dp),
            )
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 26.dp, top = 8.dp, end = 25.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${dogName.orEmpty()}님의 진료 기록",
                    style = SemobanTypography.title3SemiBold,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = stringResource(R.string.all_add),
                    style = SemobanTypography.body2Regular,
                    color = Gray4,
                    modifier = Modifier.clickable { onAddClick() },
                )
                Text(
                    text = stringResource(R.string.all_edit),
                    style = SemobanTypography.body2Regular,
                    color = Gray4,
                    modifier =
                        Modifier
                            .padding(start = 13.dp)
                            .clickable { onEditClick() },
                )
            }
            if (selectedDate == null || records.isEmpty()) {
                MedicalRecordEmptyContent(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                )
            } else {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, top = 24.dp, end = 24.dp),
                ) {
                    records.forEach { record ->
                        MedicalRecordItem(
                            record = record,
                            onClick = { onItemClick(record.medicalRecordId) },
                            modifier = Modifier.padding(bottom = 15.dp),
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(150.dp))
        }
    }
}

@Composable
private fun MedicalRecordEmptyContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(R.drawable.all_no_list_icon),
            contentDescription = null,
            modifier = Modifier.size(width = 42.dp, height = 35.dp),
        )
        Text(
            text = stringResource(R.string.medicalrecord_no_list),
            style = SemobanTypography.body1Medium,
            color = Gray4,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}

@Composable
private fun MedicalRecordItem(
    record: MedicalRecord,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(52.dp)
                .background(Gray2, RoundedCornerShape(5.dp))
                .clickable { onClick() }
                .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = MedicalRecordDateUtils.showFormattedTime(record.dateTime),
            style = SemobanTypography.body1Medium,
            modifier = Modifier.weight(1f),
        )
        Image(
            painter = painterResource(R.drawable.all_arrow_navigate_next),
            contentDescription = null,
        )
    }
}

@Composable
private fun MedicalRecordCalendar(
    onDateSelected: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            val calendar =
                LayoutInflater.from(context)
                    .inflate(R.layout.view_medical_record_calendar, null) as DateRangeCalendarView

            val calendarTypeface = Typeface.createFromAsset(context.assets, "pretendard_regular.otf")
            calendar.setFonts(calendarTypeface)

            val currentMonth = Calendar.getInstance()
            val pastMonth = Calendar.getInstance()
            pastMonth.add(Calendar.MONTH, -282)
            calendar.setVisibleMonthRange(pastMonth, currentMonth)
            calendar.setCurrentMonth(currentMonth)
            calendar
        },
        update = { calendar ->
            calendar.setCalendarListener(
                object : CalendarListener {
                    override fun onDateRangeSelected(
                        startDate: Calendar,
                        endDate: Calendar,
                    ) {
                        calendar.resetAllSelectedViews()
                        onDateSelected(null)
                    }

                    override fun onFirstDateSelected(startDate: Calendar) {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        onDateSelected(dateFormat.format(startDate.time))
                    }
                },
            )
        },
    )
}

private fun convertToDisplayDate(date: String): String {
    val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputDateFormat = SimpleDateFormat("yyyy. MM. dd", Locale.getDefault())

    val parsedDate = inputDateFormat.parse(date) ?: return ""
    return outputDateFormat.format(parsedDate)
}

@Preview(showBackground = true)
@Composable
private fun MedicalRecordScreenPreview() {
    SemobanTheme {
        MedicalRecordScreen(
            dogName = "몽실이",
            selectedDate = "2024-01-01",
            records =
                listOf(
                    MedicalRecord(1L, "2024-01-01T08:00:00"),
                    MedicalRecord(2L, "2024-01-01T14:30:00"),
                ),
            onDateSelected = {},
            onAddClick = {},
            onEditClick = {},
            onItemClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MedicalRecordScreenEmptyPreview() {
    SemobanTheme {
        MedicalRecordScreen(
            dogName = "몽실이",
            selectedDate = null,
            records = emptyList(),
            onDateSelected = {},
            onAddClick = {},
            onEditClick = {},
            onItemClick = {},
        )
    }
}
