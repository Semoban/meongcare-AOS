package com.project.meongcare.feed.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.feed.model.data.repository.FeedRepositoryImpl
import com.project.meongcare.feed.model.entities.FeedPartRecords
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedPartGetViewModel
    @Inject
    constructor(
        private val feedRepositoryImpl: FeedRepositoryImpl,
    ) : ViewModel() {
        private var _feedPartGet = MutableLiveData<FeedPartRecords>()
        val feedPartGet
            get() = _feedPartGet

        fun getFeedPart(feedRecordId: Long) {
            viewModelScope.launch {
                feedPartGet.value =
                    feedRepositoryImpl.getFeedPart(feedRecordId)
            }
        }
    }
