package com.project.meongcare.supplement.utils

import androidx.fragment.app.FragmentManager
import com.project.meongcare.LocaleDateTimeFormats
import com.project.meongcare.R
import com.project.meongcare.login.view.GlobalApplication
import com.project.meongcare.supplement.model.entities.IntakeInfo
import com.project.meongcare.supplement.view.bottomSheet.SupplementCycleBottomSheetDialogFragment
import com.project.meongcare.supplement.view.bottomSheet.SupplementTimeBottomSheetDialogFragment
import com.project.meongcare.supplement.viewmodel.SupplementViewModel
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Date

class SupplementUtils {
    companion object {
        fun convertDateToTime(inputTime: String): String {
            try {
                val inputFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
                val time = LocalTime.parse(inputTime, inputFormatter)
                return time.format(LocaleDateTimeFormats.time12h())
            } catch (e: DateTimeParseException) {
                return GlobalApplication.applicationContext().getString(R.string.datetime_time_error)
            }
        }

        fun convertToDateToDate(date: Date): String {
            val instant: Instant = date.toInstant()
            val localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            return localDateTime.format(formatter)
        }

        fun showCycleBottomSheet(
            parentFragmentManager: FragmentManager,
            supplementViewModel: SupplementViewModel,
        ) {
            val bottomSheetFragment = SupplementCycleBottomSheetDialogFragment()

            bottomSheetFragment.onNumberCycleChangedListener =
                object : SupplementCycleBottomSheetDialogFragment.OnNumberCycleChangedListener {
                    override fun onNumberCycleChanged(number: Int) {
                        supplementViewModel.updateSupplementCycle(number)
                    }
                }

            bottomSheetFragment.show(
                parentFragmentManager,
                "SupplementCycleBottomSheetDialogFragment",
            )
        }

        fun showTimeBottomSheet(
            parentFragmentManager: FragmentManager,
            supplementViewModel: SupplementViewModel,
        ) {
            val bottomSheetFragment = SupplementTimeBottomSheetDialogFragment()
            bottomSheetFragment.onNumberTimeChangedListener =
                object : SupplementTimeBottomSheetDialogFragment.OnNumberTimeChangedListener {
                    override fun onNumberTimeChanged(
                        number: Int,
                        time: String,
                    ) {
                        val intakeInfo = IntakeInfo(time, number)
                        supplementViewModel.addIntakeInfoList(intakeInfo)
                    }
                }

            bottomSheetFragment.show(
                parentFragmentManager,
                "SupplementTimeBottomSheetDialogFragment",
            )
        }
    }
}
