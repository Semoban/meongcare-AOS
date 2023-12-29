package com.project.meongcare.notice.model.data.repository

import com.project.meongcare.notice.model.entities.NoticeGetListResponse

interface NoticeRepository {
    suspend fun getNoticeList(type: String): NoticeGetListResponse?
}
