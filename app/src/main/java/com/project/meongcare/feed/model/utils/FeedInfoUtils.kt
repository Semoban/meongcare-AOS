package com.project.meongcare.feed.model.utils

import android.view.View
import android.widget.TextView
import com.project.meongcare.R
import com.project.meongcare.snackbar.view.CustomSnackBar

object FeedInfoUtils {
    fun calculateRecommendDailyIntake(
        weight: Double,
        feedKcal: Double,
    ): Double {
        val dailyEnergyRequirement = 1.6 * (30 * weight + 70)
        val recommendDailyIntake = dailyEnergyRequirement * 1000 / feedKcal
        return String.format("%.2f", recommendDailyIntake).toDouble()
    }

    fun initRecommendDailyIntake(
        recommendIntake: Double,
        textView: TextView,
    ) {
        textView.apply {
            text =
                if (recommendIntake == Double.POSITIVE_INFINITY) {
                    "0"
                } else {
                    "$recommendIntake"
                }
        }
    }

    fun showSuccessSnackBar(
        view: View,
        message: String,
    ) {
        CustomSnackBar.make(
            view,
            R.drawable.snackbar_success_16dp,
            message,
        ).show()
    }

    fun showFailureSnackBar(
        view: View,
        message: String,
    ) {
        CustomSnackBar.make(
            view,
            R.drawable.snackbar_error_16dp,
            message,
        ).show()
    }
}
