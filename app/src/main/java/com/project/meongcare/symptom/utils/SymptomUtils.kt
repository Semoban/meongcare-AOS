package com.project.meongcare.symptom.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentManager
import com.project.meongcare.R
import com.project.meongcare.symptom.model.entities.Symptom
import com.project.meongcare.symptom.model.entities.SymptomType
import com.project.meongcare.symptom.view.SymptomBottomSheetDialogFragment
import com.project.meongcare.symptom.viewmodel.SymptomViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class SymptomUtils {
    companion object {
        fun convertDateToTime(localMili: String): String {
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            val dateTime = LocalDateTime.parse(localMili, inputFormatter)

            val outputFormatter = DateTimeFormatter.ofPattern("a h:mm", Locale.getDefault())
            return dateTime.format(outputFormatter)
        }

        fun convertDateToSimpleTime(localMili: String): String {
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            val dateTime = LocalDateTime.parse(localMili, inputFormatter)

            val outputFormatter = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.getDefault())
            return dateTime.format(outputFormatter)
        }

        fun convertDateToMonthDate(localMili: String): String {
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            val dateTime = LocalDateTime.parse(localMili, inputFormatter)

            val outputFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일", Locale.getDefault())
            return dateTime.format(outputFormatter)
        }

        fun convertSimpleDateToMonthDate(localMili: String): String {
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            val dateTime = LocalDateTime.parse(localMili, inputFormatter)
            val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
            return dateTime.format(outputFormatter)
        }

        fun convertToLocalDateToDate(localDate: LocalDate): Date {
            return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
        }

        fun convertToDateToLocale(date: Date): LocalDateTime {
            // Date를 Instant로 변환
            val instant: Instant = date.toInstant()

            // Instant를 ZoneId를 사용하여 LocalDateTime으로 변환
            return instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
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

        fun getSymptomName(symptomImg: Int): String {
            return when (symptomImg) {
                R.drawable.all_weighing_machine -> SymptomType.WEIGHT_LOSS.symptomName
                R.drawable.all_temperature_measurement -> SymptomType.HIGH_FEVER.symptomName
                R.drawable.symptom_cough -> SymptomType.COUGH.symptomName
                R.drawable.symptom_diarrhea -> SymptomType.DIARRHEA.symptomName
                R.drawable.symptom_loss_appetite -> SymptomType.LOSS_OF_APPETITE.symptomName
                R.drawable.symptom_amount_activity -> SymptomType.ACTIVITY_DECREASE.symptomName
                else -> SymptomType.ETC.symptomName
            }
        }

        fun hideKeyboard(view: View) {
            val inputMethodManager =
                view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun showCalendarBottomSheet(parentFragmentManager: FragmentManager, onDateSelectedListener: SymptomBottomSheetDialogFragment.OnDateSelectedListener) {
            val bottomSheetDialogFragment = SymptomBottomSheetDialogFragment()
            bottomSheetDialogFragment.setOnDateSelecetedListener(onDateSelectedListener)
            bottomSheetDialogFragment.show(parentFragmentManager, "SymptomBottomSheetDialogFragment")
        }
    }
}
