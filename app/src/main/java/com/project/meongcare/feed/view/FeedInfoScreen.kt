package com.project.meongcare.feed.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.meongcare.R
import com.project.meongcare.designsystem.component.GlideImage
import com.project.meongcare.designsystem.theme.Gray1
import com.project.meongcare.designsystem.theme.Gray3
import com.project.meongcare.designsystem.theme.Main1
import com.project.meongcare.designsystem.theme.Main4
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.White
import com.project.meongcare.feed.model.entities.FeedDetailGetResponse
import com.project.meongcare.feed.model.utils.FeedDateUtils.convertDateFormat

@Composable
fun FeedInfoScreen(
    feedInfo: FeedDetailGetResponse?,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "수정",
                style = SemobanTypography.body1Medium,
                modifier =
                    Modifier
                        .clickable { onEditClick() }
                        .padding(8.dp),
            )
            Text(
                text = "삭제",
                style = SemobanTypography.body1Medium,
                modifier =
                    Modifier
                        .clickable { onDeleteClick() }
                        .padding(start = 8.dp, top = 8.dp, end = 16.dp, bottom = 8.dp),
            )
        }
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val imageModifier =
                Modifier
                    .padding(top = 16.dp)
                    .size(width = 250.dp, height = 150.dp)
            if (feedInfo?.imageURL.isNullOrEmpty()) {
                Image(
                    painter = painterResource(R.drawable.feed_default_image),
                    contentDescription = null,
                    modifier = imageModifier,
                )
            } else {
                GlideImage(
                    model = feedInfo?.imageURL!!,
                    modifier = imageModifier,
                )
            }
            FeedInfoLabel(text = "브랜드명", modifier = Modifier.padding(top = 32.dp))
            FeedInfoContentBox(text = feedInfo?.brand.orEmpty())
            FeedInfoLabel(text = "제품명", modifier = Modifier.padding(top = 24.dp))
            FeedInfoContentBox(text = feedInfo?.feedName.orEmpty())
            FeedInfoLabel(text = "사료 성분 비율", modifier = Modifier.padding(top = 24.dp))
            IngredientTable(
                feedInfo = feedInfo,
                modifier = Modifier.padding(start = 24.dp, top = 15.dp, end = 24.dp),
            )
            KcalBar(
                kcal = feedInfo?.kcal ?: 0.0,
                modifier = Modifier.padding(start = 24.dp, top = 8.dp, end = 24.dp),
            )
            RecommendIntakeBar(
                recommendIntake = feedInfo?.recommendIntake ?: 0L,
                modifier = Modifier.padding(start = 24.dp, top = 8.dp, end = 24.dp),
            )
            FeedInfoLabel(text = "섭취 기간", modifier = Modifier.padding(top = 24.dp))
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, top = 8.dp, end = 24.dp, bottom = 40.dp),
            ) {
                FeedInfoDateBox(
                    text = convertDateFormat(feedInfo?.startDate),
                    modifier = Modifier.weight(1f),
                )
                FeedInfoDateBox(
                    text = if (feedInfo?.endDate != null) convertDateFormat(feedInfo.endDate) else "모름",
                    modifier =
                        Modifier
                            .weight(1f)
                            .padding(start = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun FeedInfoLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = SemobanTypography.title3SemiBold,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(start = 32.dp),
    )
}

@Composable
private fun FeedInfoContentBox(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = SemobanTypography.body1Regular,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(start = 24.dp, top = 8.dp, end = 24.dp)
                .background(Gray1, RoundedCornerShape(5.dp))
                .padding(horizontal = 16.dp, vertical = 13.dp),
    )
}

@Composable
private fun FeedInfoDateBox(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = SemobanTypography.body1Regular,
        modifier =
            modifier
                .background(Gray1, RoundedCornerShape(5.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp),
    )
}

@Composable
private fun IngredientTable(
    feedInfo: FeedDetailGetResponse?,
    modifier: Modifier = Modifier,
) {
    val ingredients =
        listOf(
            "조단백질" to (feedInfo?.protein ?: 0.0),
            "조지방" to (feedInfo?.fat ?: 0.0),
            "조회분" to (feedInfo?.crudeAsh ?: 0.0),
            "수분" to (feedInfo?.moisture ?: 0.0),
            "기타" to (feedInfo?.etc ?: 0.0),
        )

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .border(1.dp, Gray3, RoundedCornerShape(5.dp)),
    ) {
        ingredients.forEachIndexed { index, (type, ratio) ->
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 42.dp, vertical = 22.dp),
            ) {
                Text(
                    text = type,
                    style = SemobanTypography.body2Medium,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "$ratio%",
                    style = SemobanTypography.body2Regular,
                )
            }
            if (index < ingredients.lastIndex) {
                HorizontalDivider(thickness = 1.dp, color = Gray3)
            }
        }
    }
}

@Composable
private fun KcalBar(
    kcal: Double,
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
        Text(
            text = kcal.toString(),
            style = SemobanTypography.body2Medium,
            modifier =
                Modifier
                    .padding(horizontal = 8.dp)
                    .border(1.dp, Gray3, RoundedCornerShape(5.dp))
                    .background(White, RoundedCornerShape(5.dp))
                    .padding(horizontal = 7.dp, vertical = 6.dp),
        )
        Text(
            text = "kcal/kg",
            style = SemobanTypography.body2Regular,
        )
    }
}

@Composable
private fun RecommendIntakeBar(
    recommendIntake: Long,
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

@Preview(showBackground = true)
@Composable
private fun FeedInfoScreenPreview() {
    SemobanTheme {
        FeedInfoScreen(
            feedInfo =
                FeedDetailGetResponse(
                    brand = "로얄캐닌",
                    feedName = "미니 인도어 어덜트",
                    protein = 30.0,
                    fat = 15.0,
                    crudeAsh = 8.0,
                    moisture = 10.0,
                    etc = 37.0,
                    kcal = 3800.0,
                    recommendIntake = 120L,
                    imageURL = null,
                    startDate = "2024-01-01",
                    endDate = null,
                ),
            onBackClick = {},
            onEditClick = {},
            onDeleteClick = {},
        )
    }
}
