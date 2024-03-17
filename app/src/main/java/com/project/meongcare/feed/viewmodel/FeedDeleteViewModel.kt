package com.project.meongcare.feed.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.feed.model.data.repository.FeedRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedDeleteViewModel
    @Inject
    constructor(
        private val feedRepositoryImpl: FeedRepositoryImpl,
    ) : ViewModel() {
        private var _feedDeleted = MutableLiveData<Int>()
        val feedDeleted
            get() = _feedDeleted

        fun deleteFeed(
            accessToken: String,
            feedRecordId: Long,
        ) {
            viewModelScope.launch {
                _feedDeleted.value =
                    feedRepositoryImpl.deleteFeed(accessToken, feedRecordId)
            }
        }
    }
