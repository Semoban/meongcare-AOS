package com.project.meongcare.medicalRecord.model.data.repository

import com.project.meongcare.medicalRecord.model.data.remote.MedicalRecordAPI
import com.project.meongcare.medicalRecord.model.data.remote.MedicalRecordRetrofitInstance
import com.project.meongcare.medicalRecord.model.entities.RequestMedicalRecord
import javax.inject.Inject

class MedicalRecordRepository
@Inject
constructor() {
    companion object {
        private val medicalRecordAPI: MedicalRecordAPI =
            MedicalRecordRetrofitInstance.getInstance().create(MedicalRecordAPI::class.java)
    }

    suspend fun addMedicalRecord(
        accessToken: String?,
        requestMedicalRecord: RequestMedicalRecord,
    ): Int {
        val response =
            medicalRecordAPI.addMedicalRecord(
                accessToken,
                requestMedicalRecord.file,
                requestMedicalRecord.dto,
            )
        return response.code()
    }
}
