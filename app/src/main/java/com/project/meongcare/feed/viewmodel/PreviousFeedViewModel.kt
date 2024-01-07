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
class PreviousFeedViewModel
    @Inject
    constructor(
        private val feedRepositoryImpl: FeedRepositoryImpl,
    ) : ViewModel() {
        private var _previousFeedGet = MutableLiveData<FeedRecords>()
        val previousFeedGet
            get() = _previousFeedGet

        fun getPreviousFeed(feedRecordId: Long) {
            viewModelScope.launch {
                previousFeedGet.value =
                    feedRepositoryImpl.getPreviousFeed(feedRecordId)
            }
        }
    }