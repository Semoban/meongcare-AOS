package com.project.meongcare

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.meongcare.databinding.FragmentBirthdayBottomSheetBinding
import com.project.meongcare.onboarding.model.data.local.DateSubmitListener
import com.project.meongcare.snackbar.view.CustomSnackBar
import java.time.LocalDate

class BirthdayBottomSheetFragment(
    private val parentView: View,
    private val selectedDate: String?,
) : BottomSheetDialogFragment() {
    lateinit var binding: FragmentBirthdayBottomSheetBinding

    private val years = (LocalDate.now().year downTo OLDEST_BIRTH_YEAR).toList()
    private val months = (1..12).toList()
    private val days = (1..31).toList()

    private var dateSubmitListener: DateSubmitListener? = null
    private var selectedYear: Int? = null
    private var selectedMonth: Int? = null
    private var selectedDay: Int? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        val peekHeightInPixels = 0

        val behavior = dialog.behavior
        if (behavior != null) {
            behavior.peekHeight = peekHeightInPixels
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentBirthdayBottomSheetBinding.inflate(inflater)
        formatSelectedDate(selectedDate)

        binding.run {
            buttonBirthdaySubmit.setOnClickListener {
                val birthYear = years[numberpickerYear.value]
                val birthMonth = months[numberpickerMonth.value]
                val birthDay = days[numberpickerDay.value]

                val currentDate = LocalDate.now()
                val currentYear = currentDate.year
                val currentMonth = currentDate.monthValue
                val currentDay = currentDate.dayOfMonth

                when {
                    ((birthYear == currentYear) && (birthMonth > currentMonth)) ||
                        ((birthYear == currentYear) && (birthMonth == currentMonth) && (birthDay > currentDay)) -> {
                        dismiss()
                        CustomSnackBar.make(
                            parentView,
                            R.drawable.snackbar_error_16dp,
                            getString(R.string.snack_bar_future_date),
                        ).show()
                    }
                    else -> {
                        dateSubmitListener?.onDateSubmit(getBirthDate(birthYear, birthMonth, birthDay))
                        dismiss()
                    }
                }
            }

            numberpickerYear.run {
                wrapSelectorWheel = false
                minValue = 0
                maxValue = years.size - 1
                displayedValues = years.map { getString(R.string.all_year_format, it) }.toTypedArray()
                value = years.indexOf(selectedYear ?: years.first())
            }
            numberpickerMonth.run {
                wrapSelectorWheel = false
                minValue = 0
                maxValue = months.size - 1
                displayedValues = months.map { getString(R.string.all_month_format, it) }.toTypedArray()
                value = months.indexOf(selectedMonth ?: 1)
            }
            numberpickerDay.run {
                wrapSelectorWheel = false
                minValue = 0
                maxValue = days.size - 1
                displayedValues = days.map { getString(R.string.all_day_format, it) }.toTypedArray()
                value = days.indexOf(selectedDay ?: 1)
            }
        }

        return binding.root
    }

    fun setDateSubmitListener(dateSubmitListener: DateSubmitListener) {
        this.dateSubmitListener = dateSubmitListener
    }

    private fun formatSelectedDate(str: String?) {
        if (str != null) {
            val (year, month, day) = str.split("-").map { it.toInt() }

            selectedYear = year
            selectedMonth = month
            selectedDay = day
        }
    }

    private fun getBirthDate(
        year: Int,
        month: Int,
        day: Int,
    ): String {
        val birthMonth =
            if (month.toString().length == 1) {
                "0$month"
            } else {
                "$month"
            }

        val birthDay =
            if (day.toString().length == 1) {
                "0$day"
            } else {
                "$day"
            }

        return "$year-$birthMonth-$birthDay"
    }

    companion object {
        private const val OLDEST_BIRTH_YEAR = 1990
    }
}
