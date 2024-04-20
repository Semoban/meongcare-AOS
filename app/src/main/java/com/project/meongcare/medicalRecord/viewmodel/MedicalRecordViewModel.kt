package com.project.meongcare.medicalRecord.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.login.view.GlobalApplication
import com.project.meongcare.medicalRecord.model.data.repository.MedicalRecordRepositoryImpl
import com.project.meongcare.medicalRecord.model.entities.MedicalRecordDto
import com.project.meongcare.medicalRecord.model.entities.MedicalRecordGet
import com.project.meongcare.medicalRecord.model.entities.MedicalRecordGetResponse
import com.project.meongcare.medicalRecord.model.entities.MedicalRecordPutDto
import com.project.meongcare.medicalRecord.model.entities.RequestMedicalRecord
import com.project.meongcare.medicalRecord.model.utils.MedicalRecordUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MedicalRecordViewModel
    @Inject
    constructor(
        private val medicalRecordRepositoryImpl: MedicalRecordRepositoryImpl,
    ) : ViewModel() {
        private val _medicalRecordList = MutableLiveData<Response<MedicalRecordGetResponse>>()
        val medicalRecordList: LiveData<Response<MedicalRecordGetResponse>>
            get() = _medicalRecordList

        private val _medicalRecord = MutableLiveData<Response<MedicalRecordGet>>()
        val medicalRecord: LiveData<Response<MedicalRecordGet>>
            get() = _medicalRecord

        private val _selectedDate = MutableLiveData<String?>()
        val selectedDate: LiveData<String?>
            get() = _selectedDate

        private val _deleteMedicalRecordResponse = MutableLiveData<Int>()
        val deleteMedicalRecordResponse: LiveData<Int>
            get() = _deleteMedicalRecordResponse

        private val _medicalRecordAddResponse = MutableLiveData<Int>()
        val medicalRecordAddResponse: LiveData<Int>
            get() = _medicalRecordAddResponse

        private val _medicalRecordAddImgUri = MutableLiveData<Uri>()
        val medicalRecordAddImgUri: LiveData<Uri>
            get() = _medicalRecordAddImgUri

        init {
            _selectedDate.value = null
            getMedicalRecordImgUri(Uri.EMPTY)
        }

        fun getMedicalRecordList(
            dogId: Long,
            dateTime: String,
            accessToken: String,
        ) {
            viewModelScope.launch {
                _medicalRecordList.value =
                    medicalRecordRepositoryImpl.getMedicalRecordList(dogId, dateTime, accessToken)
            }
        }

        fun getMedicalRecord(medicalRecordId: Long) {
            viewModelScope.launch {
                //            val accessToken: String? =
                //                UserPreferences(GlobalApplication.applicationContext()).accessToken.first()
                val accessToken =
                    "Bearer eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MTgsImV4cCI6MTcxMzU4NzQ1NH0.YZNwJuZprJ2drk6Ymtqa8-DS0dkkSHNFWWQ1gcRSTwU"
                _medicalRecord.value =
                    medicalRecordRepositoryImpl.getMedicalRecord(medicalRecordId, accessToken)
            }
        }

        fun getCurrentDate(date: String?) {
            _selectedDate.value = date
        }

        fun deleteMedicalRecordList(
            medicalRecordIds: IntArray,
        ) {
            //            val accessToken: String? =
            //                UserPreferences(GlobalApplication.applicationContext()).accessToken.first()
            //            val dogId: Long? = DogPreferences(GlobalApplication.applicationContext()).dogId.first()
            val accessToken =
                "Bearer eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MTgsImV4cCI6MTcxMzU0NTYzNH0.R4BmwumxzV01XhviD6yXJMuVCH2r35ecX4t4vdTbm-M"
            viewModelScope.launch {
                _deleteMedicalRecordResponse.value =
                    medicalRecordRepositoryImpl.deleteMedicalRecordList(medicalRecordIds, accessToken)
            }
        }

        fun addMedicalRecord(
            dateTime: String,
            hospitalName: String,
            doctorName: String,
            note: String,
            uri: Uri,
        ) {
            viewModelScope.launch {
                //            val accessToken: String? =
                //                UserPreferences(GlobalApplication.applicationContext()).accessToken.first()
                //            val dogId: Long? = DogPreferences(GlobalApplication.applicationContext()).dogId.first()
                val accessToken =
                    "Bearer eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MTgsImV4cCI6MTcxMzU0NTYzNH0.R4BmwumxzV01XhviD6yXJMuVCH2r35ecX4t4vdTbm-M"
                val dogId: Long = 6

                val medicalRecordDto =
                    MedicalRecordDto(dogId, dateTime, hospitalName, doctorName, note)
                val dto = MedicalRecordUtils.convertMedicalRecordDto(medicalRecordDto)
                val file =
                    MedicalRecordUtils.convertPictureToFile(GlobalApplication.applicationContext(), uri)

                val requestMedicalRecord =
                    RequestMedicalRecord(
                        dto,
                        file,
                    )
                Log.d("진료기록 추가 확인", medicalRecordDto.toString())
                _medicalRecordAddResponse.value =
                    medicalRecordRepositoryImpl.addMedicalRecord(accessToken, requestMedicalRecord)
                Log.d("진료기록 추가 확인2", medicalRecordAddResponse.value.toString())
            }
        }

        fun putMedicalRecord(
            medicalRecordId: Long,
            dateTime: String,
            hospitalName: String,
            doctorName: String,
            note: String,
            uri: Uri,
        ) {
            viewModelScope.launch {
                //            val accessToken: String? =
                //                UserPreferences(GlobalApplication.applicationContext()).accessToken.first()
                val accessToken =
                    "Bearer eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MTgsImV4cCI6MTcxMzU4NzQ1NH0.YZNwJuZprJ2drk6Ymtqa8-DS0dkkSHNFWWQ1gcRSTwU"

                val medicalRecordPutDto =
                    MedicalRecordPutDto(medicalRecordId, dateTime, hospitalName, doctorName, note)
                val dto = MedicalRecordUtils.convertMedicalRecordPutDto(medicalRecordPutDto)
                val file = MedicalRecordUtils.convertPictureToFile(GlobalApplication.applicationContext(), uri)

                val requestMedicalRecord =
                    RequestMedicalRecord(
                        dto,
                        file,
                    )

                Log.d("진료기록 수정 확인", medicalRecordPutDto.toString())
                _medicalRecordAddResponse.value =
                    medicalRecordRepositoryImpl.putMedicalRecord(accessToken, requestMedicalRecord)
                Log.d("진료기록 수정 확인2", medicalRecordAddResponse.value.toString())
            }
        }

        fun getMedicalRecordImgUri(uri: Uri) {
            _medicalRecordAddImgUri.value = uri
        }
    }
