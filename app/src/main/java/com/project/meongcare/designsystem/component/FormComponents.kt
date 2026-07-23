package com.project.meongcare.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.project.meongcare.R
import com.project.meongcare.designsystem.theme.Black
import com.project.meongcare.designsystem.theme.Gray1
import com.project.meongcare.designsystem.theme.Gray4
import com.project.meongcare.designsystem.theme.Main4
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.Sub1
import com.project.meongcare.onboarding.model.entities.Gender

internal val ChipCheckedBackground = Color(0xFFFFF9F3)
internal val ChipUncheckedBackground = Color(0xFFF8F8F8)

// 반려견 등록·수정 폼(PetEdit/DogAdd)에서 공유하는 입력 컴포넌트

@Composable
internal fun GenderChip(
    gender: Gender,
    isSelected: Boolean,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    var chipModifier =
        modifier
            .width(94.dp)
            .height(45.dp)
            .background(
                if (isSelected) ChipCheckedBackground else ChipUncheckedBackground,
                RoundedCornerShape(5.dp),
            )
    if (onClick != null) {
        chipModifier = chipModifier.clickable { onClick() }
    }
    Box(
        modifier = chipModifier,
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(if (gender == Gender.FEMALE) R.string.petadd_female else R.string.petadd_male),
            style = SemobanTypography.body1Medium,
            color = if (isSelected) Main4 else Gray4,
        )
    }
}

@Composable
internal fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    showError: Boolean,
    modifier: Modifier = Modifier,
) {
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
                        text = if (showError) stringResource(R.string.designsystem_required_input) else hint,
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
internal fun FormClickBox(
    value: String,
    hint: String,
    showError: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
        val text =
            when {
                showError && value.isEmpty() -> stringResource(R.string.designsystem_required_input)
                value.isEmpty() -> hint
                else -> value
            }
        Text(
            text = text,
            style = SemobanTypography.body2Medium,
            color =
                when {
                    showError && value.isEmpty() -> Sub1
                    value.isEmpty() -> Gray4
                    else -> Black
                },
        )
    }
}

@Composable
internal fun FormNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    unit: String,
    showError: Boolean,
    modifier: Modifier = Modifier,
) {
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
                            text = if (showError) stringResource(R.string.designsystem_required_input) else hint,
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

@Composable
internal fun NeuterCheckbox(
    checked: Boolean,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    var rowModifier = modifier
    if (onClick != null) {
        rowModifier = rowModifier.clickable { onClick() }
    }
    Row(
        modifier = rowModifier,
        verticalAlignment = Alignment.Top,
    ) {
        Image(
            painter =
                painterResource(
                    if (checked) R.drawable.all_check_24dp else R.drawable.all_un_check_16dp,
                ),
            contentDescription = stringResource(if (checked) R.string.designsystem_neuter_checked else R.string.designsystem_neuter_unchecked),
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = stringResource(R.string.petadd_neuter_status),
            style = SemobanTypography.body1Medium,
            color = Color(0xFF4B4A4A),
            modifier = Modifier.padding(start = 7.dp),
        )
    }
}
