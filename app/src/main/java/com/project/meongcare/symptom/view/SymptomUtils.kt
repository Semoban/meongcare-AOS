package com.project.meongcare.symptom.view

import com.project.meongcare.R
import com.project.meongcare.symptom.model.entities.Symptom
import com.project.meongcare.symptom.model.entities.SymptomType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class SymptomUtils {
    companion object{
        fun convertDateToTime(localMili: String): String {
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            val dateTime = LocalDateTime.parse(localMili, inputFormatter)

            // LocalDateTime을 오전/오후 시간 형식으로 포맷
            val outputFormatter = DateTimeFormatter.ofPattern("a h:mm", Locale.getDefault())
            return dateTime.format(outputFormatter)
        }

        fun convertDateToMonthDate(localMili: String): String {
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            val dateTime = LocalDateTime.parse(localMili, inputFormatter)

            // LocalDateTime을 오전/오후 시간 형식으로 포맷
            val outputFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일", Locale.getDefault())
            return dateTime.format(outputFormatter)
        }

        fun getSymptomImg(symptomData: Symptom): Int {
            return when (symptomData.symptomString) {
                SymptomType.WEIGHT_LOSS.symptomName -> R.drawable.all_weighing_machine
                SymptomType.HIGH_FEVER.symptomName -> R.drawable.all_temperature_measurement
                SymptomType.COUGH.symptomName -> R.drawable.symptom_cough
                SymptomType.DIARRHEA.symptomName -> R.drawable.symptom_diarrhea
                SymptomType.LOSS_OF_APPETITE.symptomName -> R.drawable.symptom_loss_appetite
                SymptomType.ACTIVITY_DECREASE.symptomName -> R.drawable.symptom_amount_activity
                else -> R.drawable.symptom_stethoscope
            }
        }


    }
}