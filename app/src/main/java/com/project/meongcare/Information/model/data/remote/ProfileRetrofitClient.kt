package com.project.meongcare.Information.model.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class ProfileRetrofitClient
    @Inject
    constructor() {
        val profileApi: ProfileApi by lazy {
            Retrofit.Builder()
                .baseUrl("https://dev.meongcare.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ProfileApi::class.java)
        }
    }
