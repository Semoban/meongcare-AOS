package com.project.meongcare.supplement.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.project.meongcare.R
import com.project.meongcare.designsystem.component.GlideImage
import com.project.meongcare.designsystem.theme.Gray1
import com.project.meongcare.designsystem.theme.Gray2
import com.project.meongcare.designsystem.theme.Gray3
import com.project.meongcare.designsystem.theme.Gray4
import com.project.meongcare.designsystem.theme.Gray5
import com.project.meongcare.designsystem.theme.Main1
import com.project.meongcare.designsystem.theme.Main3
import com.project.meongcare.designsystem.theme.Main4
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.White
import com.project.meongcare.supplement.model.entities.IntakeInfo
import com.project.meongcare.supplement.utils.SupplementUtils.Companion.convertDateToTime

@Composable
internal fun SupplementTopBar(
    onBack: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(56.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                painter = painterResource(R.drawable.all_arrow_back_18dp),
                contentDescription = stringResource(R.string.all_back),
            )
        }
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            content = actions,
        )
    }
}

@Composable
internal fun EssentialLabel(
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
            contentDescription = stringResource(R.string.all_required_input_description),
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}

@Composable
internal fun SupplementImageCard(
    imageModel: Any?,
    description: String? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    var boxModifier =
        modifier
            .fillMaxWidth()
            .padding(horizontal = 55.dp)
            .height(151.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(Gray2)
    if (onClick != null) {
        boxModifier = boxModifier.clickable { onClick() }
    }
    Box(
        modifier = boxModifier,
        contentAlignment = Alignment.Center,
    ) {
        if (imageModel == null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(R.drawable.supplement_img_default),
                    contentDescription = null,
                )
                if (description != null) {
                    Text(
                        text = description,
                        style = SemobanTypography.body3Regular,
                        color = Gray5,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
        } else {
            GlideImage(
                model = imageModel,
                centerCrop = true,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
internal fun SupplementUnitSelector(
    selectedUnit: String?,
    onUnitSelect: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        SUPPLEMENT_UNITS.forEach { unit ->
            SupplementUnitButton(
                text = unit,
                isSelected = unit == selectedUnit,
                onClick = onUnitSelect?.let { { it(unit) } },
                modifier = Modifier.padding(start = 4.dp),
            )
        }
    }
}

@Composable
private fun SupplementUnitButton(
    text: String,
    isSelected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    var boxModifier =
        modifier
            .width(47.dp)
            .height(31.dp)
            .background(
                if (isSelected) Main1 else White,
                RoundedCornerShape(5.dp),
            )
            .border(
                1.dp,
                if (isSelected) Main3 else Gray3,
                RoundedCornerShape(5.dp),
            )
    if (onClick != null) {
        boxModifier = boxModifier.clickable { onClick() }
    }
    Box(
        modifier = boxModifier,
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = SemobanTypography.body2Medium,
            color = if (isSelected) Main4 else Gray4,
        )
    }
}

@Composable
internal fun SupplementIntakeCountBadge(
    count: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .background(Main1, RoundedCornerShape(5.dp))
                .padding(horizontal = 9.dp),
    ) {
        Text(
            text = stringResource(R.string.supplement_count_format, count),
            style = SemobanTypography.body1Medium,
            color = Main4,
        )
    }
}

@Composable
internal fun SupplementIntakeTimeItem(
    intakeInfo: IntakeInfo,
    intakeUnit: String,
    showMinus: Boolean = false,
    onMinusClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showMinus) {
            Image(
                painter = painterResource(R.drawable.nutrition_filled_minus),
                contentDescription = stringResource(R.string.all_delete),
                modifier =
                    Modifier
                        .padding(end = 6.dp)
                        .clickable { onMinusClick?.invoke() },
            )
        }
        Row(
            modifier =
                Modifier
                    .weight(1f)
                    .height(43.dp)
                    .background(Gray1, RoundedCornerShape(5.dp))
                    .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = convertDateToTime(intakeInfo.intakeTime),
                style = SemobanTypography.body1Medium,
                color = Gray4,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "${intakeInfo.intakeCount}$intakeUnit",
                style = SemobanTypography.body2Regular,
                color = Gray5,
            )
        }
    }
}

@Composable
internal fun SupplementSectionDivider(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 23.dp)
                .height(2.dp)
                .background(Gray2),
    )
}

@Composable
internal fun CheckToggleIcon(
    checked: Boolean,
    modifier: Modifier = Modifier,
) {
    Image(
        painter =
            painterResource(
                if (checked) R.drawable.all_check_20dp else R.drawable.all_un_check_20dp,
            ),
        contentDescription = stringResource(if (checked) R.string.all_checked else R.string.all_unchecked),
        modifier = modifier.size(20.dp),
    )
}

@Composable
internal fun SupplementNoData(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.all_paws),
                contentDescription = null,
            )
            Text(
                text = stringResource(R.string.supplement_empty),
                style = SemobanTypography.body1Medium,
                color = Gray5,
                modifier = Modifier.padding(top = 16.dp),
            )
        }
    }
}

@Composable
internal fun CancelCompleteButtons(
    completeText: String,
    onCancel: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(45.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(White, RoundedCornerShape(4.dp))
                    .border(1.dp, Gray3, RoundedCornerShape(4.dp))
                    .clickable { onCancel() },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.all_cancel),
                style = SemobanTypography.bottom1SemiBold,
                color = Gray5,
            )
        }
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(start = 8.dp)
                    .background(Main4, RoundedCornerShape(4.dp))
                    .clickable { onComplete() },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = completeText,
                style = SemobanTypography.bottom1SemiBold,
                color = White,
            )
        }
    }
}

// 섭취 단위는 서버에 intakeUnit으로 그대로 전송·조회되므로
// 백엔드 협의(i18n ④단계) 전까지 리소스로 추출하지 않는다
internal val SUPPLEMENT_UNITS = listOf("mg", "스쿱", "정")
