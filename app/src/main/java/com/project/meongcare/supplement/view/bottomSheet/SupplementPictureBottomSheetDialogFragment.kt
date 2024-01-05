package com.project.meongcare.supplement.view.bottomSheet

import android.app.Dialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.meongcare.databinding.BottomsheetSupplementAddPictureBinding
import com.project.meongcare.databinding.BottomsheetSupplementAddTimeBinding

class SupplementPictureBottomSheetDialogFragment : BottomSheetDialogFragment() {
    lateinit var fragmentSupplementPictureBottomSheetBinding: BottomsheetSupplementAddPictureBinding

    interface OnPictureChangedListener {
        fun onPictureChanged(drawable: Drawable)
    }

    var onPictureChangedListener: OnPictureChangedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentSupplementPictureBottomSheetBinding = BottomsheetSupplementAddPictureBinding.inflate(inflater, container, false)

        fragmentSupplementPictureBottomSheetBinding.run {
            textViewSupplementAddPictureCamera.setOnClickListener {
                Log.d("영양제 사진", "카메라 선택")
                dismiss()
            }
            textViewSupplementAddPictureGallery.setOnClickListener {
                Log.d("영양제 사진", "앨범 선택")
                dismiss()
            }

        }

        return fragmentSupplementPictureBottomSheetBinding.root
    }

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
}
