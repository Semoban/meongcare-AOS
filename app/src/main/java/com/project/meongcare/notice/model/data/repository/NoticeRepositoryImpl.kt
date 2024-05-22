package com.project.meongcare.notice.model.data.repository

import android.util.Log
import com.project.meongcare.RetrofitClient
import com.project.meongcare.notice.model.data.remote.NoticeApi
import com.project.meongcare.notice.model.entities.NoticeGetListResponse
import javax.inject.Inject

class NoticeRepositoryImpl
    @Inject
    constructor(retrofitClient: RetrofitClient) : NoticeRepository {
        private val noticeApi = retrofitClient.createApi<NoticeApi>()
        override suspend fun getNoticeList(type: String): NoticeGetListResponse? {
            try {
                val response = noticeApi.getNoticeList(type)
                if (response.isSuccessful) {
                    Log.d("NoticeRepo-NoticeList", "통신 성공 : ${response.code()}")
                    return response.body()
                } else {
                    Log.d("NoticeRepo-NoticeList", "통신 실패 : ${response.code()}")
                    return null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
    }
