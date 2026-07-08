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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.meongcare.R
import com.project.meongcare.designsystem.theme.Black
import com.project.meongcare.designsystem.theme.Gray1
import com.project.meongcare.designsystem.theme.Gray3
import com.project.meongcare.designsystem.theme.Gray4
import com.project.meongcare.designsystem.theme.Gray5
import com.project.meongcare.designsystem.theme.Main4
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.Sub1
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
        InfoGlideImage(
            model = imageModel,
            errorRes = R.drawable.dog_profile_default,
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
        PetEditTextField(
            value = name,
            onValueChange = { name = it },
            hint = "이름을 입력해주세요",
            modifier = Modifier.padding(top = 8.dp),
        )
        PetFormLabel(text = "품종", modifier = Modifier.padding(top = 24.dp))
        PetEditClickBox(
            value = dogType.orEmpty(),
            hint = "품종을 입력해주세요",
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
        PetEditClickBox(
            value = birthDate?.let { dateFormat(it) }.orEmpty(),
            hint = "날짜를 선택해주세요",
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
        PetEditNumberField(
            value = weight,
            onValueChange = { weight = it },
            hint = "",
            unit = "Kg",
            modifier =
                Modifier
                    .padding(top = 4.dp)
                    .width(148.dp),
        )
        PetFormLabel(text = "치수", modifier = Modifier.padding(top = 24.dp))
        Row(modifier = Modifier.padding(top = 8.dp)) {
            PetEditNumberField(
                value = backRound,
                onValueChange = { backRound = it },
                hint = "등",
                unit = "cm",
                showEmptyError = false,
                modifier = Modifier.weight(1f),
            )
            PetEditNumberField(
                value = chestRound,
                onValueChange = { chestRound = it },
                hint = "가슴둘레",
                unit = "cm",
                showEmptyError = false,
                modifier =
                    Modifier
                        .padding(start = 8.dp)
                        .width(117.dp),
            )
            PetEditNumberField(
                value = neckRound,
                onValueChange = { neckRound = it },
                hint = "목둘레",
                unit = "cm",
                showEmptyError = false,
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

@Composable
private fun PetEditTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier,
) {
    val showError = value.isEmpty()
    var fieldModifier =
        Modifier
            .fillMaxWidth()
            .height(45.dp)
            .background(Gray1, RoundedCornerShape(5.dp))
    if (showError) {
        fieldModifier = fieldModifier.border(1.dp, Sub1, RoundedCornerShape(5.dp))
    }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = SemobanTypography.body2Medium.copy(color = Black),
        singleLine = true,
        modifier = modifier.fillMaxWidth(),
        decorationBox = { innerTextField ->
            Box(
                modifier = fieldModifier.padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = if (showError) "필수 입력 값입니다" else hint,
                        style = SemobanTypography.body2Medium,
                        color = if (showError) Sub1 else Gray4,
                    )
                }
                innerTextField()
            }
        },
    )
}

@Composable
private fun PetEditClickBox(
    value: String,
    hint: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val showError = value.isEmpty()
    var boxModifier =
        modifier
            .fillMaxWidth()
            .height(45.dp)
            .background(Gray1, RoundedCornerShape(5.dp))
    if (showError) {
        boxModifier = boxModifier.border(1.dp, Sub1, RoundedCornerShape(5.dp))
    }
    Box(
        modifier =
            boxModifier
                .clickable { onClick() }
                .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = if (showError) "필수 입력 값입니다" else value,
            style = SemobanTypography.body2Medium,
            color = if (showError) Sub1 else Black,
        )
    }
}

@Composable
private fun PetEditNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    unit: String,
    modifier: Modifier = Modifier,
    showEmptyError: Boolean = true,
) {
    val showError = showEmptyError && value.isEmpty()
    var fieldModifier =
        modifier
            .height(46.dp)
            .background(Gray1, RoundedCornerShape(5.dp))
    if (showError) {
        fieldModifier = fieldModifier.border(1.dp, Sub1, RoundedCornerShape(5.dp))
    }
    Row(
        modifier = fieldModifier.padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle =
                SemobanTypography.body2Medium.copy(
                    color = Black,
                    textAlign = TextAlign.End,
                ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.weight(1f),
            decorationBox = { innerTextField ->
                Box(contentAlignment = Alignment.CenterEnd) {
                    if (value.isEmpty()) {
                        Text(
                            text = if (showError) "필수 입력 값입니다" else hint,
                            style = SemobanTypography.body2Medium,
                            color = if (showError) Sub1 else Gray4,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    innerTextField()
                }
            },
        )
        Text(
            text = unit,
            style = SemobanTypography.body1Regular,
            color = Gray4,
            modifier = Modifier.padding(start = 4.dp),
        )
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
