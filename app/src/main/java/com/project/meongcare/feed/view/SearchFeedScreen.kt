package com.project.meongcare.feed.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.meongcare.R
import com.project.meongcare.designsystem.component.GlideImage
import com.project.meongcare.designsystem.theme.Gray2
import com.project.meongcare.designsystem.theme.Gray3
import com.project.meongcare.designsystem.theme.Gray4
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.White
import com.project.meongcare.feed.model.entities.Feed

@Composable
fun SearchFeedScreen(
    feeds: List<Feed>,
    onBackClick: () -> Unit,
    onSearchChange: (String) -> Unit,
    onFeedClick: (Long) -> Unit,
    onDirectInputClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var searchText by rememberSaveable { mutableStateOf("") }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(White),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 7.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(R.drawable.all_arrow_back_18dp),
                        contentDescription = "뒤로가기",
                    )
                }
                SearchFeedTextField(
                    value = searchText,
                    onValueChange = { input ->
                        searchText = input
                        onSearchChange(input)
                    },
                    modifier = Modifier.weight(1f),
                )
            }
            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(start = 44.dp, top = 8.dp, end = 16.dp),
            ) {
                items(feeds, key = { it.feedId }) { feed ->
                    SearchFeedItem(
                        feed = feed,
                        onClick = { onFeedClick(feed.feedId) },
                        modifier = Modifier.padding(bottom = 15.dp),
                    )
                }
            }
        }
        DirectInputButton(
            onClick = onDirectInputClick,
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp),
        )
    }
}

@Composable
private fun SearchFeedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = SemobanTypography.body1Medium,
        singleLine = true,
        modifier = modifier,
        decorationBox = { innerTextField ->
            Row(
                modifier =
                    Modifier
                        .background(Gray2, RoundedCornerShape(10.dp))
                        .padding(horizontal = 16.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(
                            text = "검색어를 입력해주세요",
                            style = SemobanTypography.body1Medium,
                            color = Gray4,
                        )
                    }
                    innerTextField()
                }
                Image(
                    painter = painterResource(R.drawable.all_search_18dp),
                    contentDescription = null,
                )
            }
        },
    )
}

@Composable
private fun SearchFeedItem(
    feed: Feed,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .border(1.dp, Gray3, RoundedCornerShape(10.dp))
                .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val imageModifier =
            Modifier
                .padding(start = 24.dp, top = 15.dp, bottom = 15.dp)
                .size(70.dp)
                .clip(RoundedCornerShape(10.dp))
        if (feed.imageURL.isNullOrEmpty()) {
            Image(
                painter = painterResource(R.drawable.feed_item_default_image),
                contentDescription = null,
                modifier = imageModifier,
            )
        } else {
            GlideImage(
                model = feed.imageURL,
                modifier = imageModifier,
            )
        }
        Column(modifier = Modifier.padding(start = 24.dp)) {
            Text(
                text = feed.brandName,
                style = SemobanTypography.body2Regular,
                color = Gray4,
            )
            Text(
                text = feed.feedName,
                style = SemobanTypography.body1Medium,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun DirectInputButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = White,
        shape = RoundedCornerShape(28.dp),
        shadowElevation = 6.dp,
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.all_edit),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = "직접 입력하기",
                style = SemobanTypography.title3SemiBold,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchFeedScreenPreview() {
    SemobanTheme {
        SearchFeedScreen(
            feeds =
                listOf(
                    Feed(1L, "로얄캐닌", "미니 인도어 어덜트", null),
                    Feed(2L, "네츄럴발란스", "L.I.D. 스몰브리드", null),
                ),
            onBackClick = {},
            onSearchChange = {},
            onFeedClick = {},
            onDirectInputClick = {},
        )
    }
}
