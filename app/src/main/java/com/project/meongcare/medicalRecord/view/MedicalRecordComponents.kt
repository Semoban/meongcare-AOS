package com.project.meongcare.medicalRecord.view

import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.bumptech.glide.Glide
import com.project.meongcare.designsystem.theme.Gray2
import com.project.meongcare.designsystem.theme.Gray4
import com.project.meongcare.designsystem.theme.Gray5
import com.project.meongcare.designsystem.theme.Main4
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.White

@Composable
internal fun MedicalRecordGlideImage(
    model: Any,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        modifier = modifier,
        factory = { context -> ImageView(context) },
        update = { imageView ->
            Glide.with(imageView)
                .load(model)
                .into(imageView)
        },
    )
}

@Composable
internal fun MedicalRecordDeleteDialog(
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    Dialog(onDismissRequest = onCancel) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
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
                    modifier = Modifier.padding(start = 25.dp, top = 4.dp, end = 25.dp),
                )
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 30.dp, end = 25.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    MedicalRecordDialogButton(
                        text = "취소",
                        backgroundColor = Gray2,
                        textColor = Gray5,
                        onClick = onCancel,
                    )
                    MedicalRecordDialogButton(
                        text = "삭제",
                        backgroundColor = Main4,
                        textColor = White,
                        onClick = onConfirm,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun MedicalRecordDialogButton(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = SemobanTypography.bottom2SemiBold,
        color = textColor,
        modifier =
            modifier
                .background(backgroundColor, RoundedCornerShape(5.dp))
                .clickable { onClick() }
                .padding(horizontal = 32.dp, vertical = 7.dp),
    )
}
