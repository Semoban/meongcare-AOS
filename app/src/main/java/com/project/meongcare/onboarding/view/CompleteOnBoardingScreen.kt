package com.project.meongcare.onboarding.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.meongcare.R
import com.project.meongcare.designsystem.theme.Black
import com.project.meongcare.designsystem.theme.Gray5
import com.project.meongcare.designsystem.theme.Main4
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.White

@Composable
fun CompleteOnBoardingScreen(
    onStartClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(White),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(R.drawable.onboarding_complete),
            contentDescription = null,
            modifier = Modifier.padding(top = 134.dp),
        )
        Text(
            text = "입력을 완료했어요!",
            style = SemobanTypography.title2SemiBold,
            color = Black,
            modifier = Modifier.padding(top = 32.dp),
        )
        Text(
            text = "이제 멍케어와 함께 같이 시작해볼까요?",
            style = SemobanTypography.body2Regular,
            color = Gray5,
            modifier = Modifier.padding(top = 8.dp),
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "시작하기",
            style = SemobanTypography.bottom1SemiBold,
            color = White,
            textAlign = TextAlign.Center,
            modifier =
                Modifier
                    .padding(bottom = 46.dp)
                    .width(288.dp)
                    .height(44.dp)
                    .background(Main4, RoundedCornerShape(5.dp))
                    .clickable { onStartClick() }
                    .padding(vertical = 10.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CompleteOnBoardingScreenPreview() {
    SemobanTheme {
        CompleteOnBoardingScreen(onStartClick = {})
    }
}
