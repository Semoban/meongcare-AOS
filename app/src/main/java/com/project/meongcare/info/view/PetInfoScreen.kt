package com.project.meongcare.info.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.meongcare.R
import com.project.meongcare.designsystem.component.DeleteDialogOverlay
import com.project.meongcare.designsystem.component.GenderChip
import com.project.meongcare.designsystem.component.GlideImage
import com.project.meongcare.designsystem.component.NeuterCheckbox
import com.project.meongcare.designsystem.theme.Black
import com.project.meongcare.designsystem.theme.Gray1
import com.project.meongcare.designsystem.theme.Gray4
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.White
import com.project.meongcare.info.model.entities.GetDogInfoResponse
import com.project.meongcare.onboarding.model.entities.Gender
import com.project.meongcare.onboarding.util.DogAddOnBoardingDateUtils.dateFormat

@Composable
fun PetInfoScreen(
    dogInfo: GetDogInfoResponse?,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
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
                        contentDescription = stringResource(R.string.all_back),
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onEditClick) {
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
            GlideImage(
                model = dogInfo?.imageUrl,
                errorRes = R.drawable.dog_profile_default,
                centerCrop = true,
                modifier =
                    Modifier
                        .padding(top = 42.dp)
                        .align(Alignment.CenterHorizontally)
                        .width(250.dp)
                        .height(151.dp)
                        .clip(RoundedCornerShape(30.dp)),
            )
            PetFormLabel(text = stringResource(R.string.petadd_name), modifier = Modifier.padding(top = 32.dp))
            PetFormValueBox(
                value = dogInfo?.name.orEmpty(),
                modifier = Modifier.padding(top = 8.dp),
            )
            PetFormLabel(text = stringResource(R.string.petadd_breed), modifier = Modifier.padding(top = 24.dp))
            PetFormValueBox(
                value = dogInfo?.type.orEmpty(),
                modifier = Modifier.padding(top = 8.dp),
            )
            PetFormLabel(text = stringResource(R.string.petadd_gender), modifier = Modifier.padding(top = 24.dp))
            Row(
                modifier = Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.Top,
            ) {
                GenderChip(
                    gender = Gender.FEMALE,
                    isSelected = dogInfo?.sex == Gender.FEMALE.english,
                )
                GenderChip(
                    gender = Gender.MALE,
                    isSelected = dogInfo?.sex == Gender.MALE.english,
                    modifier = Modifier.padding(start = 8.dp),
                )
                NeuterCheckbox(
                    checked = dogInfo?.castrate == true,
                    modifier = Modifier.padding(start = 8.dp, top = 7.dp),
                )
            }
            PetFormLabel(text = stringResource(R.string.petadd_birthday_label), modifier = Modifier.padding(top = 24.dp))
            PetFormValueBox(
                value = dogInfo?.birthDate?.let { dateFormat(it) }.orEmpty(),
                modifier = Modifier.padding(top = 4.dp),
            )
            PetFormLabel(text = stringResource(R.string.petadd_weight), modifier = Modifier.padding(top = 24.dp))
            Row(
                modifier =
                    Modifier
                        .padding(top = 4.dp)
                        .width(148.dp)
                        .height(46.dp)
                        .background(Gray1, RoundedCornerShape(5.dp))
                        .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = dogInfo?.weight?.toString().orEmpty(),
                    style = SemobanTypography.body2Medium,
                    color = Black,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "Kg",
                    style = SemobanTypography.body1Regular,
                    color = Gray4,
                    modifier = Modifier.padding(start = 4.dp),
                )
            }
            PetFormLabel(text = stringResource(R.string.petadd_size), modifier = Modifier.padding(top = 24.dp))
            Row(modifier = Modifier.padding(top = 8.dp, bottom = 53.dp)) {
                PetSizeValueBox(
                    value = formatBodySize(dogInfo?.backRound),
                    hint = stringResource(R.string.petadd_back_length),
                    modifier = Modifier.weight(1f),
                )
                PetSizeValueBox(
                    value = formatBodySize(dogInfo?.chestRound),
                    hint = stringResource(R.string.petadd_chest_circumference),
                    modifier =
                        Modifier
                            .padding(start = 8.dp)
                            .width(117.dp),
                )
                PetSizeValueBox(
                    value = formatBodySize(dogInfo?.neckRound),
                    hint = stringResource(R.string.petadd_neck_circumference),
                    modifier =
                        Modifier
                            .padding(start = 8.dp)
                            .weight(1f),
                )
            }
        }
        if (showDeleteDialog) {
            DeleteDialogOverlay(
                onCancel = { showDeleteDialog = false },
                onDelete = {
                    showDeleteDialog = false
                    onDeleteConfirm()
                },
            )
        }
    }
}

@Composable
internal fun PetFormLabel(
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
internal fun PetFormValueBox(
    value: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(45.dp)
                .background(Gray1, RoundedCornerShape(5.dp))
                .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = value,
            style = SemobanTypography.body2Medium,
            color = Black,
        )
    }
}

@Composable
private fun PetSizeValueBox(
    value: String,
    hint: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .height(46.dp)
                .background(Gray1, RoundedCornerShape(5.dp))
                .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = value.ifEmpty { hint },
            style = SemobanTypography.body2Medium,
            color = if (value.isEmpty()) Gray4 else Black,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "cm",
            style = SemobanTypography.body1Regular,
            color = Gray4,
            modifier = Modifier.padding(start = 4.dp),
        )
    }
}

internal fun formatBodySize(size: Double?): String =
    if (size == null || size == 0.0) "" else size.toString()

@Preview(showBackground = true)
@Composable
private fun PetInfoScreenPreview() {
    SemobanTheme {
        PetInfoScreen(
            dogInfo =
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
            onBackClick = {},
            onEditClick = {},
            onDeleteConfirm = {},
        )
    }
}
