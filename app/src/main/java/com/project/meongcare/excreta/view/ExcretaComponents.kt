package com.project.meongcare.excreta.view

import android.view.LayoutInflater
import android.widget.TimePicker
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.project.meongcare.R
import com.project.meongcare.designsystem.component.GlideImage
import com.project.meongcare.designsystem.theme.Gray1
import com.project.meongcare.designsystem.theme.Gray2
import com.project.meongcare.designsystem.theme.Gray3
import com.project.meongcare.designsystem.theme.Gray4
import com.project.meongcare.designsystem.theme.Gray5
import com.project.meongcare.designsystem.theme.Main4
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.Sub1
import com.project.meongcare.designsystem.theme.White
import com.project.meongcare.excreta.model.entities.Excreta

@Composable
internal fun ExcretaTopBar(
    title: String? = null,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(56.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.all_arrow_back_18dp),
                    contentDescription = stringResource(R.string.all_back),
                )
            }
        }
        if (title != null) {
            Text(
                text = title,
                style = SemobanTypography.title3SemiBold,
                modifier = Modifier.padding(start = if (onBack != null) 8.dp else 24.dp),
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
    style: TextStyle = SemobanTypography.body1SemiBold,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = text, style = style)
        Image(
            painter = painterResource(R.drawable.essential_input_element_icon),
            contentDescription = stringResource(R.string.all_required_input_description),
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}

@Composable
internal fun ExcretaCheckIcon(
    checked: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
) {
    Image(
        painter =
            painterResource(
                if (checked) R.drawable.all_check_24dp else R.drawable.all_un_check_16dp,
            ),
        contentDescription = stringResource(if (checked) R.string.all_checked else R.string.all_unchecked),
        modifier = modifier.size(size),
    )
}

@Composable
internal fun ExcretaTypeSelector(
    excretaType: Excreta?,
    modifier: Modifier = Modifier,
    showEssential: Boolean = true,
    onTypeChange: ((Excreta) -> Unit)? = null,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = stringResource(R.string.excreta_type_label), style = SemobanTypography.title3SemiBold)
        if (showEssential) {
            Image(
                painter = painterResource(R.drawable.essential_input_element_icon),
                contentDescription = stringResource(R.string.all_required_input_description),
                modifier = Modifier.padding(start = 8.dp),
            )
        }
        ExcretaTypeItem(
            label = stringResource(Excreta.URINE.labelRes),
            checked = excretaType == Excreta.URINE,
            onClick = onTypeChange?.let { { it(Excreta.URINE) } },
            modifier = Modifier.padding(start = if (showEssential) 13.dp else 24.dp),
        )
        ExcretaTypeItem(
            label = stringResource(Excreta.FECES.labelRes),
            checked = excretaType == Excreta.FECES,
            onClick = onTypeChange?.let { { it(Excreta.FECES) } },
            modifier = Modifier.padding(start = 24.dp),
        )
    }
}

@Composable
private fun ExcretaTypeItem(
    label: String,
    checked: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    var rowModifier = modifier
    if (onClick != null) {
        rowModifier = rowModifier.clickable { onClick() }
    }
    Row(
        modifier = rowModifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ExcretaCheckIcon(checked = checked)
        Text(
            text = label,
            style = SemobanTypography.body1Medium,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}

@Composable
internal fun ExcretaValueBox(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = SemobanTypography.body1Medium,
        modifier =
            modifier
                .fillMaxWidth()
                .background(Gray1, RoundedCornerShape(5.dp))
                .padding(horizontal = 16.dp, vertical = 13.dp),
    )
}

@Composable
internal fun ExcretaDateBox(
    dateText: String?,
    isError: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var boxModifier =
        modifier
            .fillMaxWidth()
            .background(Gray1, RoundedCornerShape(5.dp))
    if (isError) {
        boxModifier = boxModifier.border(1.dp, Sub1, RoundedCornerShape(5.dp))
    }
    Row(
        modifier =
            boxModifier
                .clickable { onClick() }
                .padding(start = 17.dp, end = 16.dp, top = 14.dp, bottom = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val (text, style, color) =
            when {
                isError -> Triple(stringResource(R.string.designsystem_required_input), SemobanTypography.body1Regular, Sub1)
                dateText != null -> Triple(dateText, SemobanTypography.body1Medium, SemobanTypography.body1Medium.color)
                else -> Triple(stringResource(R.string.all_select_date), SemobanTypography.body1Regular, Gray4)
            }
        Text(
            text = text,
            style = style,
            color = color,
            modifier = Modifier.weight(1f),
        )
        Image(
            painter = painterResource(R.drawable.all_calendar),
            contentDescription = null,
        )
    }
}

@Composable
internal fun ExcretaRecordItem(
    typeText: String,
    timeText: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    var rowModifier =
        modifier
            .fillMaxWidth()
            .border(1.dp, Gray3, RoundedCornerShape(10.dp))
    if (onClick != null) {
        rowModifier = rowModifier.clickable { onClick() }
    }
    Row(
        modifier = rowModifier.padding(start = 20.dp, end = 8.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = typeText,
            style = SemobanTypography.body1Medium,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = timeText,
            style = SemobanTypography.body2Regular,
            modifier =
                Modifier
                    .background(Gray2, RoundedCornerShape(5.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

@Composable
internal fun ExcretaImageCard(
    imageModel: Any?,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    placeholderText: String? = null,
    onClick: (() -> Unit)? = null,
    overlay: @Composable (BoxScope.() -> Unit)? = null,
) {
    var boxModifier = modifier.background(Gray2, RoundedCornerShape(30.dp))
    if (onClick != null) {
        boxModifier = boxModifier.clickable { onClick() }
    }
    Box(modifier = boxModifier) {
        Column(
            modifier =
                Modifier
                    .padding(contentPadding)
                    .alpha(if (imageModel == null) 1f else 0f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(R.drawable.excreta_feces),
                contentDescription = null,
            )
            if (placeholderText != null) {
                Text(
                    text = placeholderText,
                    style = SemobanTypography.body3Regular,
                    color = Gray5,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
        if (imageModel != null) {
            GlideImage(
                model = imageModel,
                modifier =
                    Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(30.dp)),
            )
        }
        overlay?.let { it() }
    }
}

@Composable
internal fun ExcretaCompleteButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .background(Main4, RoundedCornerShape(5.dp))
                .clickable { onClick() }
                .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.all_completion),
            style = SemobanTypography.bottom1SemiBold,
            color = White,
        )
    }
}

@Composable
internal fun ExcretaCancelDeleteButtons(
    onCancel: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .background(White, RoundedCornerShape(4.dp))
                    .border(1.dp, Gray3, RoundedCornerShape(4.dp))
                    .clickable { onCancel() }
                    .padding(vertical = 12.dp),
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
                    .padding(start = 8.dp)
                    .background(Main4, RoundedCornerShape(4.dp))
                    .clickable { onDelete() }
                    .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.all_delete),
                style = SemobanTypography.bottom1SemiBold,
                color = White,
            )
        }
    }
}

@Composable
internal fun SpinnerTimePicker(
    initialHour: Int?,
    initialMinute: Int?,
    onTimeChanged: (hour: Int, minute: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            val timePicker =
                LayoutInflater.from(context)
                    .inflate(R.layout.view_spinner_time_picker, null) as TimePicker
            if (initialHour != null && initialMinute != null) {
                timePicker.hour = initialHour
                timePicker.minute = initialMinute
            }
            timePicker.setOnTimeChangedListener { _, hour, minute ->
                onTimeChanged(hour, minute)
            }
            timePicker
        },
    )
}
