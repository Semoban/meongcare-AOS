package com.project.meongcare.excreta.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.meongcare.R
import com.project.meongcare.designsystem.theme.Gray4
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.White
import com.project.meongcare.excreta.model.entities.Excreta
import com.project.meongcare.excreta.model.entities.ExcretaRecord
import com.project.meongcare.excreta.utils.ExcretaDateTimeUtils.convertToTimeFormat

@Composable
fun ExcretaRecordEditScreen(
    dogName: String?,
    excretaRecords: List<ExcretaRecord>,
    checkedIds: List<Long>,
    onBack: () -> Unit,
    onToggleAll: () -> Unit,
    onToggleItem: (Long) -> Unit,
    onCancel: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val allChecked = checkedIds.isNotEmpty() && checkedIds.size == excretaRecords.size

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(White),
    ) {
        ExcretaTopBar(
            title = "${dogName.orEmpty()}님의 기록",
            onBack = onBack,
        )
        Row(
            modifier =
                Modifier
                    .padding(start = 24.dp, top = 8.dp)
                    .clickable { onToggleAll() },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter =
                    painterResource(
                        if (allChecked) R.drawable.all_check_16dp else R.drawable.all_circle_line_16dp,
                    ),
                contentDescription = if (allChecked) "전체 선택됨" else "전체 선택 안 됨",
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = "전체 선택",
                style = SemobanTypography.body1Medium,
                color = Gray4,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(start = 24.dp, top = 16.dp, end = 17.dp, bottom = 16.dp),
        ) {
            items(
                items = excretaRecords,
                key = { it.excretaId },
            ) { record ->
                ExcretaRecordEditItem(
                    record = record,
                    checked = checkedIds.contains(record.excretaId),
                    onClick = { onToggleItem(record.excretaId) },
                )
            }
        }
        ExcretaCancelDeleteButtons(
            onCancel = onCancel,
            onDelete = onDelete,
            modifier = Modifier.padding(start = 22.dp, end = 22.dp, bottom = 32.dp),
        )
    }
}

@Composable
private fun ExcretaRecordEditItem(
    record: ExcretaRecord,
    checked: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ExcretaCheckIcon(
            checked = checked,
            size = 16.dp,
        )
        Box(modifier = Modifier.weight(1f)) {
            ExcretaRecordItem(
                typeText = Excreta.valueOf(record.excretaType).type,
                timeText = convertToTimeFormat(record.time),
                modifier = Modifier.padding(start = 7.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ExcretaRecordEditScreenPreview() {
    SemobanTheme {
        ExcretaRecordEditScreen(
            dogName = "몽실이",
            excretaRecords =
                listOf(
                    ExcretaRecord(1, "2024-01-01T08:00:00", "FECES"),
                    ExcretaRecord(2, "2024-01-01T10:30:00", "URINE"),
                ),
            checkedIds = listOf(1L),
            onBack = {},
            onToggleAll = {},
            onToggleItem = {},
            onCancel = {},
            onDelete = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ExcretaRecordEditScreenEmptyPreview() {
    SemobanTheme {
        ExcretaRecordEditScreen(
            dogName = "몽실이",
            excretaRecords = emptyList(),
            checkedIds = emptyList(),
            onBack = {},
            onToggleAll = {},
            onToggleItem = {},
            onCancel = {},
            onDelete = {},
        )
    }
}
