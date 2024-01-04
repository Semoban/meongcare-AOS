package com.project.meongcare.excreta.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.excreta.model.data.repository.ExcretaRepositoryImpl
import com.project.meongcare.excreta.model.entities.ExcretaInfo
import com.project.meongcare.excreta.model.entities.ExcretaUploadRequest
import com.project.meongcare.excreta.utils.ExcretaInfoUtils.convertExcretaPostDto
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

    private var _excretaImage = MutableLiveData<Uri>()
    val excretaImage
        get() = _excretaImage

    val excretaPosted
        get() = _excretaPosted

    fun getExcretaDate(date: String) {
        _excretaDate.value = date
    }

    fun getExcretaImage(uri: Uri) {
        _excretaImage.value = uri
    }

    fun postExcreta(
        excretaType: String,
        dateTime: String,
        context: Context,
        uri: Uri,
    ) {
        viewModelScope.launch {
            val excretaInfo = ExcretaInfo(
                2L,
                excretaType,
                dateTime,
            )
            val dto = convertExcretaPostDto(excretaInfo)
            val file = convertExcretaFile(context, uri)

            val excretaPostRequest = ExcretaUploadRequest(
                dto,
                file,
            )

            _excretaPosted.value = excretaRepositoryImpl.postExcreta(excretaPostRequest)
        }
    }
}
