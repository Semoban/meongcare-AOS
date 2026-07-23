package com.project.meongcare.feed.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.project.meongcare.R
import com.project.meongcare.designsystem.theme.Gray1
import com.project.meongcare.designsystem.theme.Gray3
import com.project.meongcare.designsystem.theme.Gray4
import com.project.meongcare.designsystem.theme.Gray5
import com.project.meongcare.designsystem.theme.Main3
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.Sub5
import com.project.meongcare.designsystem.theme.Sub6
import com.project.meongcare.designsystem.theme.Sub7
import com.project.meongcare.designsystem.theme.Sub8
import com.project.meongcare.designsystem.theme.White
import com.project.meongcare.feed.model.entities.FeedGetResponse
import com.project.meongcare.feed.model.entities.FeedPartRecord
import kotlin.math.roundToInt

@Composable
fun FeedScreen(
    feed: FeedGetResponse?,
    previousFeeds: List<FeedPartRecord>,
    onInputGuideClick: () -> Unit,
    onStopClick: () -> Unit,
    onSeeMoreClick: () -> Unit,
    onChangeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hasFeed = feed?.brand != null

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(White)
                .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (!hasFeed) {
            Image(
                painter = painterResource(R.drawable.feed_bowl_light),
                contentDescription = null,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 79.dp),
            )
            FeedGuideButton(
                text = stringResource(if (previousFeeds.isEmpty()) R.string.feed_input_guide else R.string.feed_select_guide),
                onClick = onInputGuideClick,
                modifier = Modifier.padding(start = 36.dp, top = 24.dp, end = 36.dp),
            )
        } else {
            Text(
                text = feed?.brand.orEmpty(),
                style = SemobanTypography.body1Medium,
                color = Gray4,
                modifier = Modifier.padding(top = 72.dp),
            )
            Text(
                text = feed?.feedName.orEmpty(),
                style = SemobanTypography.title3SemiBold,
                modifier = Modifier.padding(top = 8.dp),
            )
            AndroidView(
                factory = { context -> PieChart(context) },
                update = { chart ->
                    if (feed != null) {
                        bindNutrientPieChart(chart, feed)
                    }
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 112.dp)
                        .padding(top = 24.dp)
                        .aspectRatio(1f),
            )
        }
        NutrientTable(
            feed = feed,
            modifier = Modifier.padding(top = 32.dp),
        )
        IntakeInfoCard(
            days = feed?.days ?: 0L,
            recommendIntake = feed?.recommendIntake ?: 0,
            modifier = Modifier.padding(start = 24.dp, top = 32.dp, end = 24.dp),
        )
        if (hasFeed) {
            Text(
                text = stringResource(R.string.feed_stop_guide),
                style = SemobanTypography.body3Medium,
                color = Gray4,
                textDecoration = TextDecoration.Underline,
                modifier =
                    Modifier
                        .padding(top = 8.dp)
                        .clickable { onStopClick() },
            )
        }
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp, top = 32.dp, end = 28.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.feed_previous_list_title),
                style = SemobanTypography.title3SemiBold,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(R.string.feed_see_more),
                style = SemobanTypography.body3Medium,
                color = Gray4,
                modifier =
                    Modifier
                        .clickable { onSeeMoreClick() }
                        .padding(vertical = 6.dp),
            )
        }
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, top = 16.dp, end = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            previousFeeds.forEach { feedPartRecord ->
                FeedPartItem(feedPartRecord = feedPartRecord)
            }
        }
        if (hasFeed) {
            FeedGuideButton(
                text = stringResource(R.string.feed_change_guide),
                onClick = onChangeClick,
                modifier = Modifier.padding(start = 36.dp, top = 32.dp, end = 36.dp),
                backgroundColor = White,
                borderColor = Gray3,
            )
        }
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun NutrientTable(
    feed: FeedGetResponse?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        NutrientRow(labelColor = Main3, type = stringResource(R.string.feed_crude_protein_short), percentage = feed?.protein ?: 0.0)
        NutrientRow(labelColor = Sub7, type = stringResource(R.string.feed_crude_fat), percentage = feed?.fat ?: 0.0)
        NutrientRow(labelColor = Sub6, type = stringResource(R.string.feed_crude_ash), percentage = feed?.crudeAsh ?: 0.0)
        NutrientRow(labelColor = Sub8, type = stringResource(R.string.feed_moisture), percentage = feed?.moisture ?: 0.0)
        NutrientRow(labelColor = Sub5, type = stringResource(R.string.feed_etc), percentage = feed?.etc ?: 0.0)
    }
}

@Composable
private fun NutrientRow(
    labelColor: Color,
    type: String,
    percentage: Double,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 52.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(15.dp)
                    .background(labelColor, RoundedCornerShape(5.dp)),
        )
        Text(
            text = type,
            style = SemobanTypography.body2Medium,
            modifier =
                Modifier
                    .padding(start = 12.dp)
                    .weight(1f),
        )
        Text(
            text = "${percentage.roundToInt()}%",
            style = SemobanTypography.body2Regular,
            color = Gray4,
        )
    }
}

@Composable
private fun IntakeInfoCard(
    days: Long,
    recommendIntake: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(Gray1, RoundedCornerShape(10.dp))
                .padding(vertical = 19.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IntakeInfoItem(
            content = stringResource(R.string.feed_days_format, days),
            title = stringResource(R.string.feed_intake_days_title),
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier =
                Modifier
                    .size(width = 1.dp, height = 40.dp)
                    .background(Gray5),
        )
        IntakeInfoItem(
            content = "${recommendIntake}g",
            title = stringResource(R.string.feed_daily_intake_title),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun IntakeInfoItem(
    content: String,
    title: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = content,
            style = SemobanTypography.title3SemiBold,
        )
        Text(
            text = title,
            style = SemobanTypography.body1Medium,
            color = Gray5,
            modifier = Modifier.padding(top = 10.dp),
        )
    }
}

private fun bindNutrientPieChart(
    chart: PieChart,
    feed: FeedGetResponse,
) {
    val nutrientRatio =
        listOf(
            PieEntry(feed.protein.toFloat()),
            PieEntry(feed.fat.toFloat()),
            PieEntry(feed.crudeAsh.toFloat()),
            PieEntry(feed.moisture.toFloat()),
            PieEntry(feed.etc.toFloat()),
        )

    val dataSet =
        PieDataSet(nutrientRatio, "").apply {
            colors =
                listOf(
                    Main3.toArgb(),
                    Sub7.toArgb(),
                    Sub6.toArgb(),
                    Sub8.toArgb(),
                    Sub5.toArgb(),
                )
            setDrawValues(false)
        }

    chart.apply {
        data = PieData(dataSet)
        description.isEnabled = false
        legend.isEnabled = false
        isRotationEnabled = true
        holeRadius = 60f
        setTouchEnabled(false)
        animateY(1200, Easing.EaseInOutCubic)
        animate()
    }
}

@Preview(showBackground = true)
@Composable
private fun FeedScreenPreview() {
    SemobanTheme {
        FeedScreen(
            feed =
                FeedGetResponse(
                    brand = "로얄캐닌",
                    feedName = "미니 인도어 어덜트",
                    protein = 30.0,
                    fat = 15.0,
                    crudeAsh = 8.0,
                    moisture = 10.0,
                    etc = 37.0,
                    days = 32,
                    recommendIntake = 120,
                    feedId = 1L,
                    feedRecordId = 1L,
                ),
            previousFeeds =
                listOf(
                    FeedPartRecord("브랜드", "이전 사료", "2024-01-01", "2024-02-01", null, 1L),
                ),
            onInputGuideClick = {},
            onStopClick = {},
            onSeeMoreClick = {},
            onChangeClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FeedScreenEmptyPreview() {
    SemobanTheme {
        FeedScreen(
            feed = null,
            previousFeeds = emptyList(),
            onInputGuideClick = {},
            onStopClick = {},
            onSeeMoreClick = {},
            onChangeClick = {},
        )
    }
}
