package com.project.meongcare.onboarding.view

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.meongcare.R
import com.project.meongcare.designsystem.theme.Black
import com.project.meongcare.designsystem.theme.Black30
import com.project.meongcare.designsystem.theme.Gray1
import com.project.meongcare.designsystem.theme.Gray2
import com.project.meongcare.designsystem.theme.Gray4
import com.project.meongcare.designsystem.theme.Gray5
import com.project.meongcare.designsystem.theme.Main4
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.Sub1
import com.project.meongcare.designsystem.theme.White

@Composable
fun DogVarietySearchScreen(
    dogTypes: List<String>,
    onBackClick: () -> Unit,
    onQueryChange: (String) -> Unit,
    onDogTypeSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by rememberSaveable { mutableStateOf("") }
    var showInputDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(White),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(R.drawable.all_arrow_back_18dp),
                        contentDescription = stringResource(R.string.all_back),
                    )
                }
                Row(
                    modifier =
                        Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                            .background(Gray1, RoundedCornerShape(10.dp))
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(R.drawable.all_search_18dp),
                        contentDescription = null,
                    )
                    BasicTextField(
                        value = query,
                        onValueChange = {
                            query = it
                            onQueryChange(it)
                        },
                        textStyle = SemobanTypography.body1Regular.copy(color = Black),
                        singleLine = true,
                        modifier =
                            Modifier
                                .weight(1f)
                                .padding(start = 16.dp),
                        decorationBox = { innerTextField ->
                            Box {
                                if (query.isEmpty()) {
                                    Text(
                                        text = stringResource(R.string.petadd_select_breed),
                                        style = SemobanTypography.body1Regular,
                                        color = Gray4,
                                    )
                                }
                                innerTextField()
                            }
                        },
                    )
                    if (query.isNotEmpty()) {
                        Image(
                            painter = painterResource(R.drawable.pet_food_search),
                            contentDescription = stringResource(R.string.onboarding_clear_search),
                            modifier =
                                Modifier
                                    .padding(start = 16.dp)
                                    .clickable {
                                        query = ""
                                        onQueryChange("")
                                    },
                        )
                    }
                }
            }
            if (query.isNotEmpty()) {
                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                ) {
                    items(dogTypes) { dogType ->
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .clickable { onDogTypeSelect(dogType) }
                                    .padding(vertical = 11.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painter = painterResource(R.drawable.dog_search_item_icon),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                            Text(
                                text = dogType,
                                style = SemobanTypography.body1Regular,
                                color = Black,
                                maxLines = 1,
                                modifier = Modifier.padding(start = 16.dp),
                            )
                        }
                    }
                }
            }
        }
        Surface(
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp)
                    .clickable { showInputDialog = true },
            color = White,
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 4.dp,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(R.drawable.all_edit),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = stringResource(R.string.onboarding_direct_input),
                    style = SemobanTypography.bottom2SemiBold,
                    color = Black,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }
        if (showInputDialog) {
            DogTypeInputDialog(
                onCancel = { showInputDialog = false },
                onSubmit = { dogType ->
                    showInputDialog = false
                    onDogTypeSelect(dogType)
                },
            )
        }
    }
}

@Composable
private fun DogTypeInputDialog(
    onCancel: () -> Unit,
    onSubmit: (String) -> Unit,
) {
    var dogType by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

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
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = stringResource(R.string.petadd_select_breed),
                    style = SemobanTypography.title3SemiBold,
                )
                var fieldModifier =
                    Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                        .background(Gray1, RoundedCornerShape(5.dp))
                if (showError) {
                    fieldModifier = fieldModifier.border(1.dp, Sub1, RoundedCornerShape(5.dp))
                }
                Row(
                    modifier =
                        Modifier
                            .padding(top = 16.dp)
                            .then(fieldModifier)
                            .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BasicTextField(
                        value = dogType,
                        onValueChange = {
                            dogType = it
                            if (it.isNotEmpty()) {
                                showError = false
                            }
                        },
                        textStyle = SemobanTypography.body1Regular.copy(color = Black),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        decorationBox = { innerTextField ->
                            Box {
                                if (dogType.isEmpty()) {
                                    Text(
                                        text =
                                            stringResource(
                                                if (showError) R.string.onboarding_breed_input_error else R.string.onboarding_breed_input_hint,
                                            ),
                                        style = SemobanTypography.body1Regular,
                                        color = if (showError) Sub1 else Gray4,
                                    )
                                }
                                innerTextField()
                            }
                        },
                    )
                    if (dogType.isNotEmpty()) {
                        Image(
                            painter = painterResource(R.drawable.pet_food_search),
                            contentDescription = stringResource(R.string.onboarding_clear_input),
                            modifier =
                                Modifier
                                    .padding(start = 8.dp)
                                    .clickable { dogType = "" },
                        )
                    }
                }
                Row(
                    modifier =
                        Modifier
                            .align(Alignment.End)
                            .padding(top = 24.dp),
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
                            text = stringResource(R.string.all_cancel),
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
                                .clickable {
                                    if (dogType.isEmpty()) {
                                        showError = true
                                    } else {
                                        onSubmit(dogType)
                                    }
                                }
                                .padding(horizontal = 32.dp),
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
}

@Preview(showBackground = true)
@Composable
private fun DogVarietySearchScreenPreview() {
    SemobanTheme {
        DogVarietySearchScreen(
            dogTypes = listOf("말티즈", "말라뮤트", "골든 리트리버"),
            onBackClick = {},
            onQueryChange = {},
            onDogTypeSelect = {},
        )
    }
}
