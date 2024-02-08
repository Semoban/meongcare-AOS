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

    private val yearArray = arrayOf(
        "2024년", "2023년", "2022년", "2021년", "2020년",
        "2019년", "2018년", "2017년", "2016년", "2015년", "2014년", "2013년", "2012년", "2011년", "2010년",
        "1999년", "1998년", "1997년", "1996년", "1995년", "1994년", "1993년", "1992년", "1991년", "1990년",
    )
    private val monthArray = arrayOf(
        "1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월",
    )
    private val dayArray = arrayOf(
        "1일", "2일", "3일", "4일", "5일", "6일", "7일", "8일", "9일", "10일",
        "11일", "12일", "13일", "14일", "15일", "16일", "17일", "18일", "19일", "20일",
        "21일", "22일", "23일", "24일", "25일", "26일", "27일", "28일", "29일", "30일",
        "31일",
    )

    private var dateSubmitListener: DateSubmitListener? = null
    private var selectedYear: String? = null
    private var selectedMonth: String? = null
    private var selectedDay: String? = null

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
                val birthYear = getDateSubString(yearArray[numberpickerYear.value])
                val birthMonth = getDateSubString(monthArray[numberpickerMonth.value])
                val birthDay = getDateSubString(dayArray[numberpickerDay.value])

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
                maxValue = yearArray.size - 1
                displayedValues = yearArray
                value = yearArray.indexOf(selectedYear ?: "2024년")
            }
            numberpickerMonth.run {
                wrapSelectorWheel = false
                minValue = 0
                maxValue = monthArray.size - 1
                displayedValues = monthArray
                value = monthArray.indexOf(selectedMonth ?: "1월")
            }
            numberpickerDay.run {
                wrapSelectorWheel = false
                minValue = 0
                maxValue = dayArray.size - 1
                displayedValues = dayArray
                value = dayArray.indexOf(selectedDay ?: "1일")
            }
        }

        return binding.root
    }

    fun setDateSubmitListener(dateSubmitListener: DateSubmitListener) {
        this.dateSubmitListener = dateSubmitListener
    }

    private fun getDateSubString(str: String): Int {
        return str.substring(0 until str.length - 1).toInt()
    }

    private fun formatSelectedDate(str: String?) {
        if (str != null) {
            val (year, month, day) = str.split("-").map { it }

            selectedYear = year + "년"
            selectedMonth = month + "월"
            selectedDay = day + "일"
        }
    }

    private fun getBirthDate(year: Int, month: Int, day: Int): String {
        val birthMonth = if (month.toString().length == 1) {
            "0$month"
        } else {
            "$month"
        }

        val birthDay = if (day.toString().length == 1) {
            "0$day"
        } else {
            "$day"
        }

        return "$year-$birthMonth-$birthDay"
    }
}
