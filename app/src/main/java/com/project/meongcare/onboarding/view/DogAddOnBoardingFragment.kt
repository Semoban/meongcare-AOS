package com.project.meongcare.onboarding.view

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.project.meongcare.MainActivity
import com.project.meongcare.PhotoSelectBottomSheetFragment
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentDogAddOnBoardingBinding
import com.project.meongcare.onboarding.model.data.local.PhotoMenuListener
import com.project.meongcare.onboarding.viewmodel.DogAddViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DogAddOnBoardingFragment : Fragment(), PhotoMenuListener {
    lateinit var fragmentDogAddOnBoardingBinding: FragmentDogAddOnBoardingBinding
    lateinit var mainActivity: MainActivity

    private val dogAddViewModel: DogAddViewModel by viewModels()

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
                modalBottomSheet.setPhotoMenuListener(this@DogAddOnBoardingFragment)
                // 둥근 모서리 지정
                modalBottomSheet.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerPhotoDialogTheme)
                modalBottomSheet.show(mainActivity.supportFragmentManager, modalBottomSheet.tag)
            }
        }

        return fragmentDogAddOnBoardingBinding.root
    }

    override fun onBitmapPassed(bitmap: Bitmap) {
        dogAddViewModel.getDogProfileImage(bitmap)

        fragmentDogAddOnBoardingBinding.run {
            imageviewPetaddImage.setImageBitmap(bitmap)
            imageviewPetaddDog.visibility = View.GONE
            textviewPetaddImageDescription.visibility = View.GONE
        }
    }
}
