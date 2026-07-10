package com.project.meongcare.symptom.view

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
    val title: String,
    val subtitle: String,
)

// 증상 제목은 서버에 symptomString으로 그대로 전송되어 아이콘 매핑에도 쓰이므로
// 백엔드 협의(i18n ④단계) 전까지 리소스로 추출하지 않는다
internal val symptomSelectOptions =
    listOf(
        SymptomSelectOption(
            R.drawable.all_weighing_machine,
            "체중이 감소한다",
            "갑작스럽게 체중이 감소하는 증상이 나타난다",
        ),
        SymptomSelectOption(
            R.drawable.all_temperature_measurement,
            "고열이 난다",
            "체온이 41도 이상인 상태가 오랫동안 지속된다",
        ),
        SymptomSelectOption(
            R.drawable.symptom_cough,
            "기침을 한다",
            "갑자기 켁켁거리며 기침이 많아진다",
        ),
        SymptomSelectOption(
            R.drawable.symptom_diarrhea,
            "설사를 한다",
            "갑자기 설사를 한다",
        ),
        SymptomSelectOption(
            R.drawable.symptom_loss_appetite,
            "식욕부진이 나타난다",
            "사료를 먹지 않는 행동을 보인다",
        ),
        SymptomSelectOption(
            R.drawable.symptom_amount_activity,
            "활동량이 감소한다",
            "평소에 비해 활동량이 적어진다",
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
                            symptomSelectOptions[selectedIndex].title,
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
                Text(text = option.title, style = SemobanTypography.body2Medium)
                Text(text = option.subtitle, style = SemobanTypography.body3Regular)
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
