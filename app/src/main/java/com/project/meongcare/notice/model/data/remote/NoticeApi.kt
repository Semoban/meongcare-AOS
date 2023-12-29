package com.project.meongcare.notice.model.data.remote

import com.project.meongcare.notice.model.entities.NoticeGetListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NoticeApi {
    @GET("/notice")
    suspend fun getNoticeList(
        @Query("type") type: String,
    ): Response<NoticeGetListResponse>
}
