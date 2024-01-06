package com.project.meongcare.feed.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.feed.model.data.repository.FeedRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedPostViewModel
    @Inject
    constructor(
        private val feedRepositoryImpl: FeedRepositoryImpl,
    ) : ViewModel() {
        private var _feedImage = MutableLiveData<Uri>()
        val feedImage
            get() = _feedImage

        private var _feedPosted = MutableLiveData<Int>()
        val feedPosted
            get() = _feedPosted

        fun getFeedImage(uri: Uri) {
            feedImage.value = uri
        }

        fun postFeed() {
            viewModelScope.launch {
                _feedPosted.value =
                    feedRepositoryImpl.postFeed()
            }
        }
    }
