package com.project.meongcare.info.view

import android.widget.ImageView
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Glide
import com.project.meongcare.R
import com.project.meongcare.designsystem.theme.Black30
import com.project.meongcare.designsystem.theme.Gray2
import com.project.meongcare.designsystem.theme.Gray4
import com.project.meongcare.designsystem.theme.Gray5
import com.project.meongcare.designsystem.theme.Main4
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.White
import com.project.meongcare.onboarding.model.entities.Gender

internal val ChipCheckedBackground = Color(0xFFFFF9F3)
internal val ChipUncheckedBackground = Color(0xFFF8F8F8)

@Composable
internal fun InfoTopBar(
    title: String? = null,
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
                contentDescription = "뒤로가기",
            )
        }
        if (title != null) {
            Text(
                text = title,
                style = SemobanTypography.title2SemiBold,
                modifier = Modifier.padding(start = 8.dp),
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
internal fun InfoGlideImage(
    model: Any?,
    errorRes: Int,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            ImageView(context).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
        },
        update = { imageView ->
            Glide.with(imageView)
                .load(model)
                .error(errorRes)
                .into(imageView)
        },
    )
}

@Composable
internal fun InfoCircleImage(
    model: Any?,
    errorRes: Int,
    size: Dp,
    modifier: Modifier = Modifier,
) {
    InfoGlideImage(
        model = model,
        errorRes = errorRes,
        modifier =
            modifier
                .size(size)
                .clip(CircleShape)
                .border(2.dp, White, CircleShape),
    )
}

@Composable
internal fun InfoConfirmDialog(
    title: String,
    confirmText: String,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    subtitle: String? = null,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Black30)
                .clickable { onCancel() },
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable(enabled = false) {},
            color = White,
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(modifier = Modifier.padding(vertical = 24.dp)) {
                Text(
                    text = title,
                    style = SemobanTypography.title3SemiBold,
                    modifier = Modifier.padding(start = 24.dp),
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = SemobanTypography.body2Regular,
                        color = Gray5,
                        modifier = Modifier.padding(start = 24.dp, top = 4.dp),
                    )
                }
                Row(
                    modifier =
                        Modifier
                            .align(Alignment.End)
                            .padding(top = 28.dp, end = 24.dp),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .width(96.dp)
                                .height(38.dp)
                                .background(Gray2, RoundedCornerShape(5.dp))
                                .clickable { onCancel() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "취소",
                            style = SemobanTypography.bottom2SemiBold,
                            color = Gray5,
                        )
                    }
                    Box(
                        modifier =
                            Modifier
                                .padding(start = 8.dp)
                                .width(96.dp)
                                .height(38.dp)
                                .background(Main4, RoundedCornerShape(5.dp))
                                .clickable { onConfirm() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = confirmText,
                            style = SemobanTypography.bottom2SemiBold,
                            color = White,
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun DeleteDialogOverlay(
    onCancel: () -> Unit,
    onDelete: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Black30)
                .clickable { onCancel() },
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable(enabled = false) {},
            color = White,
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(modifier = Modifier.padding(vertical = 20.dp)) {
                Text(
                    text = "삭제하시겠습니까?",
                    style = SemobanTypography.body1Medium,
                    modifier = Modifier.padding(start = 25.dp),
                )
                Text(
                    text = "삭제를 누르면 복구할 수 없습니다.",
                    style = SemobanTypography.body2Regular,
                    color = Gray4,
                    modifier = Modifier.padding(start = 25.dp, top = 4.dp),
                )
                Row(
                    modifier =
                        Modifier
                            .align(Alignment.End)
                            .padding(top = 30.dp, end = 25.dp),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .height(38.dp)
                                .background(Gray2, RoundedCornerShape(5.dp))
                                .clickable { onCancel() }
                                .padding(horizontal = 32.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "취소",
                            style = SemobanTypography.bottom2SemiBold,
                            color = Gray5,
                        )
                    }
                    Box(
                        modifier =
                            Modifier
                                .padding(start = 8.dp)
                                .height(38.dp)
                                .background(Main4, RoundedCornerShape(5.dp))
                                .clickable { onDelete() }
                                .padding(horizontal = 32.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "삭제",
                            style = SemobanTypography.bottom2SemiBold,
                            color = White,
                        )
                    }
                }
            }
        }
    }
}

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
            text = if (gender == Gender.FEMALE) "여아" else "남아",
            style = SemobanTypography.body1Medium,
            color = if (isSelected) Main4 else Gray4,
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
            contentDescription = if (checked) "중성화 함" else "중성화 안 함",
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = "중성화\n여부",
            style = SemobanTypography.body1Medium,
            color = Color(0xFF4B4A4A),
            modifier = Modifier.padding(start = 7.dp),
        )
    }
}
