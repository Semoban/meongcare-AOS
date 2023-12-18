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
import com.project.meongcare.onboarding.model.data.local.DateSubmitListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(), DateSubmitListener {
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
                Glide.with(this)
                    .load(homeProfileResponse.imageUrl)
                    .placeholder(R.drawable.home_profile_default_image)
                    .error(R.drawable.home_profile_default_image)
                    .into(fragmentHomeBinding.imageviewHomeProfile)
            }
        }

        homeViewModel.homeSelectedDate.observe(viewLifecycleOwner) { selectedDate ->
            if (selectedDate != null) {
                Log.d("homeViewmodel-selectedDate", selectedDate)
                // 가로 달력 날짜 selectedDate로 설정

                // 홈에서 연결되는 api에 selectedDate로 request 전송해서 데이터 설정
            }
        }

        fragmentHomeBinding.run {
//            homeViewModel.getUserProfile(currentAccessToken)
            currentAccessToken = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwiZXhwIjoxNzAyNjIyMzI3fQ.MMgPi787_nBf_QuH0-YCCTN6Tbh3QDpNyPGr_38PZ3Q"
            homeViewModel.getUserProfile(currentAccessToken)

            // 현재 날짜로 기본값 설정
            if (homeViewModel.homeSelectedDate.value == null) {
                homeViewModel.setSelectedDate(getCurrentDate())
            }

            imageviewHomeCalendar.setOnClickListener {
                val modalBottomSheet = CalendarBottomSheetFragment()
                modalBottomSheet.setDateSubmitListener(this@HomeFragment)
                modalBottomSheet.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerCalendarDialogTheme)
                modalBottomSheet.show(mainActivity.supportFragmentManager, modalBottomSheet.tag)
            }

            imageviewHomeAlert.setOnClickListener {
                // 알림 화면으로 전환
            }

            imageviewHomeAddDog.setOnClickListener {
                // 강아지 정보 등록 화면으로 전환
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

    override fun onDateSubmit(str: String) {
        homeViewModel.setSelectedDate(str)
    }

    private fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return currentDate.format(formatter)
    }
}
