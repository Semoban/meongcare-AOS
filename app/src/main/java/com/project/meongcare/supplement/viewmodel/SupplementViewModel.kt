package com.project.meongcare.supplement.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.supplement.model.data.repository.SupplementRepository
import com.project.meongcare.supplement.model.entities.DetailSupplement
import com.project.meongcare.supplement.model.entities.IntakeInfo
import com.project.meongcare.supplement.model.entities.Supplement
import com.project.meongcare.supplement.model.entities.SupplementDog
import com.project.meongcare.supplement.model.entities.SupplementPostRequest
import com.project.meongcare.supplement.utils.SupplementUtils.Companion.convertToDateToDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class SupplementViewModel
    @Inject
    constructor(private val repository: SupplementRepository) : ViewModel() {
        val supplementList = MutableLiveData<List<Supplement>>(emptyList())
        val intakeTimeList = MutableLiveData<List<IntakeInfo>>(emptyList())
        val intakeTimeUnit = MutableLiveData("mg")
        val supplementCycle = MutableLiveData<Int?>()
        val supplementIdList = MutableLiveData<List<Int>>(emptyList())
        val supplementDogList = MutableLiveData<List<SupplementDog>>(emptyList())
        val supplementDetail = MutableLiveData<DetailSupplement>()
        val supplementAddImg = MutableLiveData<Uri?>()
        val supplementCode = MutableLiveData<Int?>()
        val supplementDeleteCode = MutableLiveData<Int?>()
        val routineIsClicked = MutableLiveData<Boolean>()

        fun getSupplements(
            accessToken: String,
            dogId: Long,
            date: Date,
        ) {
            viewModelScope.launch {
                val convertedDate = convertToDateToDate(date)
                repository.getSupplements(accessToken, dogId, convertedDate)
                    .onSuccess {
                        supplementList.value = it.routines.sortedBy { s -> s.intakeTime }
                    }.onFailure {
                        Log.d("SupplementGetFailure", it.toString())
                    }
            }
        }

        fun getSupplementDetail(
            accessToken: String,
            supplementsId: Int,
        ) {
            viewModelScope.launch {
                repository.getSupplementDetail(accessToken, supplementsId)
                    .onSuccess {
                        supplementDetail.value = it
                        routineIsClicked.value = it.isActive
                    }.onFailure {
                        Log.d("SupplementDetailGetFailure", it.toString())
                    }
            }
        }

        fun getSupplementDogs(
            accessToken: String,
            dogId: Long,
        ) {
            viewModelScope.launch {
                repository.getSupplementDogs(accessToken, dogId)
                    .onSuccess {
                        supplementDogList.value = it.supplementsInfos.sortedBy { s -> s.supplementsId }
                    }.onFailure {
                        Log.d("SupplementDogsGetFailure", it.toString())
                    }
            }
        }

        fun checkSupplement(
            accessToken: String,
            supplementsRecordId: Int,
        ) {
            viewModelScope.launch {
                repository.checkSupplement(accessToken, supplementsRecordId)
                    .onSuccess {
                        supplementList.value =
                            supplementList.value?.map { supplement ->
                                if (supplement.supplementsRecordId == supplementsRecordId) {
                                    supplement.copy(intakeStatus = !supplement.intakeStatus)
                                } else {
                                    supplement
                                }
                            }
                    }.onFailure {
                        Log.d("SupplementCheckFailure", it.toString())
                    }
            }
        }

        fun addSupplement(
            accessToken: String,
            supplementPostRequest: SupplementPostRequest,
        ) {
            viewModelScope.launch {
                supplementCode.value = repository.addSupplement(accessToken, supplementPostRequest)
            }
        }

        fun patchSupplementAlarm(
            accessToken: String,
            supplementsId: Int,
            pushAgreement: Boolean,
        ) {
            viewModelScope.launch {
                repository.patchSupplementAlarm(accessToken, supplementsId, pushAgreement)
                    .onSuccess {
                        supplementDogList.value =
                            supplementDogList.value?.map { supplementDog ->
                                if (supplementDog.supplementsId == supplementsId) {
                                    supplementDog.copy(pushAgreement = pushAgreement)
                                } else {
                                    supplementDog
                                }
                            }
                    }.onFailure {
                        Log.d("SupplementAlarmPatchFailure", it.toString())
                    }
            }
        }

        fun patchSupplementActive(
            accessToken: String,
            supplementsId: Int,
            isActive: Boolean,
        ) {
            viewModelScope.launch {
                routineIsClicked.value = isActive
                repository.patchSupplementActive(accessToken, supplementsId, isActive)
                    .onFailure {
                        Log.d("SupplementActivePatchFailure", it.toString())
                    }
            }
        }

        fun deleteSupplements(
            accessToken: String,
            supplementsIds: IntArray,
        ) {
            viewModelScope.launch {
                supplementDeleteCode.value = repository.deleteSupplementsById(accessToken, supplementsIds)
            }
        }

        fun deleteSupplement(
            accessToken: String,
            supplementsId: Int,
        ) {
            viewModelScope.launch {
                supplementDeleteCode.value = repository.deleteSupplementById(accessToken, supplementsId)
            }
        }

        fun addIntakeInfoList(intakeInfo: IntakeInfo) {
            val currentList = intakeTimeList.value.orEmpty()

            if (currentList.none { it.intakeTime == intakeInfo.intakeTime }) {
                intakeTimeList.value =
                    (currentList + intakeInfo).sortedBy { it.intakeTime }
            }
        }

        fun removeIntakeTimeListItem(indexToRemove: Int) {
            val currentList = intakeTimeList.value.orEmpty()

            if (indexToRemove in currentList.indices) {
                intakeTimeList.value = currentList.filterIndexed { index, _ -> index != indexToRemove }
            }
        }

        fun updateIntakeTimeUnit(unit: String) {
            intakeTimeUnit.value = unit
        }

        fun updateSupplementCycle(cycle: Int) {
            supplementCycle.value = cycle
        }

        fun toggleSupplementIdSelection(supplementsId: Int) {
            val currentList = supplementIdList.value.orEmpty()
            supplementIdList.value =
                if (currentList.contains(supplementsId)) {
                    currentList - supplementsId
                } else {
                    currentList + supplementsId
                }
        }

        fun toggleAllSupplementIdSelection() {
            val allIds = supplementDogList.value.orEmpty().map { it.supplementsId }
            supplementIdList.value =
                if (supplementIdList.value.orEmpty().size == allIds.size) {
                    emptyList()
                } else {
                    allIds
                }
        }
    }
