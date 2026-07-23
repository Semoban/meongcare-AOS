package com.project.meongcare.info.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.meongcare.R
import com.project.meongcare.designsystem.component.ConfirmDialog
import com.project.meongcare.designsystem.theme.Gray3
import com.project.meongcare.designsystem.theme.Main4
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.White

@Composable
fun SettingScreen(
    pushAgreement: Boolean,
    onBackClick: () -> Unit,
    onPushToggle: (Boolean) -> Unit,
    onDeleteAccountConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(White),
        ) {
            InfoTopBar(
                title = stringResource(R.string.all_setting),
                onBack = onBackClick,
            )
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 38.dp)
                        .height(54.dp)
                        .clickable { onPushToggle(!pushAgreement) }
                        .padding(start = 44.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.setting_notification),
                    style = SemobanTypography.body1Medium,
                    modifier = Modifier.weight(1f),
                )
                Switch(
                    checked = pushAgreement,
                    onCheckedChange = onPushToggle,
                    colors =
                        SwitchDefaults.colors(
                            checkedThumbColor = White,
                            checkedTrackColor = Main4,
                            uncheckedThumbColor = White,
                            uncheckedTrackColor = Gray3,
                            uncheckedBorderColor = Gray3,
                        ),
                )
            }
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(1.dp)
                        .background(Gray3),
            )
            Text(
                text = stringResource(R.string.setting_membership_withdrawal),
                style = SemobanTypography.body1Medium,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable { showDeleteAccountDialog = true }
                        .padding(start = 44.dp, top = 18.dp, bottom = 18.dp),
            )
        }
        if (showDeleteAccountDialog) {
            ConfirmDialog(
                title = stringResource(R.string.setting_withdrawal_dialog_title),
                subtitle = stringResource(R.string.setting_withdrawal_dialog_subtitle),
                confirmText = stringResource(R.string.setting_withdrawal_confirm),
                onCancel = { showDeleteAccountDialog = false },
                onConfirm = {
                    showDeleteAccountDialog = false
                    onDeleteAccountConfirm()
                },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingScreenPreview() {
    SemobanTheme {
        SettingScreen(
            pushAgreement = true,
            onBackClick = {},
            onPushToggle = {},
            onDeleteAccountConfirm = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingScreenOffPreview() {
    SemobanTheme {
        SettingScreen(
            pushAgreement = false,
            onBackClick = {},
            onPushToggle = {},
            onDeleteAccountConfirm = {},
        )
    }
}
