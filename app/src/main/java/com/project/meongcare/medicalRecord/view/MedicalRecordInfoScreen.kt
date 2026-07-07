package com.project.meongcare.medicalRecord.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.meongcare.R
import com.project.meongcare.designsystem.theme.Gray1
import com.project.meongcare.designsystem.theme.Gray4
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.White
import com.project.meongcare.medicalRecord.model.entities.MedicalRecordGet
import com.project.meongcare.medicalRecord.model.utils.MedicalRecordUtils.Companion.convertMDateToSimpleDate
import com.project.meongcare.medicalRecord.model.utils.MedicalRecordUtils.Companion.convertMDateToSimpleTime

@Composable
fun MedicalRecordInfoScreen(
    record: MedicalRecordGet?,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
                    .padding(top = 14.dp, end = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(R.drawable.all_arrow_back_18dp),
                    contentDescription = "뒤로가기",
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onEditClick) {
                Icon(
                    painter = painterResource(R.drawable.all_edit),
                    contentDescription = "수정",
                )
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    painter = painterResource(R.drawable.all_delete),
                    contentDescription = "삭제",
                )
            }
        }
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 32.dp)
                    .padding(top = 25.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .height(169.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .background(Gray1),
                contentAlignment = Alignment.Center,
            ) {
                if (record?.imageUrl.isNullOrBlank()) {
                    Image(
                        painter = painterResource(R.drawable.medical_record_add_carrier),
                        contentDescription = null,
                    )
                } else {
                    MedicalRecordGlideImage(
                        model = record?.imageUrl!!,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
            MedicalRecordInfoLabel(
                text = stringResource(R.string.medicalrecord_date),
                modifier = Modifier.padding(top = 24.dp),
            )
            Text(
                text = record?.dateTime?.let { convertMDateToSimpleDate(it) }.orEmpty(),
                style = SemobanTypography.body1Medium,
                modifier = Modifier.padding(top = 8.dp),
            )
            MedicalRecordInfoLabel(
                text = stringResource(R.string.medicalrecord_time),
                modifier = Modifier.padding(top = 24.dp),
            )
            Text(
                text = record?.dateTime?.let { convertMDateToSimpleTime(it) }.orEmpty(),
                style = SemobanTypography.body1Medium,
                modifier = Modifier.padding(top = 8.dp),
            )
            MedicalRecordInfoLabel(
                text = stringResource(R.string.medicalrecord_hospital),
                modifier = Modifier.padding(top = 24.dp),
            )
            MedicalRecordInfoContentBox(
                text = record?.hospitalName.orEmpty(),
                modifier = Modifier.padding(top = 8.dp),
            )
            MedicalRecordInfoCount(
                text = stringResource(R.string.medicalrecord_hospital_name_length, record?.hospitalName?.length ?: 0),
            )
            MedicalRecordInfoLabel(
                text = stringResource(R.string.medicalrecord_veterinarian),
                modifier = Modifier.padding(top = 16.dp),
            )
            MedicalRecordInfoContentBox(
                text = record?.doctorName.orEmpty(),
                modifier = Modifier.padding(top = 8.dp),
            )
            MedicalRecordInfoCount(
                text = stringResource(R.string.medicalrecord_veterinarian_name_length, record?.doctorName?.length ?: 0),
            )
            MedicalRecordInfoLabel(
                text = stringResource(R.string.medicalrecord_note),
                modifier = Modifier.padding(top = 16.dp),
            )
            MedicalRecordInfoContentBox(
                text = record?.note.orEmpty(),
                modifier =
                    Modifier
                        .padding(top = 8.dp)
                        .height(148.dp),
            )
            MedicalRecordInfoCount(
                text = stringResource(R.string.medicalrecord_note_length, record?.note?.length ?: 0),
            )
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun MedicalRecordInfoLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = SemobanTypography.title3SemiBold,
        modifier = modifier,
    )
}

@Composable
private fun MedicalRecordInfoContentBox(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = SemobanTypography.body1Regular,
        modifier =
            modifier
                .fillMaxWidth()
                .background(Gray1, RoundedCornerShape(5.dp))
                .padding(horizontal = 16.dp, vertical = 13.dp),
    )
}

@Composable
private fun MedicalRecordInfoCount(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = SemobanTypography.body2Regular,
        color = Gray4,
        textAlign = TextAlign.End,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
    )
}

@Preview(showBackground = true)
@Composable
private fun MedicalRecordInfoScreenPreview() {
    SemobanTheme {
        MedicalRecordInfoScreen(
            record =
                MedicalRecordGet(
                    medicalRecordId = 1L,
                    dateTime = "2024-01-01T10:30:00",
                    hospitalName = "멍케어 동물병원",
                    doctorName = "김수의",
                    note = "정기 검진 진행. 특이사항 없음.",
                    imageUrl = null,
                ),
            onBackClick = {},
            onEditClick = {},
            onDeleteClick = {},
        )
    }
}
