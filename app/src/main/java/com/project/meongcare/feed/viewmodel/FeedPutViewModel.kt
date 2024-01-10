package com.project.meongcare.feed.viewmodel

import android.app.assist.AssistStructure.ViewNode
import android.view.View
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
        private var _feedPut = MutableLiveData<Int>()
        val feedPut
            get() = _feedPut

        fun putFeed(feedUploadRequest: FeedUploadRequest) {
            viewModelScope.launch {
                _feedPut.value =
                    feedRepositoryImpl.putFeed(feedUploadRequest)
            }
        }
    }
