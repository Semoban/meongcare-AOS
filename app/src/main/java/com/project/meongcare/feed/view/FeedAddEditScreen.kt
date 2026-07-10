package com.project.meongcare.feed.view

import android.view.LayoutInflater
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.archit.calendardaterangepicker.customviews.CalendarListener
import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView
import com.project.meongcare.R
import com.project.meongcare.designsystem.component.GlideImage
import com.project.meongcare.designsystem.theme.Black
import com.project.meongcare.designsystem.theme.Gray1
import com.project.meongcare.designsystem.theme.Gray2
import com.project.meongcare.designsystem.theme.Gray3
import com.project.meongcare.designsystem.theme.Gray4
import com.project.meongcare.designsystem.theme.Gray5
import com.project.meongcare.designsystem.theme.Main1
import com.project.meongcare.designsystem.theme.Main4
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.Sub1
import com.project.meongcare.designsystem.theme.White
import com.project.meongcare.feed.model.entities.FeedDetailGetResponse
import com.project.meongcare.feed.model.utils.FeedDateUtils.convertDateFormat
import com.project.meongcare.feed.model.utils.FeedInfoUtils.calculateRecommendDailyIntake
import com.project.meongcare.feed.model.utils.INTAKE_PERIOD_ERROR
import java.text.SimpleDateFormat
import java.util.Locale

data class FeedFormResult(
    val brand: String,
    val feedName: String,
    val protein: Double,
    val fat: Double,
    val crudeAsh: Double,
    val moisture: Double,
    val etc: Double,
    val kcal: Double,
    val recommendIntake: Int,
    val startDate: String,
    val endDate: String?,
)

private const val CALENDAR_NONE = 0
private const val CALENDAR_START = 1
private const val CALENDAR_END = 2

@Composable
fun FeedAddEditScreen(
    initialFeedInfo: FeedDetailGetResponse?,
    imageModel: Any?,
    dogWeight: Double,
    onBackClick: () -> Unit,
    onImageClick: () -> Unit,
    onComplete: (FeedFormResult) -> Unit,
    modifier: Modifier = Modifier,
) {
    var brand by rememberSaveable { mutableStateOf(initialFeedInfo?.brand.orEmpty()) }
    var feedName by rememberSaveable { mutableStateOf(initialFeedInfo?.feedName.orEmpty()) }
    var protein by rememberSaveable { mutableStateOf(initialFeedInfo?.protein?.toString().orEmpty()) }
    var fat by rememberSaveable { mutableStateOf(initialFeedInfo?.fat?.toString().orEmpty()) }
    var crudeAsh by rememberSaveable { mutableStateOf(initialFeedInfo?.crudeAsh?.toString().orEmpty()) }
    var moisture by rememberSaveable { mutableStateOf(initialFeedInfo?.moisture?.toString().orEmpty()) }
    var kcal by rememberSaveable { mutableStateOf(initialFeedInfo?.kcal?.toString().orEmpty()) }
    var startDate by rememberSaveable { mutableStateOf(initialFeedInfo?.startDate) }
    var endDate by rememberSaveable { mutableStateOf(initialFeedInfo?.endDate) }
    var endDateUnknown by rememberSaveable { mutableStateOf(initialFeedInfo != null && initialFeedInfo.endDate == null) }
    var calendarMode by rememberSaveable { mutableStateOf(CALENDAR_NONE) }

    var brandError by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf(false) }
    var ingredientError by remember { mutableStateOf<String?>(null) }
    var intakePeriodError by remember { mutableStateOf<String?>(null) }

    val etcValue = calculateEtc(protein, fat, crudeAsh, moisture)
    val recommendIntake = calculateRecommendIntake(dogWeight, kcal)

    fun updateIngredient(
        newValue: String,
        apply: (String) -> Unit,
    ) {
        apply(newValue)
        val newEtc = calculateEtc(protein, fat, crudeAsh, moisture)
        if (newEtc !in 0.0..100.0) {
            ingredientError = "사료 성분 비율의 총합은 100%를 초과할 수 없습니다."
            apply("")
        }
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(White),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(R.drawable.all_arrow_back_18dp),
                    contentDescription = "뒤로가기",
                )
            }
        }
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            FeedImageCard(
                imageModel = imageModel,
                onClick = onImageClick,
                modifier = Modifier.padding(top = 16.dp),
            )
            FeedFormLabel(text = "브랜드명", modifier = Modifier.padding(top = 32.dp))
            FeedFormTextField(
                value = brand,
                onValueChange = { brand = it },
                hint = "브랜드를 입력해주세요",
                showError = brandError,
                onErrorClick = { brandError = false },
            )
            FeedFormLabel(text = "제품명", modifier = Modifier.padding(top = 24.dp))
            FeedFormTextField(
                value = feedName,
                onValueChange = { feedName = it },
                hint = "제품명을 입력해주세요",
                showError = nameError,
                onErrorClick = { nameError = false },
            )
            FeedFormLabel(text = "사료 성분 비율", modifier = Modifier.padding(top = 24.dp))
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, top = 15.dp, end = 24.dp)
                        .border(1.dp, Gray3, RoundedCornerShape(5.dp)),
            ) {
                IngredientInputRow(
                    type = "조단백질",
                    value = protein,
                    onValueChange = { input -> updateIngredient(input) { protein = it } },
                )
                HorizontalDivider(thickness = 1.dp, color = Gray3)
                IngredientInputRow(
                    type = "조지방",
                    value = fat,
                    onValueChange = { input -> updateIngredient(input) { fat = it } },
                )
                HorizontalDivider(thickness = 1.dp, color = Gray3)
                IngredientInputRow(
                    type = "조회분",
                    value = crudeAsh,
                    onValueChange = { input -> updateIngredient(input) { crudeAsh = it } },
                )
                HorizontalDivider(thickness = 1.dp, color = Gray3)
                IngredientInputRow(
                    type = "수분",
                    value = moisture,
                    onValueChange = { input -> updateIngredient(input) { moisture = it } },
                )
                HorizontalDivider(thickness = 1.dp, color = Gray3)
                IngredientInputRow(
                    type = "기타",
                    value = formatIngredient(etcValue),
                    onValueChange = {},
                    readOnly = true,
                )
            }
            KcalInputBar(
                kcal = kcal,
                onKcalChange = { kcal = it },
                modifier = Modifier.padding(start = 24.dp, top = 8.dp, end = 24.dp),
            )
            if (ingredientError != null) {
                FeedFormErrorText(
                    text = ingredientError!!,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            RecommendIntakeBar(
                recommendIntake = recommendIntake,
                modifier = Modifier.padding(start = 24.dp, top = 8.dp, end = 24.dp),
            )
            FeedFormLabel(text = "섭취 기간", modifier = Modifier.padding(top = 24.dp))
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, top = 8.dp, end = 24.dp),
            ) {
                IntakePeriodBox(
                    text = startDate?.let { convertDateFormat(it) } ?: "시작 일자",
                    isSelected = startDate != null,
                    isActive = calendarMode == CALENDAR_START,
                    onClick = { calendarMode = CALENDAR_START },
                    modifier = Modifier.weight(1f),
                )
                IntakePeriodBox(
                    text =
                        when {
                            endDateUnknown -> "모름"
                            endDate != null -> convertDateFormat(endDate)
                            else -> "종료 일자"
                        },
                    isSelected = endDate != null || endDateUnknown,
                    isActive = calendarMode == CALENDAR_END,
                    onClick = { calendarMode = CALENDAR_END },
                    modifier =
                        Modifier
                            .weight(1f)
                            .padding(start = 8.dp),
                )
            }
            if (intakePeriodError != null) {
                FeedFormErrorText(
                    text = intakePeriodError!!,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            if (calendarMode != CALENDAR_NONE) {
                FeedRangeCalendar(
                    onDateSelected = { date ->
                        if (calendarMode == CALENDAR_START) {
                            startDate = date
                        } else {
                            endDate = date
                            endDateUnknown = false
                        }
                        intakePeriodError = null
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(top = 32.dp),
                )
            }
            if (calendarMode == CALENDAR_END) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 40.dp, top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter =
                            painterResource(
                                if (endDateUnknown) R.drawable.all_check_24dp else R.drawable.all_un_check_16dp,
                            ),
                        contentDescription = null,
                        modifier =
                            Modifier
                                .size(20.dp)
                                .clickable {
                                    endDateUnknown = !endDateUnknown
                                    if (endDateUnknown) {
                                        endDate = null
                                    }
                                },
                    )
                    Text(
                        text = "모름",
                        style = SemobanTypography.body1Medium,
                        color = Gray4,
                        modifier = Modifier.padding(start = 9.dp),
                    )
                }
            }
            Text(
                text = "완료",
                style = SemobanTypography.bottom1SemiBold,
                color = White,
                textAlign = TextAlign.Center,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 36.dp, top = 30.dp, end = 36.dp, bottom = 24.dp)
                        .background(Main4, RoundedCornerShape(5.dp))
                        .clickable {
                            var isValid = true

                            if (brand.isEmpty()) {
                                brandError = true
                                isValid = false
                            }
                            if (feedName.isEmpty()) {
                                nameError = true
                                isValid = false
                            }
                            if (protein.isEmpty() || fat.isEmpty() || crudeAsh.isEmpty() ||
                                moisture.isEmpty() || kcal.isEmpty()
                            ) {
                                ingredientError = "사료 성분 비율과 칼로리는 필수 항목입니다."
                                isValid = false
                            } else if (etcValue !in 0.0..100.0) {
                                ingredientError = "사료 성분 비율의 총합은 100%를 초과할 수 없습니다."
                                isValid = false
                            }
                            if (startDate == null || (endDate == null && !endDateUnknown)) {
                                intakePeriodError = "섭취 기간은 필수 항목입니다."
                                isValid = false
                            } else if (endDate != null &&
                                startDate!!.replace("-", "").toInt() > endDate!!.replace("-", "").toInt()
                            ) {
                                intakePeriodError = INTAKE_PERIOD_ERROR
                                isValid = false
                            }

                            if (isValid) {
                                onComplete(
                                    FeedFormResult(
                                        brand = brand,
                                        feedName = feedName,
                                        protein = protein.toDoubleOrNull() ?: 0.0,
                                        fat = fat.toDoubleOrNull() ?: 0.0,
                                        crudeAsh = crudeAsh.toDoubleOrNull() ?: 0.0,
                                        moisture = moisture.toDoubleOrNull() ?: 0.0,
                                        etc = etcValue,
                                        kcal = kcal.toDoubleOrNull() ?: 0.0,
                                        recommendIntake = recommendIntake.toInt(),
                                        startDate = startDate!!,
                                        endDate = if (endDateUnknown) null else endDate,
                                    ),
                                )
                            }
                        }
                        .padding(vertical = 12.dp),
            )
        }
    }
}

@Composable
private fun FeedImageCard(
    imageModel: Any?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(width = 250.dp, height = 150.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(Gray2)
                .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        if (imageModel == null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(R.drawable.feed_bowl_dark),
                    contentDescription = null,
                )
                Text(
                    text = "사진을 첨부해주세요",
                    style = SemobanTypography.body3Regular,
                    color = Gray5,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        } else {
            GlideImage(
                model = imageModel,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun FeedFormLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(start = 32.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = SemobanTypography.title3SemiBold,
        )
        Image(
            painter = painterResource(R.drawable.essential_input_element_icon),
            contentDescription = "필수 입력",
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}

@Composable
private fun FeedFormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    showError: Boolean,
    onErrorClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(start = 24.dp, top = 8.dp, end = 24.dp),
    ) {
        if (showError) {
            Text(
                text = "필수 입력 값입니다",
                style = SemobanTypography.body1Medium,
                color = Sub1,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(Gray1, RoundedCornerShape(5.dp))
                        .border(1.dp, Sub1, RoundedCornerShape(5.dp))
                        .clickable { onErrorClick() }
                        .padding(13.dp),
            )
        } else {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = SemobanTypography.body1Medium,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    Box(
                        modifier =
                            Modifier
                                .background(Gray1, RoundedCornerShape(5.dp))
                                .padding(13.dp),
                    ) {
                        if (value.isEmpty()) {
                            Text(
                                text = hint,
                                style = SemobanTypography.body1Medium,
                                color = Gray4,
                            )
                        }
                        innerTextField()
                    }
                },
            )
        }
    }
}

@Composable
private fun IngredientInputRow(
    type: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(start = 42.dp, top = 22.dp, end = 34.dp, bottom = 22.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = type,
            style = SemobanTypography.body2Medium,
            modifier = Modifier.weight(1f),
        )
        FeedNumberField(
            value = value,
            onValueChange = onValueChange,
            readOnly = readOnly,
            backgroundColor = Gray1,
            modifier = Modifier.width(75.dp),
        )
        Text(
            text = "%",
            style = SemobanTypography.body2Regular,
            modifier = Modifier.padding(start = 12.dp),
        )
    }
}

@Composable
private fun FeedNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    readOnly: Boolean,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = SemobanTypography.body2Regular,
) {
    BasicTextField(
        value = value,
        onValueChange = { input ->
            if (input.length <= INGREDIENT_INPUT_MAX_LENGTH) {
                onValueChange(input)
            }
        },
        textStyle = textStyle.copy(color = Black, textAlign = TextAlign.End),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        readOnly = readOnly,
        modifier = modifier,
        decorationBox = { innerTextField ->
            Box(
                modifier =
                    Modifier
                        .background(backgroundColor, RoundedCornerShape(5.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = "0.00",
                        style = textStyle,
                        color = Gray4,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                innerTextField()
            }
        },
    )
}

@Composable
private fun KcalInputBar(
    kcal: String,
    onKcalChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(54.dp)
                .background(Gray1, RoundedCornerShape(5.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "칼로리",
            style = SemobanTypography.body2Medium,
        )
        Box(
            modifier =
                Modifier
                    .padding(horizontal = 8.dp)
                    .border(1.dp, Gray3, RoundedCornerShape(5.dp))
                    .background(White, RoundedCornerShape(5.dp)),
        ) {
            FeedNumberField(
                value = kcal,
                onValueChange = onKcalChange,
                readOnly = false,
                backgroundColor = White,
                modifier = Modifier.width(75.dp),
            )
        }
        Text(
            text = "kcal/kg",
            style = SemobanTypography.body2Regular,
        )
    }
}

@Composable
private fun RecommendIntakeBar(
    recommendIntake: Double,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(54.dp)
                .background(Main1, RoundedCornerShape(5.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "추천하는 하루 섭취량:",
            style = SemobanTypography.body2Medium,
            color = Main4,
            modifier = Modifier.padding(start = 24.dp),
        )
        Text(
            text = "${recommendIntake}g",
            style = SemobanTypography.body2Medium,
            color = Main4,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}

@Composable
private fun IntakePeriodBox(
    text: String,
    isSelected: Boolean,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .background(Gray1, RoundedCornerShape(5.dp))
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = SemobanTypography.body1Regular,
            color = if (isSelected || isActive) Black else Gray4,
            modifier = Modifier.weight(1f),
        )
        if (!isSelected) {
            Image(
                painter = painterResource(R.drawable.all_calendar),
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun FeedFormErrorText(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = SemobanTypography.body2Medium,
        color = Sub1,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(start = 26.dp),
    )
}

@Composable
private fun FeedRangeCalendar(
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            LayoutInflater.from(context)
                .inflate(R.layout.view_feed_calendar, null) as DateRangeCalendarView
        },
        update = { calendar ->
            calendar.setCalendarListener(
                object : CalendarListener {
                    override fun onDateRangeSelected(
                        startDate: java.util.Calendar,
                        endDate: java.util.Calendar,
                    ) {
                        calendar.resetAllSelectedViews()
                    }

                    override fun onFirstDateSelected(startDate: java.util.Calendar) {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        onDateSelected(dateFormat.format(startDate.time))
                    }
                },
            )
        },
    )
}

private fun calculateEtc(
    protein: String,
    fat: String,
    crudeAsh: String,
    moisture: String,
): Double {
    val sum =
        (protein.toDoubleOrNull() ?: 0.0) +
            (fat.toDoubleOrNull() ?: 0.0) +
            (crudeAsh.toDoubleOrNull() ?: 0.0) +
            (moisture.toDoubleOrNull() ?: 0.0)
    return 100.0 - sum
}

private fun formatIngredient(value: Double) = String.format("%.2f", value)

private fun calculateRecommendIntake(
    dogWeight: Double,
    kcal: String,
): Double {
    val kcalValue = kcal.toDoubleOrNull() ?: return 0.0
    if (kcalValue <= 0.0) return 0.0

    val recommendIntake = calculateRecommendDailyIntake(dogWeight, kcalValue)
    return if (recommendIntake.isFinite()) recommendIntake else 0.0
}

private const val INGREDIENT_INPUT_MAX_LENGTH = 6

@Preview(showBackground = true)
@Composable
private fun FeedAddEditScreenPreview() {
    SemobanTheme {
        FeedAddEditScreen(
            initialFeedInfo = null,
            imageModel = null,
            dogWeight = 5.0,
            onBackClick = {},
            onImageClick = {},
            onComplete = {},
        )
    }
}
