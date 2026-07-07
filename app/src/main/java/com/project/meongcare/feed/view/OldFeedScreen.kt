package com.project.meongcare.feed.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.meongcare.R
import com.project.meongcare.designsystem.theme.Gray2
import com.project.meongcare.designsystem.theme.Gray4
import com.project.meongcare.designsystem.theme.Gray5
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.White
import com.project.meongcare.feed.model.entities.FeedRecord

@Composable
fun OldFeedScreen(
    feedRecords: List<FeedRecord>,
    onBackClick: () -> Unit,
    onItemClick: (feedId: Long, feedRecordId: Long) -> Unit,
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
            Text(
                text = "전에 먹던 사료 리스트",
                style = SemobanTypography.title3SemiBold,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
        if (feedRecords.isEmpty()) {
            OldFeedEmptyContent(modifier = Modifier.weight(1f))
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(feedRecords, key = { it.feedRecordId }) { feedRecord ->
                    OldFeedItem(
                        feedRecord = feedRecord,
                        onClick = { onItemClick(feedRecord.feedId, feedRecord.feedRecordId) },
                    )
                }
            }
        }
    }
}

@Composable
private fun OldFeedEmptyContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.feed_bowl_light),
                contentDescription = null,
            )
            Text(
                text = "아직 사료가 기록되지 않았습니다",
                style = SemobanTypography.body1Medium,
                color = Gray4,
                modifier = Modifier.padding(top = 24.dp),
            )
        }
    }
}

@Composable
private fun OldFeedItem(
    feedRecord: FeedRecord,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val period =
        if (feedRecord.endDate == null) {
            "${feedRecord.startDate}~ 모름"
        } else {
            "${feedRecord.startDate}~ ${feedRecord.endDate}"
        }

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable { onClick() },
    ) {
        Text(
            text = period,
            style = SemobanTypography.body1SemiBold,
            color = Gray5,
            modifier = Modifier.padding(start = 32.dp, top = 16.dp),
        )
        HorizontalDivider(
            thickness = 1.dp,
            color = Gray2,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            val imageModifier =
                Modifier
                    .padding(start = 32.dp, bottom = 16.dp)
                    .size(80.dp)
                    .clip(RoundedCornerShape(10.dp))
            if (feedRecord.imageURL.isNullOrEmpty()) {
                Image(
                    painter = painterResource(R.drawable.feed_item_default_image),
                    contentDescription = null,
                    modifier = imageModifier,
                )
            } else {
                FeedGlideImage(
                    model = feedRecord.imageURL,
                    modifier = imageModifier,
                )
            }
            Column(modifier = Modifier.padding(start = 12.dp, bottom = 16.dp)) {
                Text(
                    text = feedRecord.brand,
                    style = SemobanTypography.body2Regular,
                    color = Gray4,
                )
                Text(
                    text = feedRecord.feedName,
                    style = SemobanTypography.body1Medium,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OldFeedScreenPreview() {
    SemobanTheme {
        OldFeedScreen(
            feedRecords =
                listOf(
                    FeedRecord(
                        feedRecordId = 1L,
                        feedId = 1L,
                        brand = "로얄캐닌",
                        feedName = "미니 인도어 어덜트",
                        startDate = "2024-01-01",
                        endDate = "2024-02-01",
                        imageURL = null,
                    ),
                ),
            onBackClick = {},
            onItemClick = { _, _ -> },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OldFeedScreenEmptyPreview() {
    SemobanTheme {
        OldFeedScreen(
            feedRecords = emptyList(),
            onBackClick = {},
            onItemClick = { _, _ -> },
        )
    }
}
