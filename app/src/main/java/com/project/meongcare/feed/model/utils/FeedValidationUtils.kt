package com.project.meongcare.feed.model.utils

import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import com.project.meongcare.R

object FeedValidationUtils {
    fun validationBrandAndFeedName(
        editText: EditText,
        errorMessage: TextView,
        scrollview: ScrollView,
        title: TextView,
        inputMethodManager: InputMethodManager,
    ) {
        errorMessage.apply {
            visibility = View.VISIBLE
            scrollview.smoothScrollTo(0, title.top)
            setOnClickListener {
                visibility = View.GONE
                editText.requestFocus()
                inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    fun validationIngredient(
        errorMessage: TextView,
        editText: EditText,
        scrollview: ScrollView,
        ingredientTitle: TextView,
    ) {
        errorMessage.visibility = View.VISIBLE
        editText.apply {
            setTextColor(resources.getColor(R.color.sub1, null))
            setBackgroundResource(R.drawable.all_rect_gray1_r5_outline_sub1)
            scrollview.smoothScrollTo(0, ingredientTitle.top)
            requestFocus()
        }
    }

    fun validationTotalIngredient(
        errorMessage: TextView,
        scrollview: ScrollView,
        ingredientTitle: TextView,
    ) {
        errorMessage.apply {
            text = "사료 성분 비율의 총합은 100%를 초과할 수 없습니다."
            visibility = View.VISIBLE
        }
        scrollview.smoothScrollTo(0, ingredientTitle.top)
    }

    fun validationKcal(
        editText: EditText,
        errorMessage: TextView,
    ) {
        errorMessage.visibility = View.VISIBLE
        editText.apply {
            setTextColor(resources.getColor(R.color.sub1, null))
            setBackgroundResource(R.drawable.all_rect_white_r5_outline_sub1)
            requestFocus()
        }
    }

    fun validationIntakePeriod(
        textView: TextView,
        errorMessage: TextView,
    ) {
        errorMessage.apply {
            text = "섭취 기간은 필수 항목입니다."
            visibility = View.VISIBLE
        }
        textView.apply {
            setTextColor(resources.getColor(R.color.sub1, null))
            setBackgroundResource(R.drawable.all_rect_gray1_r5_outline_sub1)
        }
    }
}
