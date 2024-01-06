package com.project.meongcare.feed.viewmodel

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
        private var _feedPosted = MutableLiveData<Int>()
        val feedPosted
            get() = _feedPosted

        fun postFeed() {
            viewModelScope.launch {
                _feedPosted.value =
                    feedRepositoryImpl.postFeed()
            }
        }
    }
