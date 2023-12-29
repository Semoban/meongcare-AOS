package com.project.meongcare.notice.model.entities

data class NoticeGetListResponse(
    val records: MutableList<Notice>
)

data class Notice(
    val noticeId: Long,
    val title: String,
    val text: String,
    val lastUpdateTime: String,
)
