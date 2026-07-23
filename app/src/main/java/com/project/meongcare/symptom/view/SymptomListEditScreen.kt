package com.project.meongcare.symptom.view

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
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
import com.project.meongcare.designsystem.theme.Gray2
import com.project.meongcare.designsystem.theme.Gray5
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.White
import com.project.meongcare.symptom.model.entities.Symptom
import com.project.meongcare.symptom.utils.SymptomUtils.Companion.convertDateToTime
import com.project.meongcare.symptom.utils.SymptomUtils.Companion.getSymptomImg
import com.project.meongcare.symptom.utils.SymptomUtils.Companion.getSymptomTitleRes

@Composable
fun SymptomListEditScreen(
    dogName: String?,
    symptomList: List<Symptom>,
    checkedIds: List<Int>,
    onBack: () -> Unit,
    onToggleAll: () -> Unit,
    onToggleItem: (Int) -> Unit,
    onCancel: () -> Unit,
    onEmptyDelete: () -> Unit,
    onDeleteConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(White),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            SymptomTopBar(
                title = if (dogName.isNullOrBlank()) "" else stringResource(R.string.symptom_title_format, dogName),
                onBack = onBack,
            )
            if (symptomList.isEmpty()) {
                SymptomNoData(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1f),
                )
            } else {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(24.dp),
                ) {
                    Row(
                        modifier =
                            Modifier
                                .clickable { onToggleAll() }
                                .padding(bottom = 24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CheckToggleIcon(
                            checked = checkedIds.isNotEmpty() && checkedIds.size == symptomList.size,
                        )
                        Text(
                            text = stringResource(R.string.all_deleteall),
                            style = SemobanTypography.body1Medium,
                            color = Gray5,
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                    LazyColumn(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 11.dp),
                    ) {
                        items(
                            items = symptomList,
                            key = { it.symptomId },
                        ) { symptom ->
                            SymptomListEditItem(
                                symptom = symptom,
                                checked = checkedIds.contains(symptom.symptomId),
                                onClick = { onToggleItem(symptom.symptomId) },
                            )
                        }
                    }
                }
                CancelCompleteButtons(
                    completeText = stringResource(R.string.all_delete),
                    onCancel = onCancel,
                    onComplete = {
                        if (checkedIds.isEmpty()) {
                            onEmptyDelete()
                        } else {
                            showDeleteDialog = true
                        }
                    },
                    modifier = Modifier.padding(start = 27.dp, end = 27.dp, bottom = 32.dp, top = 10.dp),
                )
            }
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
private fun SymptomListEditItem(
    symptom: Symptom,
    checked: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(bottom = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CheckToggleIcon(checked = checked)
        Surface(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
            color = White,
            shape = RoundedCornerShape(10.dp),
            shadowElevation = 4.dp,
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = convertDateToTime(symptom.dateTime),
                    style = SemobanTypography.body2Medium,
                )
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp)
                            .background(Gray2, RoundedCornerShape(5.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(getSymptomImg(symptom)),
                        contentDescription = symptom.symptomString,
                        modifier = Modifier.size(24.dp),
                    )
                    Text(
                        text = getSymptomTitleRes(symptom.symptomString)?.let { stringResource(it) } ?: symptom.note,
                        style = SemobanTypography.body1Medium,
                        modifier = Modifier.padding(start = 13.dp),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SymptomListEditScreenPreview() {
    SemobanTheme {
        SymptomListEditScreen(
            dogName = "몽실이",
            symptomList =
                listOf(
                    Symptom(1, "2024-01-01T08:00:00", "기침을 해요", "아침부터 기침을 해요"),
                    Symptom(2, "2024-01-01T14:30:00", "설사를 해요", "점심 이후 설사"),
                ),
            checkedIds = listOf(1),
            onBack = {},
            onToggleAll = {},
            onToggleItem = {},
            onCancel = {},
            onEmptyDelete = {},
            onDeleteConfirm = {},
        )
    }
}
