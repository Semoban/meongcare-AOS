package com.project.meongcare.feed.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.feed.model.data.repository.FeedRepositoryImpl
import com.project.meongcare.feed.model.entities.FeedGetResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedGetViewModel
    @Inject
    constructor(
        private val feedRepositoryImpl: FeedRepositoryImpl,
    ) : ViewModel() {
        private var _feedGet = MutableLiveData<FeedGetResponse>()
        val feedGet
            get() = _feedGet

        fun getFeed(
            accessToken: String,
            dogId: Long,
        ) {
            viewModelScope.launch {
                _feedGet.value =
                    feedRepositoryImpl.getFeed(accessToken, dogId)
            }
        }
    }
