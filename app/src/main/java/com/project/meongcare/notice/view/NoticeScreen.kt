package com.project.meongcare.notice.view

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.meongcare.R
import com.project.meongcare.designsystem.theme.Black
import com.project.meongcare.designsystem.theme.Gray2
import com.project.meongcare.designsystem.theme.Gray4
import com.project.meongcare.designsystem.theme.Main1
import com.project.meongcare.designsystem.theme.Main3
import com.project.meongcare.designsystem.theme.Main4
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.White
import com.project.meongcare.notice.model.entities.Notice
import com.project.meongcare.notice.utils.NoticeDateTimeUtils
import kotlinx.coroutines.launch

private val noticeTabTitles = listOf("공지사항", "이벤트")

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoticeScreen(
    notices: List<Notice>,
    events: List<Notice>,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState { noticeTabTitles.size }
    val coroutineScope = rememberCoroutineScope()

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
                    .height(50.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.all_arrow_back_18dp),
                contentDescription = "뒤로가기",
                modifier =
                    Modifier
                        .padding(start = 16.dp)
                        .size(16.dp)
                        .clickable { onBackClick() },
            )
            Text(
                text = "알림",
                style = SemobanTypography.title2SemiBold,
                modifier = Modifier.padding(start = 16.dp),
            )
        }
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = White,
            contentColor = Black,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    height = 2.dp,
                    color = Main3,
                )
            },
        ) {
            noticeTabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                    text = {
                        Text(
                            text = title,
                            style = SemobanTypography.body1Medium,
                            color = Black,
                        )
                    },
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
        ) { page ->
            val noticeList = if (page == 0) notices else events
            if (noticeList.isEmpty()) {
                NoticeEmptyContent()
            } else {
                NoticeListContent(noticeList)
            }
        }
    }
}

@Composable
private fun NoticeListContent(
    notices: List<Notice>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier =
            modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        itemsIndexed(notices, key = { _, notice -> notice.noticeId }) { index, notice ->
            NoticeItem(notice)
            if (index < notices.lastIndex) {
                HorizontalDivider(thickness = 1.dp, color = Gray2)
            }
        }
    }
}

@Composable
private fun NoticeItem(
    notice: Notice,
    modifier: Modifier = Modifier,
) {
    var isExpanded by rememberSaveable(notice.noticeId) { mutableStateOf(false) }

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .animateContentSize(),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(horizontal = 24.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = notice.title,
                        style = SemobanTypography.body1Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    if (NoticeDateTimeUtils.isNewNotice(notice.lastUpdateTime)) {
                        NoticeNewBadge(modifier = Modifier.padding(start = 5.dp))
                    }
                }
                Text(
                    text = NoticeDateTimeUtils.formatLastUpdateTime(notice.lastUpdateTime),
                    style = SemobanTypography.body2Light,
                    color = Gray4,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            Image(
                painter =
                    painterResource(
                        if (isExpanded) R.drawable.notice_toggle_checked else R.drawable.notice_toggle_unchecked,
                    ),
                contentDescription = null,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
        if (isExpanded) {
            Text(
                text = notice.text,
                style = SemobanTypography.body3Regular,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
            )
        }
    }
}

@Composable
private fun NoticeNewBadge(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .size(width = 54.dp, height = 20.dp)
                .background(Main1, RoundedCornerShape(3.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "NEW",
            style = SemobanTypography.body2Medium,
            color = Main4,
        )
    }
}

@Composable
private fun NoticeEmptyContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.all_no_list_icon),
            contentDescription = null,
            modifier = Modifier.size(width = 42.dp, height = 35.dp),
        )
        Text(
            text = "아직 알림이 없습니다!",
            style = SemobanTypography.body1Medium,
            color = Gray4,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NoticeScreenPreview() {
    SemobanTheme {
        NoticeScreen(
            notices =
                listOf(
                    Notice(1, "세모반 서비스 점검 안내", "안녕하세요. 세모반입니다.\n서비스 점검 안내드립니다.", "2024-01-01T10:00:00"),
                    Notice(2, "개인정보 처리방침 개정 안내", "개인정보 처리방침이 개정되었습니다.", "2023-12-01T10:00:00"),
                ),
            events = emptyList(),
            onBackClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NoticeScreenEmptyPreview() {
    SemobanTheme {
        NoticeScreen(
            notices = emptyList(),
            events = emptyList(),
            onBackClick = {},
        )
    }
}
