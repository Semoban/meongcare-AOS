package com.project.meongcare.symptom.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.meongcare.R
import com.project.meongcare.designsystem.theme.Gray2
import com.project.meongcare.designsystem.theme.Gray4
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.White
import com.project.meongcare.symptom.model.entities.Symptom
import com.project.meongcare.symptom.utils.SymptomUtils.Companion.convertDateToTime
import com.project.meongcare.symptom.utils.SymptomUtils.Companion.getSymptomImg

@Composable
fun SymptomScreen(
    dogName: String?,
    symptomList: List<Symptom>,
    onAddClick: () -> Unit,
    onEditClick: () -> Unit,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Gray2)
                .padding(horizontal = 24.dp, vertical = 24.dp),
    ) {
        SymptomHeader(
            dogName = dogName,
            showEdit = symptomList.isNotEmpty(),
            onAddClick = onAddClick,
            onEditClick = onEditClick,
        )
        if (symptomList.isEmpty()) {
            SymptomNoData(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 22.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                itemsIndexed(
                    items = symptomList,
                    key = { _, symptom -> symptom.symptomId },
                ) { index, symptom ->
                    SymptomItemCard(
                        symptom = symptom,
                        onClick = { onItemClick(index) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SymptomHeader(
    dogName: String?,
    showEdit: Boolean,
    onAddClick: () -> Unit,
    onEditClick: () -> Unit,
) {
    if (dogName.isNullOrBlank()) return

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "${dogName}님의 이상증상",
            style = SemobanTypography.title3SemiBold,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "추가",
            style = SemobanTypography.body1Regular,
            color = Gray4,
            modifier = Modifier.clickable { onAddClick() },
        )
        if (showEdit) {
            Text(
                text = "편집",
                style = SemobanTypography.body1Regular,
                color = Gray4,
                modifier =
                    Modifier
                        .padding(start = 8.dp)
                        .clickable { onEditClick() },
            )
        }
    }
}

@Composable
private fun SymptomItemCard(
    symptom: Symptom,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = White,
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 4.dp,
        onClick = onClick
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
                    text = symptom.note,
                    style = SemobanTypography.body1Medium,
                    modifier = Modifier.padding(start = 13.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F4F4)
@Composable
private fun SymptomItemCardPreview() {
    SemobanTheme {
        SymptomItemCard(
            symptom = Symptom(1, "2024-01-01T08:00:00", "기침", "아침부터 기침을 해요"),
            onClick = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F4F4)
@Composable
private fun SymptomHeaderPreview() {
    SemobanTheme {
        SymptomHeader(
            dogName = "몽실이",
            showEdit = true,
            onAddClick = {},
            onEditClick = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F4F4, heightDp = 300)
@Composable
private fun SymptomNoDataPreview() {
    SemobanTheme {
        SymptomNoData(modifier = Modifier.fillMaxSize())
    }
}

@Preview(showBackground = true)
@Composable
private fun SymptomScreenPreview() {
    SemobanTheme {
        SymptomScreen(
            dogName = "몽실이",
            symptomList =
                listOf(
                    Symptom(1, "2024-01-01T08:00:00", "기침을 해요", "아침부터 기침을 해요"),
                    Symptom(2, "2024-01-01T14:30:00", "설사를 해요", "점심 이후 설사"),
                ),
            onAddClick = {},
            onEditClick = {},
            onItemClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SymptomScreenEmptyPreview() {
    SemobanTheme {
        SymptomScreen(
            dogName = "몽실이",
            symptomList = emptyList(),
            onAddClick = {},
            onEditClick = {},
            onItemClick = {},
        )
    }
}
