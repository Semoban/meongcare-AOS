package com.project.meongcare.supplement.model.data.repository

import com.project.meongcare.supplement.model.entities.DetailSupplement
import com.project.meongcare.supplement.model.entities.DogSupplement
import com.project.meongcare.supplement.model.entities.ResultSupplement
import com.project.meongcare.supplement.model.entities.SupplementPostRequest
import okhttp3.ResponseBody

interface SupplementRepository {
    suspend fun getSupplements(
        accessToken: String?,
        dogId: Long?,
        date: String,
    ): Result<ResultSupplement>

    suspend fun getSupplementDetail(
        accessToken: String?,
        supplementsId: Int,
    ): Result<DetailSupplement>

    suspend fun getSupplementDogs(
        accessToken: String?,
        dogId: Long?,
    ): Result<DogSupplement>

    suspend fun checkSupplement(
        accessToken: String?,
        supplementsRecordId: Int,
    ): Result<ResponseBody>

    suspend fun patchSupplementAlarm(
        accessToken: String?,
        supplementsId: Int,
        pushAgreement: Boolean,
    ): Result<ResponseBody>

    suspend fun patchSupplementActive(
        accessToken: String?,
        supplementsId: Int,
        isActive: Boolean,
    ): Result<ResponseBody>

    suspend fun deleteSupplementsById(
        accessToken: String?,
        supplementsIds: IntArray,
    ): Int

    suspend fun deleteSupplementById(
        accessToken: String?,
        supplementsId: Int,
    ): Int

    suspend fun addSupplement(
        accessToken: String?,
        supplementsPostRequest: SupplementPostRequest,
    ): Int
}
