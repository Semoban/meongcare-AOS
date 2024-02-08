package com.project.meongcare.feed.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.feed.model.data.repository.FeedRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedStopViewModel
    @Inject
    constructor(
        private val feedRepositoryImpl: FeedRepositoryImpl,
    ) : ViewModel() {
        private var _feedStopped = MutableLiveData<Int>()
        val feedStopped
            get() = _feedStopped

        fun stopFeed(
            accessToken: String,
            feedRecordId: Long,
        ) {
            viewModelScope.launch {
                _feedStopped.value = feedRepositoryImpl.stopFeed(accessToken, feedRecordId)
            }
        }
    }
