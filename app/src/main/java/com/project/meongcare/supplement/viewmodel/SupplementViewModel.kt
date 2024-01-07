package com.project.meongcare.supplement.viewmodel

import android.util.Log
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.supplement.model.data.repository.SupplementRepository
import com.project.meongcare.supplement.model.entities.IntakeInfo
import com.project.meongcare.supplement.model.entities.Supplement
import com.project.meongcare.supplement.model.entities.SupplementInfo
import com.project.meongcare.supplement.utils.SupplementUtils.Companion.convertToDateToDate
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class SupplementViewModel(private val repository: SupplementRepository) : ViewModel() {
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
    var supplementIdListAllCheck = MutableLiveData<Boolean>()
    var supplementInfoList = MutableLiveData<MutableList<SupplementInfo>>()

    init {
        intakeTimeList.value = mutableListOf()
        supplementList.value = mutableListOf()
        supplementIdList.value = mutableListOf()
        supplementInfoList.value = mutableListOf()
        supplementCheckCount.value = 0.0
        supplementSize.value = 0.0
        supplementPercentage.value = 0.0
        supplementIdListAllCheck.value = false
    }

    fun getSupplements(
        dogId: Int,
        date: Date,
    ) {
        viewModelScope.launch {
            val covertedDate = convertToDateToDate(date)
            val supplements = repository.getSupplements(dogId, covertedDate)
            supplements.onSuccess {
                supplementList.value =
                    it.routines.sortedBy { s -> s.intakeTime }.toMutableList()
                supplementSize.value = supplementList.value!!.size.toDouble()
                supplementCheckCount.value =
                    supplementList.value!!.count { it.intakeStatus }.toDouble()
//                    supplementList.value = it.routines.toMutableList()
                Log.d("Supplement get Api 통신 성공", supplementList.value.toString())
            }.onFailure {
                Log.d("Supplement get Api 통신 에러", it.toString())
            }
        }
    }

    fun getSupplementInfos(
        dogId: Int,
    ) {
        viewModelScope.launch {
            val supplements = repository.getSupplementInfos(dogId)
            supplements.onSuccess {
                supplementInfoList.value =
                    it.supplementsInfos.sortedBy { s -> s.supplementsId }.toMutableList()
                Log.d("영양제 get info Api 통신 성공", supplementInfoList.value.toString())
            }.onFailure {
                Log.d("영양제 get info Api 통신 에러", it.toString())
            }
        }
    }

    fun checkSupplement(supplementsRecordId: Int, imageView: ImageView) {
        viewModelScope.launch {
            val check = repository.checkSupplement(supplementsRecordId)
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

    fun patchSupplementAlarm(supplementsId: Int, pushAgreement: Boolean) {
        viewModelScope.launch {
            Log.d("영양제 알람1", "$supplementsId, $pushAgreement")
            val alarm = repository.patchSupplementAlarm(supplementsId, pushAgreement)
            alarm.onSuccess {
                Log.d("영양제 알람 Api 통신 성공", it.toString())
            }.onFailure {
                Log.d("영양제 알람 Api 통신 에러", it.toString())
            }
        }
    }

    fun deleteSupplements(supplementsIds: IntArray) {
        viewModelScope.launch {
            val check = repository.deleteSupplementsById(supplementsIds)
            check.onSuccess {
                Log.d("영양제 삭제 Api 통신 성공", it.toString())
            }.onFailure {
                Log.d("영양제 삭제 Api 통신 에러", it.toString())
            }
        }
    }

    fun updatePercentage(
        progressBar: ProgressBar,
        textViewPercentage: TextView,
        textViewCount: TextView
    ) {
        viewModelScope.launch {
            supplementPercentage.value =
                if (supplementCheckCount.value!! != 0.0 || supplementSize.value!! != 0.0) {
                    supplementCheckCount.value!! / supplementSize.value!! * 100
                } else 0.0

            Log.d(
                "영양제 가져오기",
                "${supplementCheckCount.value}, ${supplementSize.value}, ${supplementPercentage.value}"
            )
            withContext(Main) {
                progressBar.progress = supplementPercentage.value!!.toInt()
                textViewPercentage.text = String.format(
                    "%.1f",
                    supplementPercentage.value,
                )
                textViewCount.text =
                    (supplementSize.value!! - supplementCheckCount.value!!).toInt().toString()
            }
        }
    }

    fun addIntakeInfoList(
        intakeInfo: IntakeInfo
    ) {
        val currentList = intakeTimeList.value?.toMutableList() ?: mutableListOf()

        if (currentList.none { i -> i.intakeTime == intakeInfo.intakeTime }) {
            currentList.add(intakeInfo)
        }

        intakeTimeList.value = currentList.distinct()
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

    fun updateSupplementIds(supplementsRoutineIdList: MutableList<Int>) {
        viewModelScope.launch {
            if (supplementsRoutineIdList.isNotEmpty()) {
                Log.d(
                    "루틴 편집 9",
                    supplementIdList.value!!.containsAll(supplementsRoutineIdList).toString()
                )
                Log.d("루틴 편집 11", supplementsRoutineIdList.toString())
                if (supplementIdList.value!!.containsAll(supplementsRoutineIdList)) {
                    supplementIdList.value!!.removeAll(supplementsRoutineIdList)
                    Log.d("루틴 편집 12", supplementIdList.value.toString())
                } else {
                    supplementIdList.value!!.addAll(supplementsRoutineIdList)
                    Log.d("루틴 편집 10", supplementIdList.value!!.toString())
                }
            }
        }
    }

    fun setAllItemsChecked(isChecked: Boolean, supplementsRoutineIdList: MutableList<Int>) {
        if (isChecked) {
            supplementIdList.value!!.clear()
            supplementIdList.value = supplementsRoutineIdList
            Log.d("루틴 변경3", supplementList.value.toString())
            Log.d("루틴 변경4", supplementIdList.value.toString())
        } else {
            supplementIdList.value!!.clear()
        }

        supplementIdListAllCheck.value = isChecked
    }
}
