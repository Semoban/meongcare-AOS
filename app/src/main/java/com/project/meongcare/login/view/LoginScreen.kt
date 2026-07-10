package com.project.meongcare.login.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.meongcare.R
import com.project.meongcare.designsystem.theme.Black
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.White

private val KakaoYellow = Color(0xFFFEE500)
private val NaverGreen = Color(0xFF03C75A)

@Composable
fun LoginScreen(
    onKakaoLoginClick: () -> Unit,
    onNaverLoginClick: () -> Unit,
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
            painter = painterResource(R.drawable.semoban_logo),
            contentDescription = "세모반 로고",
            modifier =
                Modifier
                    .padding(top = 156.dp)
                    .width(185.dp)
                    .height(67.dp),
        )
        Spacer(modifier = Modifier.weight(1f))
        LoginButton(
            text = "카카오로 시작하기",
            iconRes = R.drawable.login_kakao_talk,
            backgroundColor = KakaoYellow,
            textColor = Black,
            onClick = onKakaoLoginClick,
        )
        LoginButton(
            text = "네이버로 시작하기",
            iconRes = R.drawable.login_naver,
            backgroundColor = NaverGreen,
            textColor = White,
            onClick = onNaverLoginClick,
            modifier = Modifier.padding(top = 4.dp, bottom = 140.dp),
        )
    }
}

@Composable
private fun LoginButton(
    text: String,
    iconRes: Int,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .width(280.dp)
                .height(50.dp)
                .background(backgroundColor, RoundedCornerShape(12.dp))
                .clickable { onClick() }
                .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                style = SemobanTypography.body2Medium,
                color = textColor,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    SemobanTheme {
        LoginScreen(
            onKakaoLoginClick = {},
            onNaverLoginClick = {},
        )
    }
}
