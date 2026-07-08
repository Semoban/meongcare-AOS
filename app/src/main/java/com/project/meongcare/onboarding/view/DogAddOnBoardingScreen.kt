package com.project.meongcare.onboarding.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.meongcare.R
import com.project.meongcare.designsystem.theme.Gray2
import com.project.meongcare.designsystem.theme.Gray3
import com.project.meongcare.designsystem.theme.Gray4
import com.project.meongcare.designsystem.theme.Gray5
import com.project.meongcare.designsystem.theme.Main4
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.White
import com.project.meongcare.info.view.GenderChip
import com.project.meongcare.info.view.InfoFormClickBox
import com.project.meongcare.info.view.InfoFormNumberField
import com.project.meongcare.info.view.InfoFormTextField
import com.project.meongcare.info.view.InfoGlideImage
import com.project.meongcare.info.view.NeuterCheckbox
import com.project.meongcare.onboarding.model.entities.Gender
import com.project.meongcare.onboarding.util.DogAddOnBoardingDateUtils.dateFormat

data class DogAddFormResult(
    val name: String,
    val gender: String,
    val castrate: Boolean,
    val weight: Double,
    val backRound: Double?,
    val neckRound: Double?,
    val chestRound: Double?,
)

@Composable
fun DogAddOnBoardingScreen(
    imageModel: Any?,
    dogType: String?,
    birthDate: String?,
    showCancelButton: Boolean,
    onImageClick: () -> Unit,
    onTypeClick: () -> Unit,
    onBirthdayClick: () -> Unit,
    onCancelClick: () -> Unit,
    onComplete: (DogAddFormResult) -> Unit,
    modifier: Modifier = Modifier,
) {
    var name by rememberSaveable { mutableStateOf("") }
    var weight by rememberSaveable { mutableStateOf("") }
    var backRound by rememberSaveable { mutableStateOf("") }
    var chestRound by rememberSaveable { mutableStateOf("") }
    var neckRound by rememberSaveable { mutableStateOf("") }
    var selectedGender by rememberSaveable { mutableStateOf<String?>(null) }
    var castrate by rememberSaveable { mutableStateOf(false) }
    var submitAttempted by remember { mutableStateOf(false) }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(White)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp),
    ) {
        Text(
            text = "반려견에 대해 알려주세요",
            style = SemobanTypography.title2SemiBold,
            modifier = Modifier.padding(top = 32.dp),
        )
        Box(
            modifier =
                Modifier
                    .padding(top = 32.dp)
                    .width(178.dp)
                    .height(125.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(Gray2)
                    .clickable { onImageClick() },
            contentAlignment = Alignment.Center,
        ) {
            if (imageModel == null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(R.drawable.pet_add_dog),
                        contentDescription = null,
                    )
                    Text(
                        text = "사진을 첨부해주세요",
                        style = SemobanTypography.body3Regular,
                        color = Gray5,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            } else {
                InfoGlideImage(
                    model = imageModel,
                    errorRes = R.drawable.dog_profile_default,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
        DogAddFormLabel(text = "이름", modifier = Modifier.padding(top = 24.dp))
        InfoFormTextField(
            value = name,
            onValueChange = { name = it },
            hint = "이름을 입력해주세요",
            showError = submitAttempted && name.isEmpty(),
            modifier = Modifier.padding(top = 8.dp),
        )
        DogAddFormLabel(text = "품종", modifier = Modifier.padding(top = 24.dp))
        InfoFormClickBox(
            value = dogType.orEmpty(),
            hint = "품종을 입력해주세요",
            showError = submitAttempted && dogType.isNullOrEmpty(),
            onClick = onTypeClick,
            modifier = Modifier.padding(top = 8.dp),
        )
        DogAddFormLabel(text = "성별", modifier = Modifier.padding(top = 24.dp))
        Row(
            modifier = Modifier.padding(top = 8.dp),
            verticalAlignment = Alignment.Top,
        ) {
            GenderChip(
                gender = Gender.FEMALE,
                isSelected = selectedGender == Gender.FEMALE.english,
                onClick = { selectedGender = Gender.FEMALE.english },
            )
            GenderChip(
                gender = Gender.MALE,
                isSelected = selectedGender == Gender.MALE.english,
                onClick = { selectedGender = Gender.MALE.english },
                modifier = Modifier.padding(start = 8.dp),
            )
            NeuterCheckbox(
                checked = castrate,
                onClick = { castrate = !castrate },
                modifier = Modifier.padding(start = 8.dp, top = 7.dp),
            )
        }
        DogAddFormLabel(text = "생일을 알려주세요", modifier = Modifier.padding(top = 24.dp))
        InfoFormClickBox(
            value = birthDate?.let { dateFormat(it) }.orEmpty(),
            hint = "날짜를 선택해주세요",
            showError = submitAttempted && birthDate.isNullOrEmpty(),
            onClick = onBirthdayClick,
            modifier = Modifier.padding(top = 4.dp),
        )
        Text(
            text = "강아지의 생일을 모를 경우, 처음 만난 날을 입력 해주세요",
            style = SemobanTypography.body3Regular,
            color = Gray5,
            modifier = Modifier.padding(top = 4.dp),
        )
        DogAddFormLabel(text = "체중", modifier = Modifier.padding(top = 24.dp))
        InfoFormNumberField(
            value = weight,
            onValueChange = { weight = it },
            hint = "",
            unit = "Kg",
            showError = submitAttempted && weight.isEmpty(),
            modifier =
                Modifier
                    .padding(top = 4.dp)
                    .width(148.dp),
        )
        Text(
            text = "치수",
            style = SemobanTypography.body1SemiBold,
            modifier = Modifier.padding(top = 24.dp),
        )
        Row(modifier = Modifier.padding(top = 8.dp)) {
            InfoFormNumberField(
                value = backRound,
                onValueChange = { backRound = it },
                hint = "등",
                unit = "cm",
                showError = false,
                modifier = Modifier.weight(1f),
            )
            InfoFormNumberField(
                value = chestRound,
                onValueChange = { chestRound = it },
                hint = "가슴둘레",
                unit = "cm",
                showError = false,
                modifier =
                    Modifier
                        .padding(start = 8.dp)
                        .width(117.dp),
            )
            InfoFormNumberField(
                value = neckRound,
                onValueChange = { neckRound = it },
                hint = "목둘레",
                unit = "cm",
                showError = false,
                modifier =
                    Modifier
                        .padding(start = 8.dp)
                        .weight(1f),
            )
        }
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 42.dp, bottom = 56.dp)
                    .height(44.dp),
        ) {
            if (showCancelButton) {
                Box(
                    modifier =
                        Modifier
                            .width(112.dp)
                            .fillMaxSize()
                            .background(White, RoundedCornerShape(5.dp))
                            .border(1.dp, Gray3, RoundedCornerShape(5.dp))
                            .clickable { onCancelClick() },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "취소",
                        style = SemobanTypography.bottom1SemiBold,
                        color = Gray4,
                    )
                }
            }
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .padding(start = if (showCancelButton) 8.dp else 0.dp)
                        .background(Main4, RoundedCornerShape(5.dp))
                        .clickable {
                            submitAttempted = true
                            if (name.isNotEmpty() &&
                                !dogType.isNullOrEmpty() &&
                                selectedGender != null &&
                                !birthDate.isNullOrEmpty() &&
                                weight.isNotEmpty()
                            ) {
                                onComplete(
                                    DogAddFormResult(
                                        name = name,
                                        gender = selectedGender!!,
                                        castrate = castrate,
                                        weight = weight.toDouble(),
                                        backRound = backRound.toDoubleOrNull(),
                                        neckRound = neckRound.toDoubleOrNull(),
                                        chestRound = chestRound.toDoubleOrNull(),
                                    ),
                                )
                            }
                        },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "완료",
                    style = SemobanTypography.bottom1SemiBold,
                    color = White,
                )
            }
        }
    }
}

@Composable
private fun DogAddFormLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = text, style = SemobanTypography.body1SemiBold)
        Image(
            painter = painterResource(R.drawable.essential_input_element_icon),
            contentDescription = "필수 입력",
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DogAddOnBoardingScreenPreview() {
    SemobanTheme {
        DogAddOnBoardingScreen(
            imageModel = null,
            dogType = null,
            birthDate = null,
            showCancelButton = true,
            onImageClick = {},
            onTypeClick = {},
            onBirthdayClick = {},
            onCancelClick = {},
            onComplete = {},
        )
    }
}
