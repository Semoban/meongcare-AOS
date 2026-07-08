package com.project.meongcare.supplement.view

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.meongcare.R
import com.project.meongcare.designsystem.theme.Black
import com.project.meongcare.designsystem.theme.Gray3
import com.project.meongcare.designsystem.theme.Gray5
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.White
import com.project.meongcare.supplement.model.entities.SupplementDog

@Composable
fun SupplementRoutineEditScreen(
    supplements: List<SupplementDog>,
    checkedIds: List<Int>,
    onBackClick: () -> Unit,
    onToggleAllClick: () -> Unit,
    onToggleClick: (Int) -> Unit,
    onAlarmClick: (SupplementDog) -> Unit,
    onItemClick: (Int) -> Unit,
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
            SupplementTopBar(onBack = onBackClick)
            if (supplements.isEmpty()) {
                SupplementNoData(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1f),
                )
            } else {
                Column(
                    modifier =
                        Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                ) {
                    Row(
                        modifier =
                            Modifier
                                .padding(bottom = 16.dp)
                                .clickable { onToggleAllClick() },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CheckToggleIcon(
                            checked = checkedIds.isNotEmpty() && checkedIds.size == supplements.size,
                        )
                        Text(
                            text = "전체 삭제",
                            style = SemobanTypography.body1Medium,
                            color = Gray5,
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                    supplements.forEach { supplement ->
                        SupplementRoutineEditItem(
                            supplement = supplement,
                            checked = checkedIds.contains(supplement.supplementsId),
                            onToggleClick = { onToggleClick(supplement.supplementsId) },
                            onAlarmClick = { onAlarmClick(supplement) },
                            onItemClick = { onItemClick(supplement.supplementsId) },
                            modifier = Modifier.padding(bottom = 16.dp),
                        )
                    }
                }
            }
            CancelCompleteButtons(
                completeText = "삭제",
                onCancel = onBackClick,
                onComplete = { showDeleteDialog = true },
                modifier = Modifier.padding(start = 27.dp, end = 27.dp, bottom = 32.dp),
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
private fun SupplementRoutineEditItem(
    supplement: SupplementDog,
    checked: Boolean,
    onToggleClick: () -> Unit,
    onAlarmClick: () -> Unit,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CheckToggleIcon(
            checked = checked,
            modifier = Modifier.clickable { onToggleClick() },
        )
        Row(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
                    .border(1.dp, Gray3, RoundedCornerShape(10.dp))
                    .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = supplement.name,
                style = SemobanTypography.body2Medium,
                color = Black,
                modifier =
                    Modifier
                        .weight(1f)
                        .clickable { onItemClick() }
                        .padding(vertical = 22.dp),
            )
            Image(
                painter =
                    painterResource(
                        if (supplement.pushAgreement) {
                            R.drawable.all_notification_18dp
                        } else {
                            R.drawable.all_notification_inactivate_18dp
                        },
                    ),
                contentDescription = if (supplement.pushAgreement) "알림 켜짐" else "알림 꺼짐",
                modifier =
                    Modifier
                        .padding(start = 12.dp)
                        .clickable { onAlarmClick() },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SupplementRoutineEditScreenPreview() {
    SemobanTheme {
        SupplementRoutineEditScreen(
            supplements =
                listOf(
                    SupplementDog(1, "오메가3", true),
                    SupplementDog(2, "유산균", false),
                    SupplementDog(3, "관절영양제", true),
                ),
            checkedIds = listOf(2),
            onBackClick = {},
            onToggleAllClick = {},
            onToggleClick = {},
            onAlarmClick = {},
            onItemClick = {},
            onDeleteConfirm = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SupplementRoutineEditScreenEmptyPreview() {
    SemobanTheme {
        SupplementRoutineEditScreen(
            supplements = emptyList(),
            checkedIds = emptyList(),
            onBackClick = {},
            onToggleAllClick = {},
            onToggleClick = {},
            onAlarmClick = {},
            onItemClick = {},
            onDeleteConfirm = {},
        )
    }
}
