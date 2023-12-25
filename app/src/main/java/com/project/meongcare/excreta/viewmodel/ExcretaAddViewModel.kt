package com.project.meongcare.excreta.viewmodel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.excreta.model.data.repository.ExcretaRepositoryImpl
import com.project.meongcare.excreta.model.entities.ExcretaInfo
import com.project.meongcare.excreta.model.entities.ExcretaPostRequest
import com.project.meongcare.excreta.utils.ExcretaInfoUtils.convertExcretaDto
import com.project.meongcare.excreta.utils.ExcretaInfoUtils.convertExcretaFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExcretaAddViewModel
@Inject constructor(
    private val excretaRepositoryImpl: ExcretaRepositoryImpl,
) : ViewModel() {
    private var _excretaDate = MutableLiveData<String>()
    val excretaDate
        get() = _excretaDate

    private var _excretaPosted = MutableLiveData<Int>()
    val excretaPosted
        get() = _excretaPosted

    fun getExcretaDate(date: String) {
        _excretaDate.value = date
    }

    fun postExcreta(
        excretaType: String,
        dateTime: String,
    ) {
        viewModelScope.launch {
            val excretaInfo = ExcretaInfo(
                2L,
                excretaType,
                dateTime,
            )
            val dto = convertExcretaDto(excretaInfo)
            val file = convertExcretaFile()

            val excretaPostRequest = ExcretaPostRequest(
                dto,
                file,
            )

            _excretaPosted.value = excretaRepositoryImpl.postExcreta(excretaPostRequest)
        }
    }
}
