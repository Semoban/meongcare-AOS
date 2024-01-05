package com.project.meongcare.Information.view

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.project.meongcare.MainActivity
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentProfileBinding
import com.project.meongcare.onboarding.model.data.local.PhotoMenuListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment(), PhotoMenuListener {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentProfileBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        binding.run {
            imagebuttonProfileBack.setOnClickListener {
                // 이전 화면으로 이동
            }

            imageviewProfileImage.setOnClickListener {
                val modalBottomSheet = UserProfileSelectBottomSheetFragment()
                modalBottomSheet.setPhotoMenuListener(this@ProfileFragment)
                modalBottomSheet.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerPhotoDialogTheme)
                modalBottomSheet.show(mainActivity.supportFragmentManager, modalBottomSheet.tag)
            }
        }

        return binding.root
    }

    override fun onUriPassed(uri: Uri) {
        binding.run {
            Glide.with(this@ProfileFragment)
                .load(uri)
                .error(R.drawable.profile_default_image)
                .into(imageviewProfileImage)
        }
    }
}
