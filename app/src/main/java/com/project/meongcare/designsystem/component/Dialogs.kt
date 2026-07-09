package com.project.meongcare.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.meongcare.designsystem.theme.Black30
import com.project.meongcare.designsystem.theme.Gray2
import com.project.meongcare.designsystem.theme.Gray4
import com.project.meongcare.designsystem.theme.Gray5
import com.project.meongcare.designsystem.theme.Main4
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.White

// 취소/확인 2버튼 확인 다이얼로그 오버레이 — 바깥 영역 탭 시 취소된다
@Composable
internal fun ConfirmDialog(
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

// 삭제 확인 다이얼로그 오버레이 — 바깥 영역 탭 시 취소된다
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

@Preview(showBackground = true)
@Composable
private fun ConfirmDialogPreview() {
    SemobanTheme {
        ConfirmDialog(
            title = "로그아웃 하시겠습니까?",
            confirmText = "로그아웃",
            onCancel = {},
            onConfirm = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DeleteDialogOverlayPreview() {
    SemobanTheme {
        DeleteDialogOverlay(
            onCancel = {},
            onDelete = {},
        )
    }
}
