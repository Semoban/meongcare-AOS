package com.project.meongcare.symptom.view

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.meongcare.R
import com.project.meongcare.designsystem.theme.Gray2
import com.project.meongcare.designsystem.theme.Gray3
import com.project.meongcare.designsystem.theme.Gray4
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.White

internal data class SymptomSelectOption(
    val imgRes: Int,
    @StringRes val titleRes: Int,
    @StringRes val subtitleRes: Int,
)

internal val symptomSelectOptions =
    listOf(
        SymptomSelectOption(
            R.drawable.all_weighing_machine,
            R.string.symptom_type_weight_loss,
            R.string.symptom_type_weight_loss_desc,
        ),
        SymptomSelectOption(
            R.drawable.all_temperature_measurement,
            R.string.symptom_type_high_fever,
            R.string.symptom_type_high_fever_desc,
        ),
        SymptomSelectOption(
            R.drawable.symptom_cough,
            R.string.symptom_type_cough,
            R.string.symptom_type_cough_desc,
        ),
        SymptomSelectOption(
            R.drawable.symptom_diarrhea,
            R.string.symptom_type_diarrhea,
            R.string.symptom_type_diarrhea_desc,
        ),
        SymptomSelectOption(
            R.drawable.symptom_loss_appetite,
            R.string.symptom_type_loss_of_appetite,
            R.string.symptom_type_loss_of_appetite_desc,
        ),
        SymptomSelectOption(
            R.drawable.symptom_amount_activity,
            R.string.symptom_type_activity_decrease,
            R.string.symptom_type_activity_decrease_desc,
        ),
    )

private const val ETC_INDEX = -2
private const val NONE_INDEX = -1

@Composable
fun SymptomSelectScreen(
    onCancel: () -> Unit,
    onEmptySelection: () -> Unit,
    onSelected: (imgRes: Int, title: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedIndex by remember { mutableStateOf(NONE_INDEX) }
    var customText by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(White),
    ) {
        SymptomTopBar()
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(top = 35.dp, start = 15.dp, end = 15.dp, bottom = 20.dp),
        ) {
            symptomSelectOptions.forEachIndexed { index, option ->
                SymptomSelectRow(
                    option = option,
                    checked = selectedIndex == index,
                    onClick = { selectedIndex = index },
                    modifier = Modifier.padding(top = if (index == 0) 0.dp else 16.dp),
                )
            }
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CheckToggleIcon(
                    checked = selectedIndex == ETC_INDEX,
                    modifier = Modifier.clickable { selectedIndex = ETC_INDEX },
                )
                Box(
                    modifier =
                        Modifier
                            .weight(1f)
                            .padding(start = 9.dp)
                            .height(64.dp)
                            .background(Gray2, RoundedCornerShape(5.dp))
                            .border(1.dp, Gray3, RoundedCornerShape(5.dp))
                            .padding(15.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    BasicTextField(
                        value = customText,
                        onValueChange = {
                            customText = it
                            selectedIndex = ETC_INDEX
                        },
                        singleLine = true,
                        textStyle = SemobanTypography.body2Medium,
                        modifier = Modifier.fillMaxWidth(),
                        decorationBox = { innerTextField ->
                            if (customText.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.symptom_custom_input_hint),
                                    style = SemobanTypography.body2Medium,
                                    color = Gray4,
                                )
                            }
                            innerTextField()
                        },
                    )
                }
            }
        }
        CancelCompleteButtons(
            completeText = stringResource(R.string.all_completion),
            onCancel = onCancel,
            onComplete = {
                when (selectedIndex) {
                    NONE_INDEX -> onEmptySelection()
                    ETC_INDEX -> onSelected(R.drawable.symptom_etc_record, customText)
                    else ->
                        onSelected(
                            symptomSelectOptions[selectedIndex].imgRes,
                            context.getString(symptomSelectOptions[selectedIndex].titleRes),
                        )
                }
            },
            modifier = Modifier.padding(start = 22.dp, end = 22.dp, bottom = 20.dp),
        )
    }
}

@Composable
private fun SymptomSelectRow(
    option: SymptomSelectOption,
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
        CheckToggleIcon(checked = checked)
        Row(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(start = 9.dp)
                    .background(Gray2, RoundedCornerShape(5.dp))
                    .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(option.imgRes),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
            )
            Column(modifier = Modifier.padding(start = 13.dp)) {
                Text(text = stringResource(option.titleRes), style = SemobanTypography.body2Medium)
                Text(text = stringResource(option.subtitleRes), style = SemobanTypography.body3Regular)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SymptomSelectScreenPreview() {
    SemobanTheme {
        SymptomSelectScreen(
            onCancel = {},
            onEmptySelection = {},
            onSelected = { _, _ -> },
        )
    }
}
