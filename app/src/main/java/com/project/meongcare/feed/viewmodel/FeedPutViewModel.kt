package com.project.meongcare.feed.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.feed.model.data.repository.FeedRepositoryImpl
import com.project.meongcare.feed.model.entities.FeedUploadRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedPutViewModel
    @Inject
    constructor(
        private val feedRepositoryImpl: FeedRepositoryImpl,
    ) : ViewModel() {
        private var _feedImage = MutableLiveData<Uri>()
        val feedImage
            get() = _feedImage

        private var _feedPut = MutableLiveData<Int>()
        val feedPut
            get() = _feedPut

        fun getImageFeed(uri: Uri) {
            viewModelScope.launch {
                _feedImage.value = uri
            }
        }

        fun putFeed(feedUploadRequest: FeedUploadRequest) {
            viewModelScope.launch {
                _feedPut.value =
                    feedRepositoryImpl.putFeed(feedUploadRequest)
            }
        }
    }
