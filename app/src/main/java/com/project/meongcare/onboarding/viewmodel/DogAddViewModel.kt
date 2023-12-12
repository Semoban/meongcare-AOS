package com.project.meongcare.onboarding.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class DogAddViewModel
    @Inject
    constructor() : ViewModel() {
        private val _dogProfileImage = MutableLiveData<Bitmap?>()
        val dogProfileImage: LiveData<Bitmap?>
            get() = _dogProfileImage

        private val _dogBirthDate = MutableLiveData<String>()
        val dogBirthDate: LiveData<String>
            get() = _dogBirthDate

        fun getDogProfileImage(bitmap: Bitmap) {
            _dogProfileImage.value = bitmap
        }

        fun getDogBirthDate(str: String) {
            _dogBirthDate.value = str
        }
    }
