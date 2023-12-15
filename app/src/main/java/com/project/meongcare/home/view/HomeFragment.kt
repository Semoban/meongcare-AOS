package com.project.meongcare.home.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.project.meongcare.CalendarBottomSheetFragment
import com.project.meongcare.MainActivity
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentHomeBinding
import com.project.meongcare.home.viewmodel.HomeViewModel
import com.project.meongcare.login.model.data.local.UserPreferences
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var fragmentHomeBinding: FragmentHomeBinding
    private lateinit var mainActivity: MainActivity

    private val homeViewModel: HomeViewModel by viewModels()

    @Inject
    lateinit var userPreferences: UserPreferences

    lateinit var currentAccessToken: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater)
        mainActivity = activity as MainActivity

//        getAccessToken()

        homeViewModel.homeProfileResponse.observe(viewLifecycleOwner) { homeProfileResponse ->
            if (homeProfileResponse != null) {
                Log.d("homeViewModel", "response not null")
                Glide.with(this)
                    .load(homeProfileResponse.imageUrl)
                    .error(R.drawable.home_profile_default_image)
                    .into(fragmentHomeBinding.imageviewHomeProfile)
            }
        }

        fragmentHomeBinding.run {
//            homeViewModel.getUserProfile(currentAccessToken)
            currentAccessToken = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwiZXhwIjoxNzAyNjIyMzI3fQ.MMgPi787_nBf_QuH0-YCCTN6Tbh3QDpNyPGr_38PZ3Q"
            homeViewModel.getUserProfile(currentAccessToken)

            imageviewHomeCalendar.setOnClickListener {
                val modalBottomSheet = CalendarBottomSheetFragment()
                modalBottomSheet.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerCalendarDialogTheme)
                modalBottomSheet.show(mainActivity.supportFragmentManager, modalBottomSheet.tag)
            }
        }

        return fragmentHomeBinding.root
    }

    private fun getAccessToken() {
        runBlocking {
            userPreferences.accessToken.collect { accessToken ->
                if (accessToken != null) {
                    currentAccessToken = accessToken
                }
            }
        }
    }
}
