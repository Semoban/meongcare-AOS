package com.project.meongcare.symptom.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentManager
import com.project.meongcare.LocaleDateTimeFormats
import com.project.meongcare.R
import com.project.meongcare.symptom.model.entities.Symptom
import com.project.meongcare.symptom.model.entities.SymptomType
import com.project.meongcare.symptom.view.bottomSheet.SymptomBottomSheetDialogFragment
import java.text.SimpleDateFormat
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

            return dateTime.format(LocaleDateTimeFormats.time12hShort())
        }

        fun convertDateToMonthDate(localMili: String): String {
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            val dateTime = LocalDateTime.parse(localMili, inputFormatter)

            return dateTime.format(LocaleDateTimeFormats.datePadded())
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
            val instant: Instant = date.toInstant()
            return instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
        }

        fun convertToDateToMiliSec(date: Date): String {
            val instant: Instant = date.toInstant()
            val localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            return localDateTime.format(formatter)
        }

        fun convertDateToSimpleTime(inputDateString: String): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val date: Date = inputFormat.parse(inputDateString)
            val outputFormat = SimpleDateFormat("HH:mm:ss")
            return outputFormat.format(date)
        }

        fun getSymptomImg(symptomData: Symptom): Int {
            return when (symptomData.symptomString) {
                SymptomType.WEIGHT_LOSS.symptomName -> R.drawable.all_weighing_machine
                SymptomType.HIGH_FEVER.symptomName -> R.drawable.all_temperature_measurement
                SymptomType.COUGH.symptomName -> R.drawable.symptom_cough
                SymptomType.DIARRHEA.symptomName -> R.drawable.symptom_diarrhea
                SymptomType.LOSS_OF_APPETITE.symptomName -> R.drawable.symptom_loss_appetite
                SymptomType.ACTIVITY_DECREASE.symptomName -> R.drawable.symptom_amount_activity
                else -> R.drawable.symptom_etc_record
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

        // 프리셋 증상은 서버에 로케일 무관 키(symptomString)로 저장되므로
        // 표시 제목은 현재 로케일 리소스로 변환하고, etc(직접 입력)만 note를 그대로 쓴다
        @StringRes
        fun getSymptomTitleRes(symptomString: String): Int? {
            return when (symptomString) {
                SymptomType.WEIGHT_LOSS.symptomName -> R.string.symptom_type_weight_loss
                SymptomType.HIGH_FEVER.symptomName -> R.string.symptom_type_high_fever
                SymptomType.COUGH.symptomName -> R.string.symptom_type_cough
                SymptomType.DIARRHEA.symptomName -> R.string.symptom_type_diarrhea
                SymptomType.LOSS_OF_APPETITE.symptomName -> R.string.symptom_type_loss_of_appetite
                SymptomType.ACTIVITY_DECREASE.symptomName -> R.string.symptom_type_activity_decrease
                else -> null
            }
        }

        fun getSymptomTitle(
            context: Context,
            symptomString: String,
            note: String,
        ): String {
            val titleRes = getSymptomTitleRes(symptomString) ?: return note
            return context.getString(titleRes)
        }

        fun hideKeyboard(view: View) {
            val inputMethodManager =
                view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun showCalendarBottomSheet(
            parentFragmentManager: FragmentManager,
            onDateSelectedListener: SymptomBottomSheetDialogFragment.OnDateSelectedListener,
        ) {
            val bottomSheetDialogFragment = SymptomBottomSheetDialogFragment()
            bottomSheetDialogFragment.setOnDateSelecetedListener(onDateSelectedListener)
            bottomSheetDialogFragment.show(parentFragmentManager, "SymptomBottomSheetDialogFragment")
        }
    }
}
