package com.project.meongcare.weight.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.project.meongcare.R
import com.project.meongcare.designsystem.theme.Gray1
import com.project.meongcare.designsystem.theme.Gray2
import com.project.meongcare.designsystem.theme.Gray3
import com.project.meongcare.designsystem.theme.Gray4
import com.project.meongcare.designsystem.theme.Gray5
import com.project.meongcare.designsystem.theme.Main4
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.Sub1
import com.project.meongcare.designsystem.theme.White
import com.project.meongcare.weight.model.entities.WeightMonthResponse
import com.project.meongcare.weight.model.entities.WeightWeekResponse
import com.project.meongcare.weight.model.entities.WeightWeeksResponse
import java.text.DecimalFormat
import kotlin.math.abs

@Composable
fun WeightScreen(
    dogName: String?,
    dailyWeight: Double?,
    weeklyWeights: WeightWeeksResponse?,
    monthlyWeight: WeightMonthResponse?,
    thisMonth: Float,
    isEditable: Boolean,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Gray2)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 10.dp),
    ) {
        // 미래 날짜에서는 편집 버튼을 숨기되 자리는 유지해 카드 위치가 흔들리지 않게 한다
        Text(
            text = stringResource(R.string.all_edit),
            style = SemobanTypography.body1Medium,
            color = Gray4,
            modifier =
                Modifier
                    .align(Alignment.End)
                    .padding(top = 32.dp, end = 17.dp)
                    .alpha(if (isEditable) 1f else 0f)
                    .clickable(enabled = isEditable) { onEditClick() },
        )
        DailyWeightCard(
            dogName = dogName,
            dailyWeight = dailyWeight,
            modifier = Modifier.padding(top = 16.dp),
        )
        WeeklyWeightCard(
            dogName = dogName,
            weeklyWeights = weeklyWeights,
            modifier = Modifier.padding(top = 8.dp),
        )
        MonthlyWeightCard(
            monthlyWeight = monthlyWeight,
            thisMonth = thisMonth,
            modifier = Modifier.padding(top = 8.dp),
        )
        Spacer(modifier = Modifier.height(96.dp))
    }
}

@Composable
private fun DailyWeightCard(
    dogName: String?,
    dailyWeight: Double?,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = White,
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 4.dp,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 17.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.petadd_weight),
                    style = SemobanTypography.title3SemiBold,
                )
                Text(
                    text = stringResource(R.string.weight_daily_desc_format, dogName.orEmpty()),
                    style = SemobanTypography.body3Regular,
                    color = Gray4,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            Text(
                text = "${dailyWeight ?: 0.0} KG",
                style = SemobanTypography.title1SemiBold,
            )
        }
    }
}

@Composable
private fun WeeklyWeightCard(
    dogName: String?,
    weeklyWeights: WeightWeeksResponse?,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = White,
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 4.dp,
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = stringResource(R.string.weight_weekly_title_format, dogName.orEmpty()),
                style = SemobanTypography.title3SemiBold,
            )
            Text(
                text = stringResource(R.string.weight_weekly_desc_format, dogName.orEmpty()),
                style = SemobanTypography.body2Regular,
                color = Gray5,
                modifier = Modifier.padding(top = 8.dp),
            )
            AndroidView(
                factory = { context -> LineChart(context) },
                update = { chart ->
                    if (weeklyWeights != null) {
                        bindWeeklyChart(chart, weeklyWeights)
                    }
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                        .height(164.dp),
            )
        }
    }
}

@Composable
private fun MonthlyWeightCard(
    monthlyWeight: WeightMonthResponse?,
    thisMonth: Float,
    modifier: Modifier = Modifier,
) {
    val monthlyWeightChange =
        if (monthlyWeight != null) monthlyWeight.thisMonthWeight - monthlyWeight.lastMonthWeight else 0.0
    val title =
        when {
            monthlyWeightChange > 0 -> stringResource(R.string.weight_monthly_increase_title)
            monthlyWeightChange < 0 -> stringResource(R.string.weight_monthly_decrease_title)
            else -> stringResource(R.string.weight_monthly_same_title)
        }
    val content =
        when {
            monthlyWeightChange > 0 -> stringResource(R.string.weight_monthly_increase_format, monthlyWeightChange)
            monthlyWeightChange < 0 -> stringResource(R.string.weight_monthly_decrease_format, abs(monthlyWeightChange))
            else -> stringResource(R.string.weight_monthly_same_desc)
        }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = White,
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 4.dp,
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = title,
                style = SemobanTypography.title3SemiBold,
            )
            Text(
                text = content,
                style = SemobanTypography.body2Regular,
                color = Gray5,
                modifier = Modifier.padding(top = 8.dp),
            )
            AndroidView(
                factory = { context -> BarChart(context) },
                update = { chart ->
                    if (monthlyWeight != null) {
                        bindMonthlyChart(chart, monthlyWeight, thisMonth)
                    }
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                        .height(164.dp),
            )
        }
    }
}

@Composable
internal fun WeightEditDialog(
    initialWeight: String,
    onCancel: () -> Unit,
    onConfirm: (Double) -> Unit,
) {
    var weightText by rememberSaveable { mutableStateOf(initialWeight) }
    var showError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onCancel) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = White,
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(modifier = Modifier.padding(vertical = 28.dp)) {
                Text(
                    text = stringResource(R.string.weight_edit_dialog_title),
                    style = SemobanTypography.title3SemiBold,
                    modifier = Modifier.padding(start = 25.dp),
                )
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, top = 20.dp, end = 24.dp),
                ) {
                    if (showError) {
                        Text(
                            text = stringResource(R.string.designsystem_required_input),
                            style = SemobanTypography.body1Medium,
                            color = Sub1,
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .background(Gray1, RoundedCornerShape(5.dp))
                                    .clickable { showError = false }
                                    .padding(13.dp),
                        )
                    } else {
                        BasicTextField(
                            value = weightText,
                            onValueChange = { input ->
                                if (input.length <= WEIGHT_INPUT_MAX_LENGTH) {
                                    weightText = input
                                }
                            },
                            textStyle = SemobanTypography.body1Regular,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .background(Gray1, RoundedCornerShape(5.dp))
                                    .padding(horizontal = 20.dp, vertical = 13.dp),
                        )
                    }
                }
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp, end = 23.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    WeightDialogButton(
                        text = stringResource(R.string.all_cancel),
                        backgroundColor = Gray2,
                        textColor = Gray5,
                        onClick = onCancel,
                    )
                    WeightDialogButton(
                        text = stringResource(R.string.all_confirm),
                        backgroundColor = Main4,
                        textColor = White,
                        onClick = {
                            val weight = weightText.toDoubleOrNull()
                            if (weight == null) {
                                showError = true
                            } else {
                                onConfirm(weight)
                            }
                        },
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun WeightDialogButton(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = SemobanTypography.bottom2SemiBold,
        color = textColor,
        modifier =
            modifier
                .background(backgroundColor, RoundedCornerShape(5.dp))
                .clickable { onClick() }
                .padding(horizontal = 32.dp, vertical = 7.dp),
    )
}

private fun bindWeeklyChart(
    chart: LineChart,
    response: WeightWeeksResponse,
) {
    val weightWeeklyData =
        response.weeks.mapIndexed { index, weightWeekResponse ->
            Entry((index + 1).toFloat(), weightWeekResponse.weight.toFloat())
        }

    val typo = ResourcesCompat.getFont(chart.context, R.font.pretendard_medium)

    val weightWeeklyDataSet =
        LineDataSet(weightWeeklyData, "").apply {
            valueTextSize = 12F
            valueTypeface = typo
            valueFormatter = WeightDataFormatter()
            color = Main4.toArgb()
            setCircleColor(Main4.toArgb())
            setDrawCircleHole(false)
            setDrawFilled(true)
            fillDrawable = ContextCompat.getDrawable(chart.context, R.drawable.weight_weekly_chart_gradient)
        }

    val weightWeeklyLabelColors =
        listOf(
            Gray3.toArgb(),
            Gray3.toArgb(),
            Gray3.toArgb(),
            Main4.toArgb(),
        )

    chart.apply {
        data = LineData(weightWeeklyDataSet)
        data.setValueTextColors(weightWeeklyLabelColors)

        xAxis.apply {
            granularity = 1F
            textSize = 14F
            typeface = typo
            textColor = Gray4.toArgb()
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = WeekFormatter(context.getString(R.string.weight_chart_week_pattern))
            spaceMin = 0.2F
            spaceMax = 0.2F
            setDrawGridLines(false)
            axisLineColor = White.toArgb()
        }

        axisLeft.apply {
            setDrawLabels(false)
            setDrawAxisLine(false)
            gridColor = Gray2.toArgb()
            gridLineWidth = 1F
        }

        axisRight.apply {
            setDrawLabels(false)
            setDrawAxisLine(false)
            setDrawGridLines(false)
        }

        description.isEnabled = false
        legend.xOffset = -50f
        setTouchEnabled(true)
        setScaleEnabled(false)
        setPinchZoom(false)
        setDrawMarkers(true)
        animateY(1200)
    }
}

private fun bindMonthlyChart(
    chart: BarChart,
    response: WeightMonthResponse,
    thisMonth: Float,
) {
    val weightMonthlyData =
        listOf(
            BarEntry(thisMonth - 1F, response.lastMonthWeight.toFloat()),
            BarEntry(thisMonth, response.thisMonthWeight.toFloat()),
        )

    val typo = ResourcesCompat.getFont(chart.context, R.font.pretendard_medium)

    val weightMonthlyDataSet =
        BarDataSet(weightMonthlyData, "").apply {
            colors = listOf(Gray3.toArgb(), Main4.toArgb())
        }

    val weightLabelColors =
        listOf(
            Gray5.toArgb(),
            Main4.toArgb(),
        )

    chart.apply {
        data = BarData(weightMonthlyDataSet)

        data.apply {
            barWidth = 0.5F
            setValueTextSize(14F)
            setValueTypeface(typo)
            setValueTextColors(weightLabelColors)
            setValueFormatter(WeightDataFormatter())
        }

        xAxis.apply {
            granularity = 1F
            position = XAxis.XAxisPosition.BOTTOM
            textSize = 14F
            typeface = typo
            valueFormatter =
                MonthFormatter(
                    context.getString(R.string.weight_chart_month_pattern),
                    context.getString(R.string.weight_chart_december),
                )
            setDrawGridLines(false)
        }

        axisLeft.apply {
            axisMinimum = 0F
            granularity = 1F
            setDrawLabels(false)
            setDrawAxisLine(false)
            gridColor = Gray2.toArgb()
            gridLineWidth = 1F
        }

        axisRight.apply {
            setDrawLabels(false)
            setDrawAxisLine(false)
            setDrawGridLines(false)
        }

        description.isEnabled = false
        legend.xOffset = -50f
        setTouchEnabled(false)
        setScaleEnabled(false)
        setPinchZoom(false)
        animateY(1000)
    }
}

private class WeekFormatter(pattern: String) : ValueFormatter() {
    private val format = DecimalFormat(pattern)

    override fun getFormattedValue(value: Float): String {
        return format.format(value)
    }
}

private class MonthFormatter(pattern: String, private val decemberLabel: String) : ValueFormatter() {
    private val format = DecimalFormat(pattern)

    override fun getFormattedValue(value: Float): String {
        return if (value == 0F) {
            decemberLabel
        } else {
            format.format(value)
        }
    }
}

private class WeightDataFormatter : ValueFormatter() {
    private val format = DecimalFormat("0.00kg")

    override fun getFormattedValue(value: Float): String {
        return format.format(value)
    }
}

private const val WEIGHT_INPUT_MAX_LENGTH = 6

@Preview(showBackground = true)
@Composable
private fun WeightScreenPreview() {
    SemobanTheme {
        WeightScreen(
            dogName = "몽실이",
            dailyWeight = 5.2,
            weeklyWeights =
                WeightWeeksResponse(
                    listOf(
                        WeightWeekResponse(5.0, "2024-01-01", "2024-01-07"),
                        WeightWeekResponse(5.1, "2024-01-08", "2024-01-14"),
                        WeightWeekResponse(5.3, "2024-01-15", "2024-01-21"),
                        WeightWeekResponse(5.2, "2024-01-22", "2024-01-28"),
                    ),
                ),
            monthlyWeight = WeightMonthResponse(5.0, 5.2),
            thisMonth = 7F,
            isEditable = true,
            onEditClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WeightEditDialogPreview() {
    SemobanTheme {
        WeightEditDialog(
            initialWeight = "5.2",
            onCancel = {},
            onConfirm = {},
        )
    }
}
