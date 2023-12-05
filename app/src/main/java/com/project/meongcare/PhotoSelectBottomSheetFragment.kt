package com.project.meongcare

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.meongcare.databinding.FragmentPhotoSelectBottomSheetBinding

class PhotoSelectBottomSheetFragment : BottomSheetDialogFragment() {
    lateinit var fragmentPhotoSelectBottomSheetBinding: FragmentPhotoSelectBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentPhotoSelectBottomSheetBinding =
            FragmentPhotoSelectBottomSheetBinding.inflate(inflater)
        return fragmentPhotoSelectBottomSheetBinding.root
    }
}
