package com.project.meongcare.feed.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.feed.model.data.repository.FeedRepositoryImpl
import com.project.meongcare.feed.model.entities.FeedDetailGetResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedDetailGetViewModel
    @Inject
    constructor(
        private val feedRepositoryImpl: FeedRepositoryImpl,
    ) : ViewModel() {
        private var _feedDetailGet = MutableLiveData<FeedDetailGetResponse>()
        val feedDetailGet
            get() = _feedDetailGet

        fun getFeedDetail(
            accessToken: String,
            feedId: Long,
            feedRecordId: Long,
        ) {
            viewModelScope.launch {
                _feedDetailGet.value =
                    feedRepositoryImpl.getDetailFeed(
                        accessToken,
                        feedId,
                        feedRecordId,
                    )
            }
        }
    }
