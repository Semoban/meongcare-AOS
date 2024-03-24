package com.project.meongcare.aws.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.aws.model.data.repository.AWSS3Repository
import com.project.meongcare.aws.model.entities.AWSS3Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class AWSS3ViewModel
    @Inject
    constructor(private val awsS3Repository: AWSS3Repository) : ViewModel() {
        private val _preSignedUrl = MutableLiveData<AWSS3Response?>()
        val preSignedUrl: LiveData<AWSS3Response?>
            get() = _preSignedUrl

        private val _uploadImageResponse = MutableLiveData<Int?>()
        val uploadImageResponse: LiveData<Int?>
            get() = _uploadImageResponse

        fun getPreSignedUrl(
            accessToken: String,
            fileName: String,
        ) {
            viewModelScope.launch {
                _preSignedUrl.value = awsS3Repository.getPreSignedUrl(accessToken, fileName)
            }
        }

        fun uploadImageToS3(
            preSignedUrl: String,
            file: MultipartBody.Part,
        ) {
            viewModelScope.launch {
                _uploadImageResponse.value = awsS3Repository.uploadImageToS3(preSignedUrl, file)
            }
        }
    }
