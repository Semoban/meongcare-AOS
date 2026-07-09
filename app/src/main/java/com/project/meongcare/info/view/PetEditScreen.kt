package com.project.meongcare.info.view

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.project.meongcare.designsystem.component.FormClickBox
import com.project.meongcare.designsystem.component.FormNumberField
import com.project.meongcare.designsystem.component.FormTextField
import com.project.meongcare.designsystem.component.GenderChip
import com.project.meongcare.designsystem.component.GlideImage
import com.project.meongcare.designsystem.component.NeuterCheckbox
import com.project.meongcare.designsystem.theme.Gray3
import com.project.meongcare.designsystem.theme.Gray4
import com.project.meongcare.designsystem.theme.Gray5
import com.project.meongcare.designsystem.theme.Main4
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.White
import com.project.meongcare.info.model.entities.GetDogInfoResponse
import com.project.meongcare.onboarding.model.entities.Gender
import com.project.meongcare.onboarding.util.DogAddOnBoardingDateUtils.dateFormat

data class PetEditFormResult(
    val name: String,
    val gender: String,
    val castrate: Boolean,
    val weight: Double,
    val backRound: Double?,
    val neckRound: Double?,
    val chestRound: Double?,
)

@Composable
fun PetEditScreen(
    initialDogInfo: GetDogInfoResponse,
    imageModel: Any?,
    dogType: String?,
    birthDate: String?,
    onBackClick: () -> Unit,
    onImageClick: () -> Unit,
    onTypeClick: () -> Unit,
    onBirthdayClick: () -> Unit,
    onComplete: (PetEditFormResult) -> Unit,
    modifier: Modifier = Modifier,
) {
    var name by rememberSaveable { mutableStateOf(initialDogInfo.name) }
    var weight by rememberSaveable { mutableStateOf(initialDogInfo.weight.toString()) }
    var backRound by rememberSaveable { mutableStateOf(formatBodySize(initialDogInfo.backRound)) }
    var chestRound by rememberSaveable { mutableStateOf(formatBodySize(initialDogInfo.chestRound)) }
    var neckRound by rememberSaveable { mutableStateOf(formatBodySize(initialDogInfo.neckRound)) }
    var selectedGender by rememberSaveable { mutableStateOf(initialDogInfo.sex) }
    var castrate by rememberSaveable { mutableStateOf(initialDogInfo.castrate) }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(White)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 36.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(R.drawable.all_arrow_back_18dp),
                    contentDescription = "뒤로가기",
                )
            }
        }
        GlideImage(
            model = imageModel,
            errorRes = R.drawable.dog_profile_default,
            centerCrop = true,
            modifier =
                Modifier
                    .padding(top = 42.dp)
                    .align(Alignment.CenterHorizontally)
                    .width(250.dp)
                    .height(151.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .clickable { onImageClick() },
        )
        PetFormLabel(text = "이름", modifier = Modifier.padding(top = 32.dp))
        FormTextField(
            value = name,
            onValueChange = { name = it },
            hint = "이름을 입력해주세요",
            showError = name.isEmpty(),
            modifier = Modifier.padding(top = 8.dp),
        )
        PetFormLabel(text = "품종", modifier = Modifier.padding(top = 24.dp))
        FormClickBox(
            value = dogType.orEmpty(),
            hint = "품종을 입력해주세요",
            showError = dogType.isNullOrEmpty(),
            onClick = onTypeClick,
            modifier = Modifier.padding(top = 8.dp),
        )
        PetFormLabel(text = "성별", modifier = Modifier.padding(top = 24.dp))
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
        PetFormLabel(text = "생일을 알려주세요", modifier = Modifier.padding(top = 24.dp))
        FormClickBox(
            value = birthDate?.let { dateFormat(it) }.orEmpty(),
            hint = "날짜를 선택해주세요",
            showError = birthDate.isNullOrEmpty(),
            onClick = onBirthdayClick,
            modifier = Modifier.padding(top = 4.dp),
        )
        Text(
            text = "강아지의 생일을 모를 경우, 처음 만난 날을 입력 해주세요",
            style = SemobanTypography.body3Regular,
            color = Gray5,
            modifier = Modifier.padding(top = 4.dp),
        )
        PetFormLabel(text = "체중", modifier = Modifier.padding(top = 24.dp))
        FormNumberField(
            value = weight,
            onValueChange = { weight = it },
            hint = "",
            unit = "Kg",
            showError = weight.isEmpty(),
            modifier =
                Modifier
                    .padding(top = 4.dp)
                    .width(148.dp),
        )
        PetFormLabel(text = "치수", modifier = Modifier.padding(top = 24.dp))
        Row(modifier = Modifier.padding(top = 8.dp)) {
            FormNumberField(
                value = backRound,
                onValueChange = { backRound = it },
                hint = "등",
                unit = "cm",
                showError = false,
                modifier = Modifier.weight(1f),
            )
            FormNumberField(
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
            FormNumberField(
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
                    .padding(top = 40.dp, bottom = 72.dp)
                    .height(45.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .background(White, RoundedCornerShape(4.dp))
                        .border(1.dp, Gray3, RoundedCornerShape(4.dp))
                        .clickable { onBackClick() },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "취소",
                    style = SemobanTypography.bottom1SemiBold,
                    color = Gray4,
                )
            }
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .padding(start = 8.dp)
                        .background(Main4, RoundedCornerShape(4.dp))
                        .clickable {
                            if (name.isNotEmpty() &&
                                !dogType.isNullOrEmpty() &&
                                !birthDate.isNullOrEmpty() &&
                                weight.isNotEmpty()
                            ) {
                                onComplete(
                                    PetEditFormResult(
                                        name = name,
                                        gender = selectedGender,
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

@Preview(showBackground = true)
@Composable
private fun PetEditScreenPreview() {
    SemobanTheme {
        PetEditScreen(
            initialDogInfo =
                GetDogInfoResponse(
                    dogId = 1,
                    name = "몽실이",
                    imageUrl = null,
                    type = "말티즈",
                    sex = "female",
                    castrate = true,
                    birthDate = "2020-05-01",
                    backRound = 30.0,
                    neckRound = 20.0,
                    chestRound = 40.0,
                    weight = 3.5,
                ),
            imageModel = null,
            dogType = "말티즈",
            birthDate = "2020-05-01",
            onBackClick = {},
            onImageClick = {},
            onTypeClick = {},
            onBirthdayClick = {},
            onComplete = {},
        )
    }
}
