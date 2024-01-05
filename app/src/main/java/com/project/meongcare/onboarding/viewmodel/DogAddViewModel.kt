package com.project.meongcare.onboarding.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DogAddViewModel
    @Inject
    constructor() : ViewModel() {
        private val _dogProfileImage = MutableLiveData<Uri?>()
        val dogProfileImage: LiveData<Uri?>
            get() = _dogProfileImage

        private val _dogBirthDate = MutableLiveData<String>()
        val dogBirthDate: LiveData<String>
            get() = _dogBirthDate

        fun getDogProfileImage(uri: Uri) {
            _dogProfileImage.value = uri
        }

        fun getDogBirthDate(str: String) {
            _dogBirthDate.value = str
        }
    }
