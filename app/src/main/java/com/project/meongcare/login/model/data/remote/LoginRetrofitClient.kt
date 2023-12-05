package com.project.meongcare.login.model.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Inject

class LoginRetrofitClient
@Inject
constructor() {
    val loginApi: LoginApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://dev.meongcare.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LoginApi::class.java)
    }
}
