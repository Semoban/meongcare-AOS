package com.project.meongcare.supplement.view

import androidx.fragment.app.FragmentManager
import com.project.meongcare.supplement.viewmodel.SupplementViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class SupplementUtils {
    companion object {
        fun convertDateToTime(localMili: String): String {
            val inputFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
            val dateTime = LocalDateTime.parse(localMili, inputFormatter)

            val outputFormatter = DateTimeFormatter.ofPattern("a h:mm", Locale.getDefault())
            return dateTime.format(outputFormatter)
        }

        fun convertToDateToMiliSec(date: Date): String {
            val instant: Instant = date.toInstant()
            val localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            return localDateTime.format(formatter)
        }

        fun showCycleBottomSheet(
            parentFragmentManager: FragmentManager,
            supplementViewModel: SupplementViewModel
        ) {
            val bottomSheetFragment = SupplementCycleBottomSheetDialogFragment()

            bottomSheetFragment.onNumberCycleChangedListener =
                object : SupplementCycleBottomSheetDialogFragment.OnNumberCycleChangedListener {
                    override fun onNumberCycleChanged(number: Int) {
                        supplementViewModel.supplementCycle.value = number
                    }
                }

            bottomSheetFragment.show(parentFragmentManager, "SupplementCycleBottomSheetDialogFragment")
        }
    }
}
