package com.project.meongcare.medicalRecord.viewmodel

import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.R
import com.project.meongcare.home.model.data.local.DogPreferences
import com.project.meongcare.login.model.data.local.UserPreferences
import com.project.meongcare.login.view.GlobalApplication
import com.project.meongcare.medicalRecord.model.data.repository.MedicalRecordRepository
import com.project.meongcare.medicalRecord.model.entities.MedicalRecordDto
import com.project.meongcare.medicalRecord.model.entities.RequestMedicalRecord
import com.project.meongcare.medicalRecord.utils.MedicalRecordUtils.Companion.convertMedicalRecordDto
import com.project.meongcare.medicalRecord.utils.MedicalRecordUtils.Companion.convertPictureToFile
import com.project.meongcare.supplement.model.entities.RequestSupplement
import com.project.meongcare.supplement.model.entities.SupplementDto
import com.project.meongcare.supplement.utils.SupplementUtils
import com.project.meongcare.symptom.model.data.repository.SymptomRepository
import com.project.meongcare.symptom.model.entities.Symptom
import com.project.meongcare.symptom.model.entities.ToAddSymptom
import com.project.meongcare.symptom.model.entities.ToEditSymptom
import com.project.meongcare.symptom.utils.SymptomUtils.Companion.convertToDateToMiliSec
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MedicalRecordViewModel
    @Inject
    constructor(private val repository: MedicalRecordRepository) : ViewModel() {
        var medicalRecordAddCode = MutableLiveData<Int>()

        init {

        }

        fun addSupplement(
            dateTime: String,
            hospitalName: String,
            doctorName: String,
            note: String,
            uri: Uri,
        ) {
            viewModelScope.launch {
                val accessToken: String? =
                    UserPreferences(GlobalApplication.applicationContext()).accessToken.first()
                val dogId: Long? = DogPreferences(GlobalApplication.applicationContext()).dogId.first()

                val medicalRecordDto =
                    MedicalRecordDto(dogId!!, dateTime, hospitalName, doctorName, note)
                val dto = convertMedicalRecordDto(medicalRecordDto)
                val file =
                    convertPictureToFile(GlobalApplication.applicationContext(), uri)

                val requestMedicalRecord =
                    RequestMedicalRecord(
                        dto,
                        file,
                    )
                Log.d("진료기록 추가 확인", medicalRecordDto.toString())
                medicalRecordAddCode.value = repository.addMedicalRecord(accessToken, requestMedicalRecord)
                Log.d("진료기록 추가 확인2", medicalRecordAddCode.value.toString())
            }
        }
    }
