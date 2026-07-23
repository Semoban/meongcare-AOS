package com.project.meongcare.symptom.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.meongcare.R
import com.project.meongcare.designsystem.component.DeleteDialogOverlay
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.White

@Composable
fun SymptomInfoScreen(
    dogName: String?,
    dateText: String,
    timeText: String,
    symptomImgRes: Int,
    symptomTitle: String,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
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
            ) {
                IconButton(onClick = onEdit) {
                    Icon(
                        painter = painterResource(R.drawable.all_edit),
                        contentDescription = stringResource(R.string.all_modify),
                    )
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        painter = painterResource(R.drawable.all_delete),
                        contentDescription = stringResource(R.string.all_delete),
                    )
                }
            }
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
            ) {
                Text(text = stringResource(R.string.symptom_date_label), style = SemobanTypography.body1SemiBold)
                SymptomDateBox(
                    dateText = dateText,
                    isError = false,
                    modifier = Modifier.padding(top = 10.dp),
                )
                Text(
                    text = stringResource(R.string.symptom_time_label),
                    style = SemobanTypography.body1SemiBold,
                    modifier = Modifier.padding(top = 25.dp),
                )
                SymptomDateBox(
                    dateText = timeText,
                    isError = false,
                    modifier = Modifier.padding(top = 10.dp),
                )
                Text(
                    text = stringResource(R.string.symptom_label),
                    style = SemobanTypography.body1SemiBold,
                    modifier = Modifier.padding(top = 25.dp),
                )
                SelectedSymptomItem(
                    imgRes = symptomImgRes,
                    title = symptomTitle,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
        if (showDeleteDialog) {
            DeleteDialogOverlay(
                onCancel = { showDeleteDialog = false },
                onDelete = onDelete,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SymptomInfoScreenPreview() {
    SemobanTheme {
        SymptomInfoScreen(
            dogName = "몽실이",
            dateText = "2024년 01월 01일",
            timeText = "오전 8:00",
            symptomImgRes = R.drawable.symptom_cough,
            symptomTitle = "기침을 한다",
            onBack = {},
            onEdit = {},
            onDelete = {},
        )
    }
}
