package com.project.meongcare.notice.model.data.remote

import com.project.meongcare.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class NoticeRetrofitClient
    @Inject
    constructor() {
        val noticeApi: NoticeApi by lazy {
            Retrofit.Builder()
                .baseUrl(BuildConfig.SEMOBAN_DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NoticeApi::class.java)
        }
    }
