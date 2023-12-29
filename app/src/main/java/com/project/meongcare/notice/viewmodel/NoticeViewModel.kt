package com.project.meongcare.notice.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.meongcare.notice.model.data.repository.NoticeRepository
import com.project.meongcare.notice.model.entities.Notice
import com.project.meongcare.notice.model.entities.NoticeGetListResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoticeViewModel
    @Inject
    constructor(private val noticeRepository: NoticeRepository) : ViewModel() {
        private val _noticeList = MutableLiveData<NoticeGetListResponse>()
        val noticeList: LiveData<NoticeGetListResponse>
            get() = _noticeList

        fun getNoticeList(type: String) {
            viewModelScope.launch {
                _noticeList.value = noticeRepository.getNoticeList(type)
            }
        }
    }
