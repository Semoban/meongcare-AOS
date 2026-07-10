package com.project.meongcare.medicalRecord.view

import android.view.LayoutInflater
import android.widget.TimePicker
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.project.meongcare.R
import com.project.meongcare.designsystem.component.GlideImage
import com.project.meongcare.designsystem.theme.Black
import com.project.meongcare.designsystem.theme.Gray1
import com.project.meongcare.designsystem.theme.Gray4
import com.project.meongcare.designsystem.theme.Main4
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.designsystem.theme.SemobanTypography
import com.project.meongcare.designsystem.theme.Sub1
import com.project.meongcare.designsystem.theme.White
import com.project.meongcare.medicalRecord.model.entities.MedicalRecordGet
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class MedicalRecordFormResult(
    val date: String,
    val hour: Int,
    val minute: Int,
    val hospitalName: String,
    val doctorName: String,
    val note: String,
)

@Composable
fun MedicalRecordFormScreen(
    initialRecord: MedicalRecordGet?,
    imageModel: Any?,
    selectedDate: String?,
    completeText: String,
    onBackClick: () -> Unit,
    onImageClick: () -> Unit,
    onDateClick: () -> Unit,
    onComplete: (MedicalRecordFormResult) -> Unit,
    modifier: Modifier = Modifier,
) {
    var hospitalName by rememberSaveable { mutableStateOf(initialRecord?.hospitalName.orEmpty()) }
    var doctorName by rememberSaveable { mutableStateOf(initialRecord?.doctorName.orEmpty()) }
    var note by rememberSaveable { mutableStateOf(initialRecord?.note.orEmpty()) }

    val initialTime = initialRecord?.dateTime?.substringAfterLast("T")
    var hour by rememberSaveable { mutableStateOf(initialTime?.substringBefore(":")?.toIntOrNull() ?: 0) }
    var minute by rememberSaveable {
        mutableStateOf(initialTime?.substringAfter(":")?.substringBefore(":")?.toIntOrNull() ?: 0)
    }

    var dateError by remember { mutableStateOf(false) }
    var hospitalError by remember { mutableStateOf(false) }
    var doctorError by remember { mutableStateOf(false) }
    var noteError by remember { mutableStateOf(false) }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(White),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(R.drawable.all_arrow_back_18dp),
                    contentDescription = "뒤로가기",
                )
            }
        }
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 32.dp)
                    .padding(top = 25.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .height(169.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .background(Gray1)
                        .clickable { onImageClick() },
                contentAlignment = Alignment.Center,
            ) {
                if (imageModel == null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(R.drawable.medical_record_add_carrier),
                            contentDescription = null,
                        )
                        Text(
                            text = stringResource(R.string.medicalrecord_image_description),
                            style = SemobanTypography.body3Regular,
                            color = Gray4,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                } else {
                    GlideImage(
                        model = imageModel,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
            MedicalRecordFormLabel(
                text = stringResource(R.string.medicalrecord_date),
                modifier = Modifier.padding(top = 24.dp),
            )
            MedicalRecordDateBox(
                selectedDate = selectedDate,
                showError = dateError,
                onClick = {
                    dateError = false
                    onDateClick()
                },
                modifier = Modifier.padding(top = 8.dp),
            )
            MedicalRecordFormLabel(
                text = stringResource(R.string.medicalrecord_time),
                modifier = Modifier.padding(top = 24.dp),
            )
            MedicalRecordTimePicker(
                initialHour = hour,
                initialMinute = minute,
                onTimeChanged = { newHour, newMinute ->
                    hour = newHour
                    minute = newMinute
                },
                modifier = Modifier.padding(top = 8.dp),
            )
            MedicalRecordFormLabel(
                text = stringResource(R.string.medicalrecord_hospital),
                modifier = Modifier.padding(top = 24.dp),
            )
            MedicalRecordFormTextField(
                value = hospitalName,
                onValueChange = {
                    hospitalError = false
                    hospitalName = it.take(HOSPITAL_NAME_MAX_LENGTH)
                },
                hint = stringResource(R.string.medicalrecord_hospital_name),
                countText = stringResource(R.string.medicalrecord_hospital_name_length, hospitalName.length),
                showError = hospitalError,
                modifier = Modifier.padding(top = 8.dp),
            )
            MedicalRecordFormLabel(
                text = stringResource(R.string.medicalrecord_veterinarian),
                modifier = Modifier.padding(top = 16.dp),
            )
            MedicalRecordFormTextField(
                value = doctorName,
                onValueChange = {
                    doctorError = false
                    doctorName = it.take(DOCTOR_NAME_MAX_LENGTH)
                },
                hint = stringResource(R.string.medicalrecord_veterinarian_name),
                countText = stringResource(R.string.medicalrecord_veterinarian_name_length, doctorName.length),
                showError = doctorError,
                modifier = Modifier.padding(top = 8.dp),
            )
            MedicalRecordFormLabel(
                text = stringResource(R.string.medicalrecord_note),
                modifier = Modifier.padding(top = 16.dp),
            )
            MedicalRecordFormTextField(
                value = note,
                onValueChange = {
                    noteError = false
                    note = it.take(NOTE_MAX_LENGTH)
                },
                hint = stringResource(R.string.medicalrecord_note),
                countText = stringResource(R.string.medicalrecord_note_length, note.length),
                showError = noteError,
                singleLine = false,
                minHeight = 148.dp,
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                text = completeText,
                style = SemobanTypography.bottom1SemiBold,
                color = White,
                textAlign = TextAlign.Center,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp, bottom = 24.dp)
                        .background(Main4, RoundedCornerShape(5.dp))
                        .clickable {
                            var isValid = true

                            if (selectedDate.isNullOrEmpty()) {
                                dateError = true
                                isValid = false
                            }
                            if (hospitalName.isBlank()) {
                                hospitalError = true
                                isValid = false
                            }
                            if (doctorName.isBlank()) {
                                doctorError = true
                                isValid = false
                            }
                            if (note.isBlank()) {
                                noteError = true
                                isValid = false
                            }

                            if (isValid) {
                                onComplete(
                                    MedicalRecordFormResult(
                                        date = selectedDate!!,
                                        hour = hour,
                                        minute = minute,
                                        hospitalName = hospitalName,
                                        doctorName = doctorName,
                                        note = note,
                                    ),
                                )
                            }
                        }
                        .padding(vertical = 12.dp),
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun MedicalRecordFormLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = SemobanTypography.title3SemiBold,
        modifier = modifier,
    )
}

@Composable
private fun MedicalRecordDateBox(
    selectedDate: String?,
    showError: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var boxModifier =
        modifier
            .fillMaxWidth()
            .background(Gray1, RoundedCornerShape(5.dp))
    if (showError) {
        boxModifier = boxModifier.border(1.dp, Sub1, RoundedCornerShape(5.dp))
    }
    Row(
        modifier =
            boxModifier
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text =
                when {
                    showError -> "필수 입력 값입니다."
                    selectedDate != null -> convertToDisplayFormDate(selectedDate)
                    else -> stringResource(R.string.medicalrecord_date)
                },
            style = if (selectedDate != null) SemobanTypography.body1Medium else SemobanTypography.body1Regular,
            color =
                when {
                    showError -> Sub1
                    selectedDate != null -> Black
                    else -> Gray4
                },
            modifier = Modifier.weight(1f),
        )
        if (!showError && selectedDate == null) {
            Image(
                painter = painterResource(R.drawable.all_calendar),
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun MedicalRecordFormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    countText: String,
    showError: Boolean,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    minHeight: Dp = Dp.Unspecified,
) {
    var fieldModifier =
        Modifier
            .fillMaxWidth()
            .background(Gray1, RoundedCornerShape(5.dp))
    if (showError) {
        fieldModifier = fieldModifier.border(1.dp, Sub1, RoundedCornerShape(5.dp))
    }
    if (minHeight != Dp.Unspecified) {
        fieldModifier = fieldModifier.height(minHeight)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = SemobanTypography.body1Regular,
            singleLine = singleLine,
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { innerTextField ->
                Box(
                    modifier = fieldModifier.padding(horizontal = 16.dp, vertical = 13.dp),
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = if (showError) "필수 입력 값입니다" else hint,
                            style = SemobanTypography.body1Regular,
                            color = if (showError) Sub1 else Gray4,
                        )
                    }
                    innerTextField()
                }
            },
        )
        Text(
            text = countText,
            style = SemobanTypography.body2Regular,
            color = Gray4,
            textAlign = TextAlign.End,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
        )
    }
}

@Composable
private fun MedicalRecordTimePicker(
    initialHour: Int,
    initialMinute: Int,
    onTimeChanged: (hour: Int, minute: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            val timePicker =
                LayoutInflater.from(context)
                    .inflate(R.layout.view_spinner_time_picker, null) as TimePicker
            timePicker.hour = initialHour
            timePicker.minute = initialMinute
            timePicker.setOnTimeChangedListener { _, hour, minute ->
                onTimeChanged(hour, minute)
            }
            timePicker
        },
    )
}

private fun convertToDisplayFormDate(date: String): String {
    val parsedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    return parsedDate.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일"))
}

private const val HOSPITAL_NAME_MAX_LENGTH = 50
private const val DOCTOR_NAME_MAX_LENGTH = 10
private const val NOTE_MAX_LENGTH = 500

@Preview(showBackground = true)
@Composable
private fun MedicalRecordFormScreenPreview() {
    SemobanTheme {
        MedicalRecordFormScreen(
            initialRecord = null,
            imageModel = null,
            selectedDate = null,
            completeText = "기록하기",
            onBackClick = {},
            onImageClick = {},
            onDateClick = {},
            onComplete = {},
        )
    }
}
