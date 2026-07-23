package com.project.meongcare.designsystem.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@Composable
private fun ColorPalettePreview() {
    SemobanTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ColorRow("Main1~4", listOf(Main1, Main2, Main3, Main4))
            ColorRow("Sub1~4", listOf(Sub1, Sub2, Sub3, Sub4))
            ColorRow("Sub5~8", listOf(Sub5, Sub6, Sub7, Sub8))
            ColorRow("Gray1~5", listOf(Gray1, Gray2, Gray3, Gray4, Gray5))
            ColorRow("Black/30/60", listOf(Black, Black30, Black60))
        }
    }
}

@Composable
private fun ColorRow(
    label: String,
    colors: List<Color>,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        colors.forEach { color ->
            Box(
                modifier =
                    Modifier
                        .size(40.dp)
                        .background(color, RoundedCornerShape(8.dp)),
            )
        }
        Text(text = label, style = SemobanTypography.body3Regular)
    }
}

@Preview(showBackground = true)
@Composable
private fun TypographyPreview() {
    SemobanTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text("Title1 SemiBold 24sp", style = SemobanTypography.title1SemiBold)
            Text("Title2 Bold 20sp", style = SemobanTypography.title2Bold)
            Text("Title2 SemiBold 20sp", style = SemobanTypography.title2SemiBold)
            Text("Title3 SemiBold 18sp", style = SemobanTypography.title3SemiBold)
            Text("Body1 Bold 16sp", style = SemobanTypography.body1Bold)
            Text("Body1 SemiBold 16sp", style = SemobanTypography.body1SemiBold)
            Text("Body1 Medium 16sp", style = SemobanTypography.body1Medium)
            Text("Body1 Regular 16sp", style = SemobanTypography.body1Regular)
            Text("Body2 Bold 14sp", style = SemobanTypography.body2Bold)
            Text("Body2 Medium 14sp", style = SemobanTypography.body2Medium)
            Text("Body2 Regular 14sp", style = SemobanTypography.body2Regular)
            Text("Body2 Light 14sp", style = SemobanTypography.body2Light)
            Text("Body3 Medium 12sp", style = SemobanTypography.body3Medium)
            Text("Body3 Regular 12sp", style = SemobanTypography.body3Regular)
            Text("Body3 Light 12sp", style = SemobanTypography.body3Light)
        }
    }
}
