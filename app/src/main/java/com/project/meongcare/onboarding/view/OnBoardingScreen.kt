package com.project.meongcare.onboarding.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.meongcare.R
import com.project.meongcare.designsystem.theme.Black
import com.project.meongcare.designsystem.theme.Gray3
import com.project.meongcare.designsystem.theme.Gray4
import com.project.meongcare.designsystem.theme.Main3
import com.project.meongcare.designsystem.theme.Main4
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.White

private data class OnBoardingPage(
    val title: String,
    val imageRes: Int,
)

private val onBoardingPages =
    listOf(
        OnBoardingPage("반려견의 건강을\n기록하고 관리해보세요!", R.drawable.onboarding_one),
        OnBoardingPage("기록을 통해\n변화를 알 수 있어요!", R.drawable.onboarding_two),
        OnBoardingPage("그럼 나만의\n세모반을 시작해볼까요?", R.drawable.onboarding_three),
    )

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnBoardingScreen(
    onSkipClick: () -> Unit,
    onStartClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState { onBoardingPages.size }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(White),
    ) {
        Text(
            text = "건너뛰기",
            style = SemobanTypography.body2Regular,
            color = Gray4,
            modifier =
                Modifier
                    .align(Alignment.End)
                    .padding(top = 59.dp, end = 23.dp)
                    .clickable { onSkipClick() },
        )
        HorizontalPager(
            state = pagerState,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp),
        ) { page ->
            OnBoardingPageContent(page = onBoardingPages[page])
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(onBoardingPages.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier =
                        Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (isSelected) 12.dp else 8.dp)
                            .background(
                                if (isSelected) Main3 else Gray3,
                                CircleShape,
                            ),
                )
            }
        }
        Box(
            modifier =
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 32.dp, bottom = 24.dp)
                    .width(288.dp)
                    .height(44.dp),
        ) {
            if (pagerState.currentPage == onBoardingPages.lastIndex) {
                Text(
                    text = "시작하기",
                    style = SemobanTypography.bottom1SemiBold,
                    color = White,
                    textAlign = TextAlign.Center,
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(Main4, RoundedCornerShape(5.dp))
                            .clickable { onStartClick() }
                            .padding(vertical = 10.dp),
                )
            }
        }
    }
}

@Composable
private fun OnBoardingPageContent(
    page: OnBoardingPage,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = page.title,
            style = SemobanTypography.title1SemiBold.copy(fontSize = 24.sp),
            color = Black,
            modifier = Modifier.padding(start = 32.dp),
        )
        Image(
            painter = painterResource(page.imageRes),
            contentDescription = null,
            modifier =
                Modifier
                    .padding(top = 57.dp)
                    .align(Alignment.CenterHorizontally)
                    .width(214.dp)
                    .height(261.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OnBoardingScreenPreview() {
    SemobanTheme {
        OnBoardingScreen(
            onSkipClick = {},
            onStartClick = {},
        )
    }
}
