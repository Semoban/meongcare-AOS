package com.project.meongcare.snackbar.view

import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.project.meongcare.databinding.LayoutSnackbarBinding

class CustomSnackBar(view: View, private val drawable: Int, private val message: String) {
    companion object {
        fun make(
            view: View,
            drawable: Int,
            message: String,
        ) = CustomSnackBar(view, drawable, message)
    }

    private val context = view.context
    private val snackbar = Snackbar.make(view, "", 1000)
    private val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout

    private val inflater = LayoutInflater.from(context)
    private val snackbarBinding: LayoutSnackbarBinding =
        LayoutSnackbarBinding.inflate(inflater, null, false)

    init {
        initView()
        initData()
    }

    private fun initView() {
        with(snackbarLayout) {
            removeAllViews()
            setPadding(0, 0, 0, 0)
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
            addView(snackbarBinding.root, 0)
        }
    }

    private fun initData() {
        snackbarBinding.textViewLayoutSnackbar.text = message
        snackbarBinding.imageViewLayoutSnackbar.setImageResource(drawable)
    }

    fun show() {
        snackbar.show()
    }

}