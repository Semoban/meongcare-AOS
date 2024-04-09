package com.project.meongcare.excreta.utils

import android.view.View
import com.project.meongcare.R
import com.project.meongcare.snackbar.view.CustomSnackBar

object ExcretaInfoUtils {
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
