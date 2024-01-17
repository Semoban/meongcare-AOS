package com.project.meongcare.onboarding.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.onboarding.model.data.repository.DogAddRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class DogAddViewModel
    @Inject
    constructor(
        private val dogAddRepository: DogAddRepository,
    ) : ViewModel() {
        private val _dogProfileImage = MutableLiveData<Uri?>()
        val dogProfileImage: LiveData<Uri?>
            get() = _dogProfileImage

        private val _dogBirthDate = MutableLiveData<String>()
        val dogBirthDate: LiveData<String>
            get() = _dogBirthDate

        private val _dogAddResponse = MutableLiveData<Int>()
        val dogAddResponse
            get() = _dogAddResponse

        fun getDogProfileImage(uri: Uri) {
            _dogProfileImage.value = uri
        }

        fun getDogBirthDate(str: String) {
            _dogBirthDate.value = str
        }

        fun postDogInfo(
            accessToken: String,
            file: MultipartBody.Part,
            dto: RequestBody,
        ) {
            viewModelScope.launch {
                _dogAddResponse.value = dogAddRepository.postDogInfo(accessToken, file, dto)
            }
        }
    }
