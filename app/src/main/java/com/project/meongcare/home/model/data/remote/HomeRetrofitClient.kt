package com.project.meongcare.home.model.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class HomeRetrofitClient
    @Inject
    constructor() {
        val homeApi: HomeApi by lazy {
            Retrofit.Builder()
                .baseUrl("https://dev.meongcare.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(HomeApi::class.java)
        }
    }
