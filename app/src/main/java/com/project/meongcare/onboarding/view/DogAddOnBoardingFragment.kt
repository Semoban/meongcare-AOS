package com.project.meongcare.onboarding.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.DialogFragment
import com.project.meongcare.MainActivity
import com.project.meongcare.PhotoSelectBottomSheetFragment
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentDogAddOnBoardingBinding

class DogAddOnBoardingFragment : Fragment() {
    lateinit var fragmentDogAddOnBoardingBinding: FragmentDogAddOnBoardingBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        fragmentDogAddOnBoardingBinding = FragmentDogAddOnBoardingBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        fragmentDogAddOnBoardingBinding.run {
            cardviewPetaddImage.setOnClickListener {
                val modalBottomSheet = PhotoSelectBottomSheetFragment()
                modalBottomSheet.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerPhotoDialogTheme)
                modalBottomSheet.show(mainActivity.supportFragmentManager, modalBottomSheet.tag)
            }
        }

        return fragmentDogAddOnBoardingBinding.root
    }
}
