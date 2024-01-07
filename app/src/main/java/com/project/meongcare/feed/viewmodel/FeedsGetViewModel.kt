package com.project.meongcare.feed.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.feed.model.data.repository.FeedRepositoryImpl
import com.project.meongcare.feed.model.entities.Feeds
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedsGetViewModel
    @Inject
    constructor(
        private val feedRepositoryImpl: FeedRepositoryImpl,
    ) : ViewModel() {
        private var _feedsGet = MutableLiveData<Feeds>()
        val feedsGet
            get() = _feedsGet

        fun getFeeds() {
            viewModelScope.launch {
                feedsGet.value =
                    feedRepositoryImpl.getFeeds()
            }
        }
    }
