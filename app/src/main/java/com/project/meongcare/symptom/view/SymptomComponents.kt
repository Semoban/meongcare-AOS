package com.project.meongcare.symptom.view

import android.view.LayoutInflater
import android.widget.TimePicker
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.project.meongcare.R
import com.project.meongcare.designsystem.theme.Gray1
import com.project.meongcare.designsystem.theme.Gray2
import com.project.meongcare.designsystem.theme.Gray3
import com.project.meongcare.designsystem.theme.Gray4
import com.project.meongcare.designsystem.theme.Gray5
import com.project.meongcare.designsystem.theme.Main4
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.Sub1
import com.project.meongcare.designsystem.theme.White

@Composable
internal fun SymptomTopBar(
    title: String? = null,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(56.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.all_arrow_back_18dp),
                    contentDescription = "뒤로가기",
                )
            }
        }
        if (title != null) {
            Text(
                text = title,
                style = SemobanTypography.title2SemiBold,
                modifier = Modifier.padding(start = if (onBack != null) 8.dp else 24.dp),
            )
        }
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            content = actions,
        )
    }
}

@Composable
internal fun EssentialLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = text, style = SemobanTypography.body1SemiBold)
        Image(
            painter = painterResource(R.drawable.essential_input_element_icon),
            contentDescription = "필수 입력",
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}

@Composable
internal fun SymptomDateBox(
    dateText: String?,
    isError: Boolean,
    placeholder: String = "날짜를 선택해주세요",
    showCalendarIcon: Boolean = false,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    var boxModifier =
        modifier
            .fillMaxWidth()
            .background(Gray1, RoundedCornerShape(5.dp))
    if (isError && dateText == null) {
        boxModifier = boxModifier.border(1.dp, Sub1, RoundedCornerShape(5.dp))
    }
    if (onClick != null) {
        boxModifier = boxModifier.clickable { onClick() }
    }
    Row(
        modifier = boxModifier.padding(horizontal = if (showCalendarIcon) 20.dp else 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val (text, style, color) =
            when {
                dateText != null -> Triple(dateText, SemobanTypography.body1Medium, SemobanTypography.body1Medium.color)
                isError -> Triple("필수 입력 값입니다.", SemobanTypography.body1Regular, Sub1)
                else -> Triple(placeholder, SemobanTypography.body1Regular, Gray4)
            }
        Text(
            text = text,
            style = style,
            color = color,
            modifier = Modifier.weight(1f),
        )
        if (showCalendarIcon) {
            Image(
                painter = painterResource(R.drawable.all_calendar_gray4),
                contentDescription = null,
            )
        }
    }
}

@Composable
internal fun SelectSymptomBox(
    isError: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val background = if (isError) Gray1 else White
    val borderColor = if (isError) Sub1 else Gray2
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(background, RoundedCornerShape(5.dp))
                .border(1.dp, borderColor, RoundedCornerShape(5.dp))
                .clickable { onClick() }
                .padding(horizontal = 18.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = if (isError) "필수 입력 값입니다." else "증상을 선택해주세요",
            style = SemobanTypography.body1Regular,
            color = if (isError) Sub1 else Gray4,
            modifier = Modifier.weight(1f),
        )
        Image(
            painter = painterResource(R.drawable.all_arrow_forward),
            contentDescription = null,
        )
    }
}

@Composable
internal fun SelectedSymptomItem(
    imgRes: Int,
    title: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(White, RoundedCornerShape(5.dp)),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp)
                    .height(64.dp)
                    .background(Gray2, RoundedCornerShape(5.dp))
                    .padding(start = 22.dp, end = 12.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(imgRes),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
            )
            Text(
                text = title,
                style = SemobanTypography.body2Medium,
                modifier = Modifier.padding(start = 13.dp),
            )
        }
    }
}

@Composable
internal fun CheckToggleIcon(
    checked: Boolean,
    modifier: Modifier = Modifier,
) {
    Image(
        painter =
            painterResource(
                if (checked) R.drawable.all_check_20dp else R.drawable.all_un_check_20dp,
            ),
        contentDescription = if (checked) "선택됨" else "선택 안 됨",
        modifier = modifier.size(20.dp),
    )
}

@Composable
internal fun CancelCompleteButtons(
    completeText: String,
    onCancel: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(45.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(White, RoundedCornerShape(4.dp))
                    .border(1.dp, Gray3, RoundedCornerShape(4.dp))
                    .clickable { onCancel() },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "취소",
                style = SemobanTypography.bottom1SemiBold,
                color = Gray5,
            )
        }
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(start = 8.dp)
                    .background(Main4, RoundedCornerShape(4.dp))
                    .clickable { onComplete() },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = completeText,
                style = SemobanTypography.bottom1SemiBold,
                color = White,
            )
        }
    }
}

@Composable
internal fun SpinnerTimePicker(
    initialHour: Int?,
    initialMinute: Int?,
    onTimeChanged: (hour: Int, minute: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            val timePicker =
                LayoutInflater.from(context)
                    .inflate(R.layout.view_spinner_time_picker, null) as TimePicker
            if (initialHour != null && initialMinute != null) {
                timePicker.hour = initialHour
                timePicker.minute = initialMinute
            }
            timePicker.setOnTimeChangedListener { _, hour, minute ->
                onTimeChanged(hour, minute)
            }
            timePicker
        },
    )
}

@Composable
internal fun SymptomNoData(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.all_paws),
                contentDescription = null,
            )
            Text(
                text = "이상증상을 발견하지 못했어요!",
                style = SemobanTypography.body1Medium,
                color = Gray5,
                modifier = Modifier.padding(top = 16.dp),
            )
        }
    }
}
