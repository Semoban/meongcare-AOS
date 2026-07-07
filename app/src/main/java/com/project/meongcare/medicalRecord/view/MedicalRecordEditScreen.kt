package com.project.meongcare.medicalRecord.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.meongcare.R
import com.project.meongcare.designsystem.theme.Gray2
import com.project.meongcare.designsystem.theme.Gray5
import com.project.meongcare.designsystem.theme.Main4
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.White
import com.project.meongcare.medicalRecord.model.entities.MedicalRecord
import com.project.meongcare.medicalRecord.model.utils.MedicalRecordDateUtils

@Composable
fun MedicalRecordEditScreen(
    dogName: String?,
    records: List<MedicalRecord>,
    checkedIds: List<Int>,
    onBackClick: () -> Unit,
    onToggleAll: () -> Unit,
    onToggleItem: (Int) -> Unit,
    onCancelClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

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
                    .padding(top = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(R.drawable.all_arrow_back_18dp),
                    contentDescription = "뒤로가기",
                )
            }
            Text(
                text = "${dogName.orEmpty()}님의 진료 기록",
                style = SemobanTypography.title3SemiBold,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
        Row(
            modifier =
                Modifier
                    .padding(start = 24.dp, top = 24.dp)
                    .clickable { onToggleAll() },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MedicalRecordCheckIcon(
                checked = checkedIds.isNotEmpty() && checkedIds.size == records.size,
            )
            Text(
                text = stringResource(R.string.medicalrecord_select_all),
                style = SemobanTypography.body1Medium,
                color = Gray5,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(start = 24.dp, top = 24.dp, end = 24.dp),
        ) {
            items(records, key = { it.medicalRecordId }) { record ->
                MedicalRecordEditItem(
                    record = record,
                    checked = checkedIds.contains(record.medicalRecordId.toInt()),
                    onClick = { onToggleItem(record.medicalRecordId.toInt()) },
                    modifier = Modifier.padding(bottom = 15.dp),
                )
            }
        }
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(start = 27.dp, end = 27.dp, top = 10.dp, bottom = 32.dp),
        ) {
            Text(
                text = stringResource(R.string.medicalrecord_cancel),
                style = SemobanTypography.bottom1SemiBold,
                color = Gray5,
                textAlign = TextAlign.Center,
                modifier =
                    Modifier
                        .weight(1f)
                        .background(Gray2, RoundedCornerShape(5.dp))
                        .clickable { onCancelClick() }
                        .padding(vertical = 12.dp),
            )
            Text(
                text = "삭제",
                style = SemobanTypography.bottom1SemiBold,
                color = White,
                textAlign = TextAlign.Center,
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                        .background(Main4, RoundedCornerShape(5.dp))
                        .clickable {
                            if (checkedIds.isNotEmpty()) {
                                showDeleteDialog = true
                            }
                        }
                        .padding(vertical = 12.dp),
            )
        }
    }

    if (showDeleteDialog) {
        MedicalRecordDeleteDialog(
            onCancel = { showDeleteDialog = false },
            onConfirm = {
                showDeleteDialog = false
                onDeleteClick()
            },
        )
    }
}

@Composable
internal fun MedicalRecordCheckIcon(
    checked: Boolean,
    modifier: Modifier = Modifier,
) {
    Image(
        painter =
            painterResource(
                if (checked) R.drawable.all_check_24dp else R.drawable.all_un_check_16dp,
            ),
        contentDescription = null,
        modifier = modifier.size(20.dp),
    )
}

@Composable
private fun MedicalRecordEditItem(
    record: MedicalRecord,
    checked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MedicalRecordCheckIcon(checked = checked)
        Row(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
                    .height(52.dp)
                    .background(Gray2, RoundedCornerShape(5.dp))
                    .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = MedicalRecordDateUtils.showFormattedTime(record.dateTime),
                style = SemobanTypography.body1Medium,
                modifier = Modifier.weight(1f),
            )
            Image(
                painter = painterResource(R.drawable.all_arrow_navigate_next),
                contentDescription = null,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MedicalRecordEditScreenPreview() {
    SemobanTheme {
        MedicalRecordEditScreen(
            dogName = "몽실이",
            records =
                listOf(
                    MedicalRecord(1L, "2024-01-01T08:00:00"),
                    MedicalRecord(2L, "2024-01-01T14:30:00"),
                ),
            checkedIds = listOf(1),
            onBackClick = {},
            onToggleAll = {},
            onToggleItem = {},
            onCancelClick = {},
            onDeleteClick = {},
        )
    }
}
