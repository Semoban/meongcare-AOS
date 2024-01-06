package com.project.meongcare.Information.view

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.project.meongcare.Information.viewmodel.ProfileViewModel
import com.project.meongcare.MainActivity
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentProfileBinding
import com.project.meongcare.onboarding.model.data.local.PhotoMenuListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment(), PhotoMenuListener {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var mainActivity: MainActivity

    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentProfileBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        profileViewModel.userProfile.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                binding.run {
                    Glide.with(this@ProfileFragment)
                        .load(response.imageUrl)
                        .error(R.drawable.profile_default_image)
                        .into(imageviewProfileImage)
                    textviewProfileEmail.text = response.email
                }
            }
        }

        profileViewModel.dogList.observe(viewLifecycleOwner) { dogList ->
            if (dogList.isNotEmpty()) {
                binding.textViewNoDog.visibility = View.GONE
                binding.recyclerviewProfilePetList.visibility = View.VISIBLE
                val adapter = binding.recyclerviewProfilePetList.adapter as ProfileDogAdapter
                adapter.updateDogList(dogList)
            } else {
                binding.textViewNoDog.visibility = View.VISIBLE
                binding.recyclerviewProfilePetList.visibility = View.GONE
            }
        }

        val accessToken = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MywiZXhwIjoxNzA0NTA4MzA4fQ.9blniYll3w_xR6mOfyFcquFhLT5bmGOWrRwj9cZvpz0"
        profileViewModel.getUserProfile(accessToken)
        profileViewModel.getDogList(accessToken)

        binding.run {
            imagebuttonProfileBack.setOnClickListener {
                findNavController().popBackStack()
            }

            imageviewProfileImage.setOnClickListener {
                val modalBottomSheet = UserProfileSelectBottomSheetFragment()
                modalBottomSheet.setPhotoMenuListener(this@ProfileFragment)
                modalBottomSheet.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerPhotoDialogTheme)
                modalBottomSheet.show(mainActivity.supportFragmentManager, modalBottomSheet.tag)
            }

            recyclerviewProfilePetList.run {
                adapter = ProfileDogAdapter(layoutInflater, context)
                layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false)
            }

            buttonProfileSetting.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_settingFragment)
            }
        }

        return binding.root
    }

    override fun onUriPassed(uri: Uri) {
        // uri로 회원 정보 patch api 연결한 후 통신 정상이면 이미지 뷰에 표시되도록 로직 변경
        binding.run {
            Glide.with(this@ProfileFragment)
                .load(uri)
                .error(R.drawable.profile_default_image)
                .into(imageviewProfileImage)
        }
    }
}
