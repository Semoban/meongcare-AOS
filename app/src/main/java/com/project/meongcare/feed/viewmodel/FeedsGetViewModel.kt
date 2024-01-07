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

        private var allFeeds: Feeds? = null

        fun getFeeds() {
            viewModelScope.launch {
                allFeeds = feedRepositoryImpl.getFeeds()
                _feedsGet.value = allFeeds!!
            }
        }

        fun filterFeeds(searchText: String) {
            val filteredFeeds = allFeeds?.feeds?.filter { feed ->
                feed.brandName.contains(searchText) || feed.feedName.contains(searchText)
            }
            _feedsGet.value = Feeds(filteredFeeds ?: listOf())
        }
    }
