package com.project.meongcare.supplement.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.meongcare.R
import com.project.meongcare.designsystem.component.DeleteDialogOverlay
import com.project.meongcare.designsystem.theme.Black
import com.project.meongcare.designsystem.theme.Gray2
import com.project.meongcare.designsystem.theme.Gray3
import com.project.meongcare.designsystem.theme.Gray4
import com.project.meongcare.designsystem.theme.Gray5
import com.project.meongcare.designsystem.theme.Main4
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.White
import com.project.meongcare.supplement.model.entities.DetailSupplement
import com.project.meongcare.supplement.model.entities.IntakeInfo

@Composable
fun SupplementInfoScreen(
    detail: DetailSupplement?,
    isRoutineActive: Boolean,
    onBackClick: () -> Unit,
    onRoutineToggle: () -> Unit,
    onDeleteConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(White),
        ) {
            SupplementTopBar(
                onBack = onBackClick,
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            painter = painterResource(R.drawable.all_delete),
                            contentDescription = stringResource(R.string.all_delete),
                        )
                    }
                },
            )
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
            ) {
                SupplementImageCard(imageModel = detail?.imageUrl?.takeIf { it.isNotBlank() })
                Text(
                    text = stringResource(R.string.supplement_brand_label),
                    style = SemobanTypography.body1SemiBold,
                    modifier = Modifier.padding(top = 31.dp),
                )
                SupplementInfoValueBox(
                    value = detail?.brand.orEmpty(),
                    modifier = Modifier.padding(top = 8.dp),
                )
                Text(
                    text = stringResource(R.string.supplement_name_label),
                    style = SemobanTypography.body1SemiBold,
                    modifier = Modifier.padding(top = 23.dp),
                )
                SupplementInfoValueBox(
                    value = detail?.name.orEmpty(),
                    modifier = Modifier.padding(top = 8.dp),
                )
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 28.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.supplement_intake_cycle_label),
                        style = SemobanTypography.body1SemiBold,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = stringResource(R.string.supplement_cycle_days_format, detail?.intakeCycle ?: 0),
                        style = SemobanTypography.body2Regular,
                        color = Gray5,
                    )
                }
                SupplementSectionDivider()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.supplement_intake_unit_label),
                        style = SemobanTypography.body1SemiBold,
                        modifier = Modifier.weight(1f),
                    )
                    SupplementUnitSelector(selectedUnit = detail?.intakeUnit)
                }
                SupplementSectionDivider()
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = stringResource(R.string.supplement_intake_time_label), style = SemobanTypography.body1SemiBold)
                    SupplementIntakeCountBadge(
                        count = detail?.intakeInfos?.size ?: 0,
                        modifier = Modifier.padding(start = 9.dp),
                    )
                }
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    detail?.intakeInfos?.forEach { intakeInfo ->
                        SupplementIntakeTimeItem(
                            intakeInfo = intakeInfo,
                            intakeUnit = detail.intakeUnit,
                            modifier = Modifier.padding(bottom = 8.dp),
                        )
                    }
                }
            }
            SupplementRoutineButton(
                isRoutineActive = isRoutineActive,
                onClick = onRoutineToggle,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 36.dp, end = 36.dp, bottom = 19.dp),
            )
        }
        if (showDeleteDialog) {
            DeleteDialogOverlay(
                onCancel = { showDeleteDialog = false },
                onDelete = onDeleteConfirm,
            )
        }
    }
}

@Composable
private fun SupplementInfoValueBox(
    value: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(Gray2, RoundedCornerShape(5.dp))
                .padding(horizontal = 16.dp, vertical = 11.dp),
    ) {
        Text(
            text = value,
            style = SemobanTypography.body1Regular,
            color = Black,
        )
    }
}

@Composable
private fun SupplementRoutineButton(
    isRoutineActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var buttonModifier =
        modifier.height(45.dp)
    buttonModifier =
        if (isRoutineActive) {
            buttonModifier
                .background(White, RoundedCornerShape(5.dp))
                .border(1.dp, Gray3, RoundedCornerShape(5.dp))
        } else {
            buttonModifier.background(Main4, RoundedCornerShape(5.dp))
        }
    Box(
        modifier = buttonModifier.clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text =
                stringResource(
                    if (isRoutineActive) R.string.supplement_routine_stop else R.string.supplement_routine_start,
                ),
            style = SemobanTypography.bottom1SemiBold,
            color = if (isRoutineActive) Gray4 else White,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SupplementInfoScreenPreview() {
    SemobanTheme {
        SupplementInfoScreen(
            detail =
                DetailSupplement(
                    supplementsId = 1,
                    imageUrl = "",
                    isActive = true,
                    brand = "네이처스",
                    name = "오메가3",
                    intakeCycle = 2,
                    intakeUnit = "정",
                    intakeInfos =
                        listOf(
                            IntakeInfo("08:00:00", 1),
                            IntakeInfo("20:30:00", 2),
                        ),
                ),
            isRoutineActive = true,
            onBackClick = {},
            onRoutineToggle = {},
            onDeleteConfirm = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SupplementInfoScreenInactivePreview() {
    SemobanTheme {
        SupplementInfoScreen(
            detail =
                DetailSupplement(
                    supplementsId = 1,
                    imageUrl = "",
                    isActive = false,
                    brand = "네이처스",
                    name = "오메가3",
                    intakeCycle = 2,
                    intakeUnit = "mg",
                    intakeInfos = listOf(IntakeInfo("08:00:00", 1)),
                ),
            isRoutineActive = false,
            onBackClick = {},
            onRoutineToggle = {},
            onDeleteConfirm = {},
        )
    }
}
