package com.project.meongcare.excreta.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.meongcare.R
import com.project.meongcare.designsystem.theme.Gray2
import com.project.meongcare.designsystem.theme.Gray5
import com.project.meongcare.designsystem.theme.Main1
import com.project.meongcare.designsystem.theme.Main4
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.Sub2
import com.project.meongcare.designsystem.theme.Sub3
import com.project.meongcare.designsystem.theme.White
import com.project.meongcare.excreta.model.entities.Excreta
import com.project.meongcare.excreta.model.entities.ExcretaRecord
import com.project.meongcare.excreta.utils.ExcretaDateTimeUtils.convertToTimeFormat

@Composable
fun ExcretaScreen(
    dogName: String?,
    fecesCount: Int,
    urineCount: Int,
    excretaRecords: List<ExcretaRecord>,
    onAddClick: () -> Unit,
    onEditClick: () -> Unit,
    onItemClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Gray2)
                .verticalScroll(rememberScrollState()),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp, top = 46.dp, end = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.excreta_title_format, dogName.orEmpty()),
                style = SemobanTypography.title2Bold,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(R.string.all_add),
                style = SemobanTypography.body1Medium,
                color = Gray5,
                modifier = Modifier.clickable { onAddClick() },
            )
            if (excretaRecords.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.all_edit),
                    style = SemobanTypography.body1Medium,
                    color = Gray5,
                    modifier =
                        Modifier
                            .padding(start = 8.dp)
                            .clickable { onEditClick() },
                )
            }
        }
        Surface(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 13.dp, end = 16.dp, bottom = 96.dp),
            color = White,
            shape = RoundedCornerShape(15.dp),
            shadowElevation = 2.dp,
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, top = 16.dp, end = 20.dp, bottom = 24.dp),
            ) {
                Row {
                    ExcretaCountChip(
                        text = stringResource(R.string.excreta_count_format, stringResource(Excreta.FECES.labelRes), fecesCount),
                        backgroundColor = Main1,
                        textColor = Main4,
                    )
                    ExcretaCountChip(
                        text = stringResource(R.string.excreta_count_format, stringResource(Excreta.URINE.labelRes), urineCount),
                        backgroundColor = Sub2,
                        textColor = Sub3,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
                excretaRecords.forEach { record ->
                    ExcretaRecordItem(
                        typeText = stringResource(Excreta.valueOf(record.excretaType).labelRes),
                        timeText = convertToTimeFormat(record.time),
                        onClick = { onItemClick(record.excretaId) },
                        modifier = Modifier.padding(top = 16.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun ExcretaCountChip(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = SemobanTypography.body1Medium,
        color = textColor,
        modifier =
            modifier
                .background(backgroundColor, RoundedCornerShape(5.dp))
                .padding(8.dp),
    )
}

@Preview(showBackground = true)
@Composable
private fun ExcretaScreenPreview() {
    SemobanTheme {
        ExcretaScreen(
            dogName = "몽실이",
            fecesCount = 1,
            urineCount = 2,
            excretaRecords =
                listOf(
                    ExcretaRecord(1, "2024-01-01T08:00:00", "FECES"),
                    ExcretaRecord(2, "2024-01-01T10:30:00", "URINE"),
                    ExcretaRecord(3, "2024-01-01T15:10:00", "URINE"),
                ),
            onAddClick = {},
            onEditClick = {},
            onItemClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ExcretaScreenEmptyPreview() {
    SemobanTheme {
        ExcretaScreen(
            dogName = "몽실이",
            fecesCount = 0,
            urineCount = 0,
            excretaRecords = emptyList(),
            onAddClick = {},
            onEditClick = {},
            onItemClick = {},
        )
    }
}
