package com.project.meongcare.supplement.viewmodel

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.home.model.data.local.DogPreferences
import com.project.meongcare.login.model.data.local.UserPreferences
import com.project.meongcare.login.view.GlobalApplication
import com.project.meongcare.supplement.model.data.repository.SupplementRepository
import com.project.meongcare.supplement.model.entities.DetailSupplement
import com.project.meongcare.supplement.model.entities.IntakeInfo
import com.project.meongcare.supplement.model.entities.Supplement
import com.project.meongcare.supplement.model.entities.SupplementDog
import com.project.meongcare.supplement.model.entities.SupplementPostRequest
import com.project.meongcare.supplement.utils.SupplementUtils.Companion.convertToDateToDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class SupplementViewModel
    @Inject
    constructor(private val repository: SupplementRepository) : ViewModel() {
        var supplementList = MutableLiveData<MutableList<Supplement>>()
        var intakeTimeList = MutableLiveData<MutableList<IntakeInfo>>()
        var intakeTimeUnit = MutableLiveData<String>()
        val mgButtonSelected = MutableLiveData<Boolean>(true)
        val scoopButtonSelected = MutableLiveData<Boolean>(false)
        val jungButtonSelected = MutableLiveData<Boolean>(false)
        var supplementCycle = MutableLiveData<Int>()
        var supplementCheckCount = MutableLiveData<Double>()
        var supplementSize = MutableLiveData<Double>()
        var supplementPercentage = MutableLiveData<Double>()
        var supplementIdList = MutableLiveData<MutableList<Int>>()
        var supplementDogList = MutableLiveData<MutableList<SupplementDog>>()
        var supplementDetail = MutableLiveData<DetailSupplement>()
        var supplementAddImg = MutableLiveData<Uri?>()
        var supplementCode = MutableLiveData<Int?>()
        var supplementDeleteCode = MutableLiveData<Int?>()
        var routineIsClicked = MutableLiveData<Boolean>()
        var dogName = MutableLiveData<String>()

        init {
            intakeTimeList.value = mutableListOf()
            supplementList.value = mutableListOf()
            supplementIdList.value = mutableListOf()
            supplementDogList.value = mutableListOf()
            supplementCheckCount.value = 0.0
            supplementSize.value = 0.0
            supplementPercentage.value = 0.0
            supplementAddImg.value = null
            getDogName()
        }

        fun getSupplements(date: Date) {
            viewModelScope.launch {
                val covertedDate = convertToDateToDate(date)
                val accessToken: String? =
                    UserPreferences(GlobalApplication.applicationContext()).accessToken.first()
                val dogId: Long? = DogPreferences(GlobalApplication.applicationContext()).dogId.first()
                Log.d("Supplement get Api accessToken", "$accessToken")
                Log.d("Supplement get Api dogId", "$dogId")
                val supplements = repository.getSupplements(accessToken, dogId, covertedDate)
                supplements.onSuccess {
                    supplementList.value =
                        it.routines.sortedBy { s -> s.intakeTime }.toMutableList()
                    supplementSize.value = supplementList.value!!.size.toDouble()
                    supplementCheckCount.value =
                        supplementList.value!!.count { it.intakeStatus }.toDouble()
                    Log.d("Supplement get Api 통신 성공", supplementList.value.toString())
                }.onFailure {
                    Log.d("Supplement get Api 통신 에러", it.toString())
                }
            }
        }

        fun getSupplementDetail(supplementsId: Int) {
            viewModelScope.launch {
                val accessToken: String? =
                    UserPreferences(GlobalApplication.applicationContext()).accessToken.first()
                val supplements = repository.getSupplementDetail(accessToken, supplementsId)
                supplements.onSuccess {
                    supplementDetail.value = it
                    Log.d("영양제 상세 조회 Api 통신 성공", supplementDetail.value.toString())
                }.onFailure {
                    Log.d("영양제 상세 조회 Api 통신 에러", it.toString())
                }
            }
        }

        fun getSupplementDogs() {
            viewModelScope.launch {
                val accessToken: String? =
                    UserPreferences(GlobalApplication.applicationContext()).accessToken.first()
                val dogId: Long? = DogPreferences(GlobalApplication.applicationContext()).dogId.first()
                val supplements = repository.getSupplementDogs(accessToken, dogId)
                supplements.onSuccess {
                    supplementDogList.value =
                        it.supplementsInfos.sortedBy { s -> s.supplementsId }.toMutableList()
                    Log.d("영양제 get dog Api 통신 성공", supplementDogList.value.toString())
                }.onFailure {
                    Log.d("영양제 get dog Api 통신 에러", it.toString())
                }
            }
        }

        fun checkSupplement(
            supplementsRecordId: Int,
            imageView: ImageView,
        ) {
            viewModelScope.launch {
                val accessToken: String? =
                    UserPreferences(GlobalApplication.applicationContext()).accessToken.first()
                val check = repository.checkSupplement(accessToken, supplementsRecordId)
                check.onSuccess {
                    if (!imageView.isSelected) {
                        supplementCheckCount.value = supplementCheckCount.value?.plus(1)
                    } else {
                        supplementCheckCount.value = supplementCheckCount.value?.minus(1)
                    }
                    Log.d("영양제 체크 Api 통신 성공", it.toString())
                }.onFailure {
                    Log.d("영양제 체크 Api 통신 에러", it.toString())
                }
                withContext(Main) {
                    imageView.isSelected = !imageView.isSelected
                }
            }
        }

        fun addSupplement(
            accessToken: String,
            supplementPostRequest: SupplementPostRequest,
        ) {
            viewModelScope.launch {
                supplementCode.value = repository.addSupplement(accessToken, supplementPostRequest)
                Log.d("영양제 추가 확인2", supplementCode.value.toString())
            }
        }

        fun patchSupplementAlarm(
            supplementsId: Int,
            pushAgreement: Boolean,
        ) {
            viewModelScope.launch {
                val accessToken: String? =
                    UserPreferences(GlobalApplication.applicationContext()).accessToken.first()
                val alarm = repository.patchSupplementAlarm(accessToken, supplementsId, pushAgreement)
                alarm.onSuccess {
                    Log.d("영양제 알람 Api 통신 성공", it.toString())
                }.onFailure {
                    Log.d("영양제 알람 Api 통신 에러", it.toString())
                }
            }
        }

        fun patchSupplementActive(
            supplementsId: Int,
            isActive: Boolean,
        ) {
            viewModelScope.launch {
                val accessToken: String? =
                    UserPreferences(GlobalApplication.applicationContext()).accessToken.first()
                val active = repository.patchSupplementActive(accessToken, supplementsId, isActive)
                active.onSuccess {
                    Log.d("영양제 루틴 활성화 체크 Api 통신 성공", it.toString())
                }.onFailure {
                    Log.d("영양제 루틴 활성화 체크 Api 통신 에러", it.toString())
                }
            }
        }

        fun deleteSupplements(supplementsIds: IntArray) {
            viewModelScope.launch {
                val accessToken: String? =
                    UserPreferences(GlobalApplication.applicationContext()).accessToken.first()
                val check = repository.deleteSupplementsById(accessToken, supplementsIds)
                supplementDeleteCode.value = check
            }
        }

        fun deleteSupplement(supplementsId: Int) {
            viewModelScope.launch {
                val accessToken: String? =
                    UserPreferences(GlobalApplication.applicationContext()).accessToken.first()
                val check = repository.deleteSupplementById(accessToken, supplementsId)
                supplementDeleteCode.value = check
            }
        }

        fun updatePercentage(
            progressBar: ProgressBar,
            textViewPercentage: TextView,
            textViewCount: TextView,
        ) {
            viewModelScope.launch {
                supplementPercentage.value =
                    if (supplementCheckCount.value!! != 0.0 || supplementSize.value!! != 0.0) {
                        supplementCheckCount.value!! / supplementSize.value!! * 100
                    } else {
                        0.0
                    }

                withContext(Main) {
                    progressBar.progress = supplementPercentage.value!!.toInt()
                    textViewPercentage.text =
                        String.format(
                            "%.1f",
                            supplementPercentage.value,
                        )
                    textViewCount.text =
                        (supplementSize.value!! - supplementCheckCount.value!!).toInt().toString()
                }
            }
        }

        fun addIntakeInfoList(intakeInfo: IntakeInfo) {
            val currentList = intakeTimeList.value?.toMutableList() ?: mutableListOf()

            if (currentList.none { i -> i.intakeTime == intakeInfo.intakeTime }) {
                currentList.add(intakeInfo)
            }

            intakeTimeList.value =
                currentList.distinct()
                    .sortedBy { intakeInfo -> intakeInfo.intakeTime } as MutableList<IntakeInfo>
        }

        fun updateButtonStates(selectedButton: MutableLiveData<Boolean>) {
            mgButtonSelected.value = selectedButton == mgButtonSelected
            scoopButtonSelected.value = selectedButton == scoopButtonSelected
            jungButtonSelected.value = selectedButton == jungButtonSelected
        }

        fun onMgButtonClick() {
            intakeTimeUnit.value = "mg"
            updateButtonStates(mgButtonSelected)
        }

        fun onScoopButtonClick() {
            intakeTimeUnit.value = "스쿱"
            updateButtonStates(scoopButtonSelected)
        }

        fun onJungButtonClick() {
            intakeTimeUnit.value = "정"
            updateButtonStates(jungButtonSelected)
        }

        fun removeIntakeTimeListItem(indexToRemove: Int) {
            val currentList = intakeTimeList.value?.toMutableList() ?: mutableListOf()

            if (indexToRemove >= 0 && indexToRemove < currentList.size) {
                currentList.removeAt(indexToRemove)
                intakeTimeList.value = currentList
            }
        }

        fun getDogName() {
            viewModelScope.launch {
                dogName.value = DogPreferences(GlobalApplication.applicationContext()).dogName.first()
            }
        }
    }
