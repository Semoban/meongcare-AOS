package com.project.meongcare.supplement.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.meongcare.R
import com.project.meongcare.designsystem.theme.Gray1
import com.project.meongcare.designsystem.theme.Gray2
import com.project.meongcare.designsystem.theme.Gray4
import com.project.meongcare.designsystem.theme.Gray5
import com.project.meongcare.designsystem.theme.Main4
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.Sub1
import com.project.meongcare.designsystem.theme.White
import com.project.meongcare.supplement.model.entities.IntakeInfo

@Composable
fun SupplementAddScreen(
    imageModel: Any?,
    cycle: Int?,
    intakeUnit: String,
    intakeTimeList: List<IntakeInfo>,
    onBackClick: () -> Unit,
    onImageClick: () -> Unit,
    onCycleClick: () -> Unit,
    onUnitSelect: (String) -> Unit,
    onTimeAddClick: () -> Unit,
    onTimeRemove: (Int) -> Unit,
    onComplete: (brand: String, name: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var brand by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }
    var brandError by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf(false) }
    var cycleError by remember { mutableStateOf(false) }
    var timeError by remember { mutableStateOf(false) }
    var timeEditMode by remember { mutableStateOf(false) }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(White),
    ) {
        SupplementTopBar(onBack = onBackClick)
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
        ) {
            SupplementImageCard(
                imageModel = imageModel,
                description = "사진을 첨부해주세요",
                onClick = onImageClick,
            )
            EssentialLabel(
                text = "브랜드명",
                modifier = Modifier.padding(top = 31.dp),
            )
            SupplementTextField(
                value = brand,
                onValueChange = {
                    brandError = false
                    brand = it
                },
                hint = "브랜드를 입력해주세요",
                showError = brandError,
                modifier = Modifier.padding(top = 8.dp),
            )
            EssentialLabel(
                text = "제품명",
                modifier = Modifier.padding(top = 23.dp),
            )
            SupplementTextField(
                value = name,
                onValueChange = {
                    nameError = false
                    name = it
                },
                hint = "제품명을 입력해주세요",
                showError = nameError,
                modifier = Modifier.padding(top = 8.dp),
            )
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 28.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                EssentialLabel(
                    text = "섭취 주기",
                    modifier = Modifier.weight(1f),
                )
                Row(
                    modifier =
                        Modifier.clickable {
                            cycleError = false
                            onCycleClick()
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (cycle != null) {
                        Text(
                            text = "${cycle}일 마다",
                            style = SemobanTypography.body2Regular,
                            color = Gray5,
                            modifier = Modifier.padding(end = 10.dp),
                        )
                    }
                    Image(
                        painter = painterResource(R.drawable.all_arrow_forward),
                        contentDescription = null,
                    )
                }
            }
            if (cycleError && cycle == null) {
                SupplementErrorText(modifier = Modifier.padding(top = 4.dp))
            }
            SupplementSectionDivider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                EssentialLabel(
                    text = "섭취 단위",
                    modifier = Modifier.weight(1f),
                )
                SupplementUnitSelector(
                    selectedUnit = intakeUnit,
                    onUnitSelect = onUnitSelect,
                )
            }
            SupplementSectionDivider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (intakeTimeList.isEmpty()) {
                    EssentialLabel(text = "섭취 시간")
                } else {
                    Text(text = "섭취 시간", style = SemobanTypography.body1SemiBold)
                    SupplementIntakeCountBadge(
                        count = intakeTimeList.size,
                        modifier = Modifier.padding(start = 9.dp),
                    )
                }
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "추가",
                        style = SemobanTypography.body2Regular,
                        color = Gray4,
                        modifier =
                            Modifier.clickable {
                                timeError = false
                                onTimeAddClick()
                            },
                    )
                    if (intakeTimeList.isNotEmpty()) {
                        Text(
                            text = "편집",
                            style = SemobanTypography.body2Regular,
                            color = Gray4,
                            modifier =
                                Modifier
                                    .padding(start = 8.dp)
                                    .clickable { timeEditMode = true },
                        )
                    }
                }
            }
            if (timeError && intakeTimeList.isEmpty()) {
                SupplementErrorText(modifier = Modifier.padding(top = 4.dp))
            }
            Column(modifier = Modifier.padding(top = 16.dp)) {
                intakeTimeList.forEachIndexed { index, intakeInfo ->
                    SupplementIntakeTimeItem(
                        intakeInfo = intakeInfo,
                        intakeUnit = intakeUnit,
                        showMinus = timeEditMode,
                        onMinusClick = { onTimeRemove(index) },
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }
            }
        }
        Text(
            text = "완료",
            style = SemobanTypography.bottom1SemiBold,
            color = White,
            textAlign = TextAlign.Center,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(start = 36.dp, end = 36.dp, bottom = 19.dp)
                    .background(Main4, RoundedCornerShape(5.dp))
                    .clickable {
                        var isValid = true

                        if (brand.isEmpty()) {
                            brandError = true
                            isValid = false
                        }
                        if (name.isEmpty()) {
                            nameError = true
                            isValid = false
                        }
                        if (cycle == null) {
                            cycleError = true
                            isValid = false
                        }
                        if (intakeTimeList.isEmpty()) {
                            timeError = true
                            isValid = false
                        }

                        if (isValid) {
                            onComplete(brand, name)
                        }
                    }
                    .padding(vertical = 12.dp),
        )
    }
}

@Composable
private fun SupplementTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    showError: Boolean,
    modifier: Modifier = Modifier,
) {
    var fieldModifier =
        Modifier
            .fillMaxWidth()
            .height(44.dp)
    fieldModifier =
        if (showError) {
            fieldModifier
                .background(Gray1, RoundedCornerShape(5.dp))
                .border(1.dp, Sub1, RoundedCornerShape(5.dp))
        } else {
            fieldModifier.background(Gray2, RoundedCornerShape(5.dp))
        }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = SemobanTypography.body1Regular,
        singleLine = true,
        modifier = modifier.fillMaxWidth(),
        decorationBox = { innerTextField ->
            Box(
                modifier = fieldModifier.padding(horizontal = 16.dp, vertical = 11.dp),
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = if (showError) "필수 입력 값입니다" else hint,
                        style = SemobanTypography.body1Regular,
                        color = if (showError) Sub1 else Gray4,
                    )
                }
                innerTextField()
            }
        },
    )
}

@Composable
private fun SupplementErrorText(modifier: Modifier = Modifier) {
    Text(
        text = "필수 입력 값입니다.",
        style = SemobanTypography.body3Regular,
        color = Sub1,
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
private fun SupplementAddScreenPreview() {
    SemobanTheme {
        SupplementAddScreen(
            imageModel = null,
            cycle = null,
            intakeUnit = "mg",
            intakeTimeList = emptyList(),
            onBackClick = {},
            onImageClick = {},
            onCycleClick = {},
            onUnitSelect = {},
            onTimeAddClick = {},
            onTimeRemove = {},
            onComplete = { _, _ -> },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SupplementAddScreenFilledPreview() {
    SemobanTheme {
        SupplementAddScreen(
            imageModel = null,
            cycle = 2,
            intakeUnit = "정",
            intakeTimeList =
                listOf(
                    IntakeInfo("08:00:00", 1),
                    IntakeInfo("20:30:00", 2),
                ),
            onBackClick = {},
            onImageClick = {},
            onCycleClick = {},
            onUnitSelect = {},
            onTimeAddClick = {},
            onTimeRemove = {},
            onComplete = { _, _ -> },
        )
    }
}
