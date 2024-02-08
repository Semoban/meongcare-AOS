package com.project.meongcare.home.view

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.widget.ProgressBar
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.ThreeBounce
import com.project.meongcare.R

class LoadingDialog(
    private val context: Context,
) : Dialog(context) {
    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.loading_dialog)

        val progressBar = findViewById<ProgressBar>(R.id.progress_view)
        val threeBounce: Sprite = ThreeBounce()
        progressBar.indeterminateDrawable = threeBounce
    }
}
