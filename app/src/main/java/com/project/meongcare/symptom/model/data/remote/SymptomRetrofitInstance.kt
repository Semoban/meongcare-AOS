package com.project.meongcare.symptom.model.data.remote

import com.project.meongcare.MainActivity
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type

object SymptomRetrofitInstance {
    val BASE_URL = MainActivity.BASE_URL

    val nullOnEmptyConverterFactory =
        object : Converter.Factory() {
            fun converterFactory() = this

            override fun responseBodyConverter(
                type: Type,
                annotations: Array<out Annotation>,
                retrofit: Retrofit,
            ) = object : Converter<ResponseBody, Any?> {
                val nextResponseBodyConverter =
                    retrofit.nextResponseBodyConverter<Any?>(
                        converterFactory(),
                        type,
                        annotations,
                    )

                override fun convert(value: ResponseBody) =
                    if (value.contentLength() != 0L) nextResponseBodyConverter.convert(value) else null
            }
        }

    val client =
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(nullOnEmptyConverterFactory)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    fun getInstance(): Retrofit {
        return client
    }
}
