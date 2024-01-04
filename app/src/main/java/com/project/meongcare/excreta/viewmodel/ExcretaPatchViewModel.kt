package com.project.meongcare.excreta.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.excreta.model.data.repository.ExcretaRepositoryImpl
import com.project.meongcare.excreta.model.entities.ExcretaInfoPatch
import com.project.meongcare.excreta.model.entities.ExcretaUploadRequest
import com.project.meongcare.excreta.utils.ExcretaInfoUtils.convertExcretaFile
import com.project.meongcare.excreta.utils.ExcretaInfoUtils.convertExcretaPatchDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExcretaPatchViewModel
    @Inject
    constructor(
        private val excretaRepositoryImpl: ExcretaRepositoryImpl,
    ) : ViewModel() {
        private var _excretaDate = MutableLiveData<String>()
        val excretaDate
            get() = _excretaDate

        private var _excretaImage = MutableLiveData<Uri>()
        val excretaImage
            get() = _excretaImage

        private var _excretaPatched = MutableLiveData<Int>()
        val excretaPatched
            get() = _excretaPatched

        fun getExcretaDate(date: String) {
            _excretaDate.value = date
        }

        fun getExcretaImage(uri: Uri) {
            _excretaImage.value = uri
        }

        fun patchExcreta(
            excretaId: Long,
            excretaType: String,
            excretaDateTime: String,
            context: Context,
            uri: Uri,
        ) {
            viewModelScope.launch {
                val excretaInfoPatch =
                    ExcretaInfoPatch(
                        excretaId,
                        excretaType,
                        excretaDateTime,
                    )
                val dto = convertExcretaPatchDto(excretaInfoPatch)
                val file = convertExcretaFile(context, uri)

                val excretaPatchRequest =
                    ExcretaUploadRequest(
                        dto,
                        file,
                    )

                _excretaPatched.value = excretaRepositoryImpl.patchExcreta(excretaPatchRequest)
            }
        }
    }
