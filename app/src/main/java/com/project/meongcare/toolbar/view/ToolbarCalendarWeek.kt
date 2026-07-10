package com.project.meongcare.toolbar.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.meongcare.R
import com.project.meongcare.designsystem.theme.Black
import com.project.meongcare.designsystem.theme.Main1
import com.project.meongcare.designsystem.theme.Main4
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.White
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private const val WEEK_SWIPE_DISTANCE_THRESHOLD = 100f
internal const val DAYS_IN_WEEK = 7

// 주간 달력 툴바 — symptom/excreta/supplement/weight 메인 화면 상단에서 공유한다
@Composable
internal fun ToolbarCalendarWeek(
    selectedDate: Date?,
    dateList: List<Date>,
    selectedDatePos: Int?,
    onTitleClick: () -> Unit,
    onDateClick: (Int) -> Unit,
    onWeekSwipe: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = White,
        shape = RoundedCornerShape(bottomStart = 11.dp, bottomEnd = 11.dp),
        shadowElevation = 6.dp,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                modifier =
                    Modifier
                        .padding(vertical = 19.dp)
                        .clickable { onTitleClick() },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = selectedDate?.let { getMonthDateDay(it) }.orEmpty(),
                    style = SemobanTypography.title2SemiBold,
                    color = Black,
                )
                Image(
                    painter = painterResource(R.drawable.toolbar_calendar_dropdown_stroke_10dp),
                    contentDescription = null,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
            CalendarWeekRow(
                dateList = dateList,
                selectedDatePos = selectedDatePos,
                onDateClick = onDateClick,
                onWeekSwipe = onWeekSwipe,
            )
        }
    }
}

// 주간 날짜 스트립 — 좌우 스와이프로 일주일 단위 이동 (HomeScreen에서도 재사용)
@Composable
internal fun CalendarWeekRow(
    dateList: List<Date>,
    selectedDatePos: Int?,
    onDateClick: (Int) -> Unit,
    onWeekSwipe: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    var totalDrag = 0f
                    detectHorizontalDragGestures(
                        onDragStart = { totalDrag = 0f },
                        onDragEnd = {
                            if (totalDrag <= -WEEK_SWIPE_DISTANCE_THRESHOLD) {
                                onWeekSwipe(DAYS_IN_WEEK)
                            } else if (totalDrag >= WEEK_SWIPE_DISTANCE_THRESHOLD) {
                                onWeekSwipe(-DAYS_IN_WEEK)
                            }
                        },
                    ) { _, dragAmount -> totalDrag += dragAmount }
                }
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
    ) {
        dateList.forEachIndexed { index, date ->
            CalendarWeekDayItem(
                date = date,
                isSelected = index == selectedDatePos,
                onClick = { onDateClick(index) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun CalendarWeekDayItem(
    date: Date,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val textColor = if (isSelected) Main4 else Black
    Column(
        modifier =
            modifier
                .clip(RoundedCornerShape(10.dp))
                .background(if (isSelected) Main1 else White)
                .clickable { onClick() }
                .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = getDay(date),
            style = SemobanTypography.body1Medium,
            color = textColor,
        )
        Text(
            text = getDate(date),
            style = SemobanTypography.body1Medium,
            color = textColor,
        )
    }
}

fun getDay(date: Date): String = SimpleDateFormat("EE", Locale.getDefault()).format(date)

fun getDate(date: Date): String = SimpleDateFormat("d", Locale.getDefault()).format(date)

fun getMonthDateDay(date: Date): String = SimpleDateFormat("MM.dd EE", Locale.getDefault()).format(date)

@Preview(showBackground = true)
@Composable
private fun ToolbarCalendarWeekPreview() {
    val calendar = Calendar.getInstance()
    SemobanTheme {
        ToolbarCalendarWeek(
            selectedDate = calendar.time,
            dateList =
                List(7) { index ->
                    calendar.apply { set(Calendar.DAY_OF_WEEK, index + 1) }.time
                },
            selectedDatePos = 3,
            onTitleClick = {},
            onDateClick = {},
            onWeekSwipe = {},
        )
    }
}
