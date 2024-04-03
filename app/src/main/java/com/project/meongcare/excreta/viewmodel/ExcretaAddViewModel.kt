package com.project.meongcare.excreta.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.excreta.model.data.repository.ExcretaRepositoryImpl
import com.project.meongcare.excreta.model.entities.ExcretaPostRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExcretaAddViewModel
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

        private var _excretaPosted = MutableLiveData<Int>()
        val excretaPosted
            get() = _excretaPosted

        fun getExcretaDate(date: String) {
            _excretaDate.value = date
        }

        fun getExcretaImage(uri: Uri) {
            _excretaImage.value = uri
        }

        fun postExcreta(
            accessToken: String,
            dogId: Long,
            excretaType: String,
            dateTime: String,
            imageURL: String?,
        ) {
            viewModelScope.launch {
                val excretaPostRequest =
                    ExcretaPostRequest(
                        dogId,
                        excretaType,
                        dateTime,
                        imageURL,
                    )

                _excretaPosted.value = excretaRepositoryImpl.postExcreta(accessToken, excretaPostRequest)
            }
        }
    }
