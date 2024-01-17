package com.project.meongcare.feed.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.feed.model.data.repository.FeedRepositoryImpl
import com.project.meongcare.feed.model.entities.FeedRecords
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreviousFeedGetViewModel
    @Inject
    constructor(
        private val feedRepositoryImpl: FeedRepositoryImpl,
    ) : ViewModel() {
        private var _previousFeedGet = MutableLiveData<FeedRecords>()
        val previousFeedGet
            get() = _previousFeedGet

        fun getPreviousFeed(
            dogId: Long,
            feedRecordId: Long
        ) {
            viewModelScope.launch {
                _previousFeedGet.value =
                    feedRepositoryImpl.getPreviousFeed(dogId, feedRecordId)
            }
        }
    }
