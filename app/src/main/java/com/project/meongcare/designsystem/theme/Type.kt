package com.project.meongcare.designsystem.theme

import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.project.meongcare.R

val Pretendard =
    FontFamily(
        Font(R.font.pretendard_thin, FontWeight.Thin),
        Font(R.font.pretendard_extra_light, FontWeight.ExtraLight),
        Font(R.font.pretendard_light, FontWeight.Light),
        Font(R.font.pretendard_regular, FontWeight.Normal),
        Font(R.font.pretendard_medium, FontWeight.Medium),
        Font(R.font.pretendard_semi_bold, FontWeight.SemiBold),
        Font(R.font.pretendard_bold, FontWeight.Bold),
        Font(R.font.pretendard_extra_bold, FontWeight.ExtraBold),
        Font(R.font.pretendard_black, FontWeight.Black),
    )

// res/values/typography.xml의 Typography.* 스타일을 1:1 매핑
object SemobanTypography {
    private val base =
        TextStyle(
            fontFamily = Pretendard,
            color = Black,
            letterSpacing = (-0.02).em,
            platformStyle = PlatformTextStyle(includeFontPadding = false),
        )

    val title1SemiBold = base.copy(fontSize = 24.sp, lineHeight = 32.sp, fontWeight = FontWeight.SemiBold)

    val title2Bold = base.copy(fontSize = 20.sp, lineHeight = 28.sp, fontWeight = FontWeight.Bold)
    val title2SemiBold = base.copy(fontSize = 20.sp, lineHeight = 28.sp, fontWeight = FontWeight.SemiBold)

    val title3SemiBold = base.copy(fontSize = 18.sp, lineHeight = 26.sp, fontWeight = FontWeight.SemiBold)

    val bottom1SemiBold = base.copy(fontSize = 18.sp, lineHeight = 26.sp, fontWeight = FontWeight.SemiBold)
    val bottom2SemiBold = base.copy(fontSize = 16.sp, lineHeight = 24.sp, fontWeight = FontWeight.SemiBold)

    val body1Bold = base.copy(fontSize = 16.sp, lineHeight = 24.sp, fontWeight = FontWeight.Bold)
    val body1SemiBold = base.copy(fontSize = 16.sp, lineHeight = 24.sp, fontWeight = FontWeight.SemiBold)
    val body1Medium = base.copy(fontSize = 16.sp, lineHeight = 24.sp, fontWeight = FontWeight.Medium)
    val body1Regular = base.copy(fontSize = 16.sp, lineHeight = 24.sp, fontWeight = FontWeight.Normal)

    val body2Bold = base.copy(fontSize = 14.sp, lineHeight = 22.sp, fontWeight = FontWeight.Bold)
    val body2Medium = base.copy(fontSize = 14.sp, lineHeight = 22.sp, fontWeight = FontWeight.Medium)
    val body2Regular = base.copy(fontSize = 14.sp, lineHeight = 22.sp, fontWeight = FontWeight.Normal)
    val body2Light = base.copy(fontSize = 14.sp, lineHeight = 22.sp, fontWeight = FontWeight.Light)

    val body3Medium = base.copy(fontSize = 12.sp, lineHeight = 20.sp, fontWeight = FontWeight.Medium)
    val body3Regular = base.copy(fontSize = 12.sp, lineHeight = 20.sp, fontWeight = FontWeight.Normal)
    val body3Light = base.copy(fontSize = 12.sp, lineHeight = 20.sp, fontWeight = FontWeight.Light)
}
