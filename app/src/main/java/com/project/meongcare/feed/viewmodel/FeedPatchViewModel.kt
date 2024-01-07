package com.project.meongcare.feed.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.feed.model.data.repository.FeedRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedPatchViewModel
    @Inject
    constructor(
        private val feedRepositoryImpl: FeedRepositoryImpl,
    ) : ViewModel() {
        private var _feedPatched = MutableLiveData<Int>()
        val feedPatched
            get() = _feedPatched

        fun patchFeed() {
            viewModelScope.launch {
                feedPatched.value =
                    feedRepositoryImpl.patchFeed()
            }
        }
    }
