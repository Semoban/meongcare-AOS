package com.project.meongcare.info.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.meongcare.R
import com.project.meongcare.designsystem.component.CircleGlideImage
import com.project.meongcare.designsystem.component.ConfirmDialog
import com.project.meongcare.designsystem.theme.Black
import com.project.meongcare.designsystem.theme.Black30
import com.project.meongcare.designsystem.theme.Gray1
import com.project.meongcare.designsystem.theme.Gray2
import com.project.meongcare.designsystem.theme.Gray3
import com.project.meongcare.designsystem.theme.Main4
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.White
import com.project.meongcare.home.model.entities.DogProfile

@Composable
fun ProfileScreen(
    email: String?,
    profileImageUrl: String?,
    dogs: List<DogProfile>,
    onBackClick: () -> Unit,
    onProfileImageClick: () -> Unit,
    onDogClick: (Long) -> Unit,
    onSettingClick: () -> Unit,
    onLogoutConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(White),
        ) {
            InfoTopBar(
                title = stringResource(R.string.profile_header),
                onBack = onBackClick,
            )
            CircleGlideImage(
                model = profileImageUrl,
                errorRes = R.drawable.profile_default_image,
                size = 89.dp,
                modifier =
                    Modifier
                        .padding(top = 32.dp)
                        .align(Alignment.CenterHorizontally)
                        .clickable { onProfileImageClick() },
            )
            Text(
                text = email.orEmpty(),
                style = SemobanTypography.body2Medium,
                modifier =
                    Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.CenterHorizontally),
            )
            Text(
                text = stringResource(R.string.profile_pet_list),
                style = SemobanTypography.title3SemiBold,
                modifier = Modifier.padding(start = 16.dp, top = 32.dp),
            )
            if (dogs.isEmpty()) {
                Text(
                    text = stringResource(R.string.profile_pet_list_empty),
                    style = SemobanTypography.body2Medium,
                    color = Black,
                    textAlign = TextAlign.Center,
                    modifier =
                        Modifier
                            .padding(top = 24.dp, bottom = 10.dp)
                            .width(278.dp)
                            .align(Alignment.CenterHorizontally)
                            .background(Gray2, RoundedCornerShape(11.dp))
                            .padding(vertical = 13.dp),
                )
            } else {
                LazyRow(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp, bottom = 10.dp),
                    contentPadding = PaddingValues(horizontal = 36.dp),
                ) {
                    items(dogs) { dog ->
                        ProfileDogItem(
                            dog = dog,
                            onClick = { onDogClick(dog.dogId) },
                            modifier = Modifier.padding(end = 8.dp),
                        )
                    }
                }
            }
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp)
                        .height(4.dp)
                        .background(Color(0xFFF8F8F8)),
            )
            ProfileMenuRow(text = stringResource(R.string.all_share), onClick = { showShareDialog = true })
            ProfileMenuRow(text = stringResource(R.string.all_setting), onClick = onSettingClick)
            ProfileMenuRow(text = stringResource(R.string.profile_logout), onClick = { showLogoutDialog = true })
        }
        if (showLogoutDialog) {
            ConfirmDialog(
                title = stringResource(R.string.profile_logout_dialog_title),
                confirmText = stringResource(R.string.profile_logout),
                onCancel = { showLogoutDialog = false },
                onConfirm = {
                    showLogoutDialog = false
                    onLogoutConfirm()
                },
            )
        }
        if (showShareDialog) {
            ProfileUpdateExpectedDialog(onConfirm = { showShareDialog = false })
        }
    }
}

@Composable
private fun ProfileMenuRow(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(horizontal = 31.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = SemobanTypography.body1Medium,
            modifier = Modifier.weight(1f),
        )
        Image(
            painter = painterResource(R.drawable.all_arrow_forward),
            contentDescription = null,
            colorFilter = ColorFilter.tint(Gray3),
        )
    }
}

@Composable
private fun ProfileDogItem(
    dog: DogProfile,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier =
            modifier
                .width(79.dp)
                .height(100.dp)
                .clickable { onClick() },
        color = Gray1,
        shape = RoundedCornerShape(11.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircleGlideImage(
                model = dog.imageUrl,
                errorRes = R.drawable.home_dog_default,
                size = 45.dp,
                modifier = Modifier.padding(top = 16.dp),
            )
            Text(
                text = dog.name,
                style = SemobanTypography.body3Medium,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun ProfileUpdateExpectedDialog(onConfirm: () -> Unit) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Black30)
                .clickable { onConfirm() },
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
            Column(modifier = Modifier.padding(vertical = 28.dp)) {
                Text(
                    text = stringResource(R.string.all_coming_soon),
                    style = SemobanTypography.title3SemiBold,
                    modifier = Modifier.padding(start = 25.dp),
                )
                Box(
                    modifier =
                        Modifier
                            .align(Alignment.End)
                            .padding(top = 24.dp, end = 24.dp)
                            .size(width = 93.dp, height = 38.dp)
                            .background(Main4, RoundedCornerShape(5.dp))
                            .clickable { onConfirm() },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.all_confirm),
                        style = SemobanTypography.bottom2SemiBold,
                        color = White,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    SemobanTheme {
        ProfileScreen(
            email = "semoban@meongcare.com",
            profileImageUrl = null,
            dogs =
                listOf(
                    DogProfile(1, "몽실이", ""),
                    DogProfile(2, "먼지", ""),
                ),
            onBackClick = {},
            onProfileImageClick = {},
            onDogClick = {},
            onSettingClick = {},
            onLogoutConfirm = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenEmptyPreview() {
    SemobanTheme {
        ProfileScreen(
            email = "semoban@meongcare.com",
            profileImageUrl = null,
            dogs = emptyList(),
            onBackClick = {},
            onProfileImageClick = {},
            onDogClick = {},
            onSettingClick = {},
            onLogoutConfirm = {},
        )
    }
}
