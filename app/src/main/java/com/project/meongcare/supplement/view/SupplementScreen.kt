package com.project.meongcare.supplement.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.meongcare.R
import com.project.meongcare.designsystem.theme.Black
import com.project.meongcare.designsystem.theme.Gray2
import com.project.meongcare.designsystem.theme.Gray3
import com.project.meongcare.designsystem.theme.Gray5
import com.project.meongcare.designsystem.theme.Main3
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.White
import com.project.meongcare.supplement.model.entities.Supplement
import com.project.meongcare.supplement.utils.SupplementUtils.Companion.convertDateToTime
import java.util.Locale

@Composable
fun SupplementScreen(
    dogName: String?,
    supplements: List<Supplement>,
    showCheck: Boolean,
    onAddClick: () -> Unit,
    onEditClick: () -> Unit,
    onItemClick: (Int) -> Unit,
    onCheckClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val checkedCount = supplements.count { it.intakeStatus }
    val percentage =
        if (supplements.isEmpty()) 0.0 else checkedCount.toDouble() / supplements.size * 100

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Gray2)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
    ) {
        SupplementProgressCard(
            percentage = percentage,
            remainingCount = supplements.size - checkedCount,
        )
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.supplement_title_format, dogName.orEmpty()),
                style = SemobanTypography.title3SemiBold,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(R.string.all_add),
                style = SemobanTypography.body1Regular,
                color = Gray5,
                modifier = Modifier.clickable { onAddClick() },
            )
            if (supplements.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.all_edit),
                    style = SemobanTypography.body1Regular,
                    color = Gray5,
                    modifier =
                        Modifier
                            .padding(start = 12.dp)
                            .clickable { onEditClick() },
                )
            }
        }
        if (supplements.isEmpty()) {
            SupplementNoData(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 100.dp, bottom = 82.dp),
            )
        } else {
            supplements
                .groupBy { convertDateToTime(it.intakeTime) }
                .forEach { (time, timeSupplements) ->
                    SupplementTimeGroup(
                        time = time,
                        supplements = timeSupplements,
                        showCheck = showCheck,
                        onItemClick = onItemClick,
                        onCheckClick = onCheckClick,
                        modifier = Modifier.padding(top = 24.dp),
                    )
                }
        }
    }
}

@Composable
private fun SupplementProgressCard(
    percentage: Double,
    remainingCount: Int,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = White,
        shape = RoundedCornerShape(10.dp),
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.supplement_completion_rate),
                    style = SemobanTypography.title3SemiBold,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = String.format(Locale.getDefault(), "%.1f", percentage),
                    style = SemobanTypography.title3SemiBold,
                )
                Text(
                    text = "%",
                    style = SemobanTypography.title3SemiBold,
                    color = Gray5,
                    modifier = Modifier.padding(start = 2.dp),
                )
            }
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .height(17.dp)
                        .background(Gray2, RoundedCornerShape(8.5.dp)),
            ) {
                val fraction = (percentage / 100).toFloat()
                if (fraction > 0f) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth(fraction)
                                .fillMaxHeight()
                                .background(Main3, RoundedCornerShape(8.5.dp)),
                    )
                }
            }
            Text(
                text = stringResource(R.string.supplement_remaining_format, remainingCount),
                style = SemobanTypography.body3Regular,
                color = Gray5,
                modifier = Modifier.align(Alignment.End),
            )
        }
    }
}

@Composable
private fun SupplementTimeGroup(
    time: String,
    supplements: List<Supplement>,
    showCheck: Boolean,
    onItemClick: (Int) -> Unit,
    onCheckClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = time,
            style = SemobanTypography.body1Medium,
            color = Gray5,
        )
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier =
                    Modifier
                        .padding(top = 2.dp)
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(Gray3),
            )
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(start = 7.dp),
            ) {
                supplements.forEach { supplement ->
                    SupplementItemCard(
                        supplement = supplement,
                        showCheck = showCheck,
                        onClick = { onItemClick(supplement.supplementsId) },
                        onCheckClick = { onCheckClick(supplement.supplementsRecordId) },
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun SupplementItemCard(
    supplement: Supplement,
    showCheck: Boolean,
    onClick: () -> Unit,
    onCheckClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = White,
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier.padding(start = 17.dp, top = 20.dp, bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .clickable { onClick() },
            ) {
                Text(
                    text = supplement.name,
                    style = SemobanTypography.body2Medium,
                    color = Black,
                )
                Text(
                    text = "${supplement.intakeCount}${supplement.intakeUnit}",
                    style = SemobanTypography.body3Light,
                )
            }
            if (showCheck) {
                CheckToggleIcon(
                    checked = supplement.intakeStatus,
                    modifier =
                        Modifier
                            .padding(horizontal = 17.dp)
                            .clickable { onCheckClick() },
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SupplementScreenPreview() {
    SemobanTheme {
        SupplementScreen(
            dogName = "몽실이",
            supplements =
                listOf(
                    Supplement(1, 1, "오메가3", "08:00:00", 1, "정", true),
                    Supplement(2, 2, "유산균", "08:00:00", 2, "스쿱", false),
                    Supplement(3, 3, "관절영양제", "20:30:00", 1, "정", false),
                ),
            showCheck = true,
            onAddClick = {},
            onEditClick = {},
            onItemClick = {},
            onCheckClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SupplementScreenEmptyPreview() {
    SemobanTheme {
        SupplementScreen(
            dogName = "몽실이",
            supplements = emptyList(),
            showCheck = true,
            onAddClick = {},
            onEditClick = {},
            onItemClick = {},
            onCheckClick = {},
        )
    }
}
