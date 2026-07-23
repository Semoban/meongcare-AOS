package com.project.meongcare.home.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.meongcare.R
import com.project.meongcare.designsystem.component.CircleGlideImage
import com.project.meongcare.designsystem.theme.Black
import com.project.meongcare.designsystem.theme.Gray1
import com.project.meongcare.designsystem.theme.Gray2
import com.project.meongcare.designsystem.theme.Gray4
import com.project.meongcare.designsystem.theme.Gray5
import com.project.meongcare.designsystem.theme.Main1
import com.project.meongcare.designsystem.theme.Main3
import com.project.meongcare.designsystem.theme.Main4
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.Sub2
import com.project.meongcare.designsystem.theme.Sub3
import com.project.meongcare.designsystem.theme.White
import com.project.meongcare.home.model.entities.DogProfile
import com.project.meongcare.home.model.entities.Symptom
import com.project.meongcare.symptom.model.entities.SymptomType
import com.project.meongcare.symptom.utils.SymptomUtils.Companion.getSymptomTitleRes
import com.project.meongcare.toolbar.view.CalendarWeekRow
import java.util.Date

@Composable
fun HomeScreen(
    profileImageUrl: String?,
    dogs: List<DogProfile>,
    selectedDogPos: Int?,
    showDogNotExist: Boolean,
    dateList: List<Date>,
    selectedDatePos: Int?,
    symptoms: List<Symptom>,
    fecesCount: Int,
    urineCount: Int,
    supplementsRate: Int,
    weight: String,
    feedIntake: String,
    onCalendarClick: () -> Unit,
    onAlarmClick: () -> Unit,
    onProfileClick: () -> Unit,
    onDogClick: (Int) -> Unit,
    onAddDogClick: () -> Unit,
    onDateClick: (Int) -> Unit,
    onWeekSwipe: (Int) -> Unit,
    onSymptomCardClick: () -> Unit,
    onExcretaCardClick: () -> Unit,
    onSupplementCardClick: () -> Unit,
    onWeightCardClick: () -> Unit,
    onFeedCardClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Gray2),
    ) {
        HomeTopBar(
            profileImageUrl = profileImageUrl,
            onCalendarClick = onCalendarClick,
            onAlarmClick = onAlarmClick,
            onProfileClick = onProfileClick,
        )
        HomeDogSelector(
            dogs = dogs,
            selectedDogPos = selectedDogPos,
            onDogClick = onDogClick,
            onAddDogClick = onAddDogClick,
        )
        CalendarWeekRow(
            dateList = dateList,
            selectedDatePos = selectedDatePos,
            onDateClick = onDateClick,
            onWeekSwipe = onWeekSwipe,
            modifier = Modifier.background(White, RoundedCornerShape(bottomStart = 11.dp, bottomEnd = 11.dp)),
        )
        if (showDogNotExist) {
            HomeDogNotExist(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
            )
        } else {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
            ) {
                HomeSymptomCard(
                    symptoms = symptoms,
                    onClick = onSymptomCardClick,
                    modifier = Modifier.padding(top = 8.dp),
                )
                HomeExcretaCard(
                    fecesCount = fecesCount,
                    urineCount = urineCount,
                    onClick = onExcretaCardClick,
                    modifier = Modifier.padding(top = 16.dp),
                )
                Row(
                    modifier =
                        Modifier
                            .padding(top = 16.dp)
                            .height(IntrinsicSize.Min),
                ) {
                    HomeSupplementCard(
                        supplementsRate = supplementsRate,
                        onClick = onSupplementCardClick,
                        modifier = Modifier.fillMaxHeight(),
                    )
                    Column(
                        modifier =
                            Modifier
                                .padding(start = 10.dp)
                                .weight(1f),
                    ) {
                        HomeWeightCard(
                            weight = weight,
                            onClick = onWeightCardClick,
                        )
                        HomeFeedCard(
                            feedIntake = feedIntake,
                            onClick = onFeedCardClick,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(200.dp))
            }
        }
    }
}

@Composable
private fun HomeTopBar(
    profileImageUrl: String?,
    onCalendarClick: () -> Unit,
    onAlarmClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(White),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.semoban_logo),
            contentDescription = null,
            modifier =
                Modifier
                    .padding(start = 16.dp)
                    .size(width = 59.dp, height = 21.dp),
        )
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(R.drawable.all_calendar),
            contentDescription = null,
            modifier =
                Modifier
                    .size(width = 19.dp, height = 18.dp)
                    .clickable { onCalendarClick() },
        )
        Image(
            painter = painterResource(R.drawable.all_notification_18dp),
            contentDescription = null,
            modifier =
                Modifier
                    .padding(start = 20.dp)
                    .size(18.dp)
                    .clickable { onAlarmClick() },
        )
        CircleGlideImage(
            model = profileImageUrl,
            errorRes = R.drawable.home_profile_default_image,
            size = 30.dp,
            modifier =
                Modifier
                    .padding(start = 20.dp, end = 14.dp)
                    .clickable { onProfileClick() },
        )
    }
}

@Composable
private fun HomeDogSelector(
    dogs: List<DogProfile>,
    selectedDogPos: Int?,
    onDogClick: (Int) -> Unit,
    onAddDogClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier =
            modifier
                .fillMaxWidth()
                .background(White)
                .padding(16.dp),
    ) {
        itemsIndexed(dogs) { index, dog ->
            HomeDogProfileItem(
                dog = dog,
                isSelected = index == selectedDogPos,
                onClick = { onDogClick(index) },
                modifier = Modifier.padding(horizontal = 3.dp),
            )
        }
        item {
            Image(
                painter = painterResource(R.drawable.home_dog_add_45dp),
                contentDescription = null,
                modifier =
                    Modifier
                        .padding(start = 3.dp, top = 5.dp)
                        .clickable { onAddDogClick() },
            )
        }
    }
}

@Composable
private fun HomeDogProfileItem(
    dog: DogProfile,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .width(60.dp)
                .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val borderModifier =
            if (isSelected) {
                Modifier.border(1.dp, Main4, CircleShape)
            } else {
                Modifier
            }
        Box(
            modifier =
                Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(White)
                    .then(borderModifier),
            contentAlignment = Alignment.Center,
        ) {
            CircleGlideImage(
                model = dog.imageUrl,
                errorRes = R.drawable.home_dog_default,
                size = 45.dp,
            )
        }
        Text(
            text = dog.name,
            style = SemobanTypography.body3Medium,
            color = Black,
            maxLines = 1,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
private fun HomeDogNotExist(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.home_dog_icon),
            contentDescription = null,
            modifier = Modifier.size(width = 88.dp, height = 103.dp),
        )
        Text(
            text = stringResource(R.string.home_dog_not_exist),
            style = SemobanTypography.body3Medium,
            color = Gray5,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}

@Composable
private fun HomeSymptomCard(
    symptoms: List<Symptom>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable { onClick() },
        color = White,
        shape = RoundedCornerShape(10.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.home_symptom_title),
                style = SemobanTypography.title3SemiBold,
            )
            Text(
                text =
                    if (symptoms.isEmpty()) {
                        stringResource(R.string.home_symptom_not_exist)
                    } else {
                        stringResource(R.string.home_symptom_exist)
                    },
                style = SemobanTypography.body3Regular,
                color = Gray5,
                modifier = Modifier.padding(top = 1.dp),
            )
            if (symptoms.isNotEmpty()) {
                LazyRow(modifier = Modifier.padding(top = 8.dp)) {
                    items(symptoms) { symptom ->
                        HomeSymptomChip(
                            symptom = symptom,
                            modifier = Modifier.padding(4.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeSymptomChip(
    symptom: Symptom,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .background(Gray2, RoundedCornerShape(5.dp))
                .widthIn(min = 150.dp)
                .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(getSymptomImg(symptom.symptomString)),
            contentDescription = null,
            modifier =
                Modifier
                    .size(28.dp)
                    .background(White, RoundedCornerShape(3.dp))
                    .padding(3.dp),
        )
        Text(
            text = getSymptomTitleRes(symptom.symptomString)?.let { stringResource(it) } ?: symptom.note,
            style = SemobanTypography.body2Medium,
            color = Color(0xFF2B2B2B),
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 11.dp),
        )
    }
}

private fun getSymptomImg(symptomType: String): Int {
    return when (symptomType) {
        SymptomType.WEIGHT_LOSS.symptomName -> R.drawable.all_weighing_machine
        SymptomType.HIGH_FEVER.symptomName -> R.drawable.all_temperature_measurement
        SymptomType.COUGH.symptomName -> R.drawable.symptom_cough
        SymptomType.DIARRHEA.symptomName -> R.drawable.symptom_diarrhea
        SymptomType.LOSS_OF_APPETITE.symptomName -> R.drawable.symptom_loss_appetite
        SymptomType.ACTIVITY_DECREASE.symptomName -> R.drawable.symptom_amount_activity
        else -> R.drawable.symptom_etc_record
    }
}

@Composable
private fun HomeExcretaCard(
    fecesCount: Int,
    urineCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable { onClick() },
        color = White,
        shape = RoundedCornerShape(10.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.home_excreta_title),
                style = SemobanTypography.title3SemiBold,
            )
            Spacer(modifier = Modifier.weight(1f))
            HomeExcretaCountChip(
                iconRes = R.drawable.home_feces,
                label = stringResource(R.string.home_excreta_feces),
                count = fecesCount,
                containerColor = Main1,
                contentColor = Main4,
            )
            HomeExcretaCountChip(
                iconRes = R.drawable.home_urine,
                label = stringResource(R.string.home_excreta_urine),
                count = urineCount,
                containerColor = Sub2,
                contentColor = Sub3,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    }
}

@Composable
private fun HomeExcretaCountChip(
    iconRes: Int,
    label: String,
    count: Int,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .background(containerColor, RoundedCornerShape(10.dp))
                .widthIn(min = 100.dp)
                .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier =
                Modifier
                    .size(20.dp)
                    .background(White, RoundedCornerShape(5.dp))
                    .padding(3.dp),
        )
        Text(
            text = stringResource(R.string.home_excreta_count_format, label, count),
            style = SemobanTypography.body3Medium,
            color = contentColor,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}

@Composable
private fun HomeSupplementCard(
    supplementsRate: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        color = White,
        shape = RoundedCornerShape(10.dp),
    ) {
        Row {
            Column(
                modifier =
                    Modifier
                        .fillMaxHeight()
                        .padding(start = 16.dp, top = 12.dp, bottom = 20.dp),
            ) {
                Text(
                    text = stringResource(R.string.home_supplement_title),
                    style = SemobanTypography.title3SemiBold,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "$supplementsRate %",
                    style = SemobanTypography.body1Medium,
                    color = Gray5,
                )
            }
            HomeSupplementProgressBar(
                supplementsRate = supplementsRate,
                modifier =
                    Modifier
                        .align(Alignment.CenterVertically)
                        .padding(horizontal = 8.dp),
            )
        }
    }
}

@Composable
private fun HomeSupplementProgressBar(
    supplementsRate: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(width = 78.dp, height = 33.dp)
                .graphicsLayer { rotationZ = -58.819f },
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Gray1, RoundedCornerShape(16.5.dp)),
        )
        Box(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth((supplementsRate / 100f).coerceIn(0f, 1f))
                    .background(Main3, RoundedCornerShape(16.5.dp)),
        )
    }
}

@Composable
private fun HomeWeightCard(
    weight: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable { onClick() },
        color = White,
        shape = RoundedCornerShape(10.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.home_weight_title),
                style = SemobanTypography.title3SemiBold,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "$weight kg",
                style = SemobanTypography.body1Medium,
                color = Gray5,
            )
        }
    }
}

@Composable
private fun HomeFeedCard(
    feedIntake: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable { onClick() },
        color = White,
        shape = RoundedCornerShape(10.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            Column {
                Text(
                    text = stringResource(R.string.home_feed_daily_intake),
                    style = SemobanTypography.body3Regular,
                    color = Gray4,
                )
                Text(
                    text = stringResource(R.string.home_feed_title),
                    style = SemobanTypography.title3SemiBold,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "$feedIntake g",
                style = SemobanTypography.body1Medium,
                color = Gray5,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    SemobanTheme {
        HomeScreen(
            profileImageUrl = null,
            dogs =
                listOf(
                    DogProfile(1, "몽실이", ""),
                    DogProfile(2, "먼지", ""),
                ),
            selectedDogPos = 0,
            showDogNotExist = false,
            dateList = List(7) { Date(it * 86_400_000L) },
            selectedDatePos = 3,
            symptoms =
                listOf(
                    Symptom("체중 감소", "체중이 감소한다"),
                    Symptom("기침", "기침을 한다"),
                ),
            fecesCount = 1,
            urineCount = 2,
            supplementsRate = 60,
            weight = "5.5",
            feedIntake = "300",
            onCalendarClick = {},
            onAlarmClick = {},
            onProfileClick = {},
            onDogClick = {},
            onAddDogClick = {},
            onDateClick = {},
            onWeekSwipe = {},
            onSymptomCardClick = {},
            onExcretaCardClick = {},
            onSupplementCardClick = {},
            onWeightCardClick = {},
            onFeedCardClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenDogNotExistPreview() {
    SemobanTheme {
        HomeScreen(
            profileImageUrl = null,
            dogs = emptyList(),
            selectedDogPos = null,
            showDogNotExist = true,
            dateList = List(7) { Date(it * 86_400_000L) },
            selectedDatePos = 3,
            symptoms = emptyList(),
            fecesCount = 0,
            urineCount = 0,
            supplementsRate = 0,
            weight = "0.0",
            feedIntake = "0",
            onCalendarClick = {},
            onAlarmClick = {},
            onProfileClick = {},
            onDogClick = {},
            onAddDogClick = {},
            onDateClick = {},
            onWeekSwipe = {},
            onSymptomCardClick = {},
            onExcretaCardClick = {},
            onSupplementCardClick = {},
            onWeightCardClick = {},
            onFeedCardClick = {},
        )
    }
}
