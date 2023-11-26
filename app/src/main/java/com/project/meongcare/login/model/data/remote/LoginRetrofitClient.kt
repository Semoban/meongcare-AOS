package com.project.meongcare.login.model.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Inject

class LoginRetrofitClient @Inject constructor(){
    private val BASE_URL = "http://dev.meongcare.com"

    val loginApi : LoginApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LoginApi::class.java)
    }
}