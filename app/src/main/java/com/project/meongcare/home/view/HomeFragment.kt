package com.project.meongcare.home.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.project.meongcare.CalendarBottomSheetFragment
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentHomeBinding
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.home.util.HomeDateUtil.dateFormatter
import com.project.meongcare.home.util.HomeDateUtil.dateToString
import com.project.meongcare.home.util.HomeDateUtil.getCurrentDate
import com.project.meongcare.home.util.HomeDateUtil.stringToDate
import com.project.meongcare.home.viewmodel.HomeViewModel
import com.project.meongcare.medicalRecord.viewmodel.DogViewModel
import com.project.meongcare.medicalRecord.viewmodel.UserViewModel
import com.project.meongcare.onboarding.model.data.local.DateSubmitListener
import com.project.meongcare.snackbar.view.CustomSnackBar
import com.project.meongcare.weight.model.entities.WeightPostRequest
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date

@AndroidEntryPoint
class HomeFragment : Fragment(), DateSubmitListener {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var currentAccessToken = ""
    private var currentRefreshToken = ""

    private val homeViewModel: HomeViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val dogViewModel: DogViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        // 홈 재진입 시 선택 날짜를 오늘로 초기화한다
        homeViewModel.setSelectedDate(
            Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
        )

        initComposeView()
        fetchTokens()
        observeReissueResponse()
        observeUserProfile()
        observeDogList()
        observeSelectedDate()
        observeSelectedDogPos()
        observeSelectedDogId()
        observeDailyRecords()
    }

    private fun initComposeView() {
        binding.composeViewHome.run {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SemobanTheme {
                    val profileResponse by homeViewModel.homeProfileResponse.observeAsState()
                    val dogListResponse by homeViewModel.homeDogList.observeAsState()
                    val selectedDogPos by homeViewModel.homeSelectedDogPos.observeAsState()
                    val dateList by homeViewModel.homeDateList.observeAsState()
                    val selectedDatePos by homeViewModel.homeSelectedDatePos.observeAsState()
                    val weightResponse by homeViewModel.homeDogWeight.observeAsState()
                    val feedResponse by homeViewModel.homeDogFeed.observeAsState()
                    val supplementsResponse by homeViewModel.homeDogSupplements.observeAsState()
                    val excretaResponse by homeViewModel.homeDogExcreta.observeAsState()
                    val symptomResponse by homeViewModel.homeDogSymptom.observeAsState()

                    val dogs = dogListResponse?.body()?.dogs.orEmpty()
                    val showDogNotExist =
                        when (dogListResponse?.code()) {
                            null, 401 -> false
                            200 -> dogs.isEmpty()
                            else -> true
                        }

                    HomeScreen(
                        profileImageUrl = profileResponse?.body()?.imageUrl,
                        dogs = dogs,
                        selectedDogPos = selectedDogPos,
                        showDogNotExist = showDogNotExist,
                        dateList = dateList.orEmpty(),
                        selectedDatePos = selectedDatePos,
                        symptoms = symptomResponse?.body()?.symptomRecords.orEmpty(),
                        fecesCount = excretaResponse?.body()?.fecesCount ?: 0,
                        urineCount = excretaResponse?.body()?.urineCount ?: 0,
                        supplementsRate = supplementsResponse?.body()?.supplementsRate ?: 0,
                        weight = weightResponse?.body()?.weight?.toString() ?: "0.0",
                        feedIntake = (feedResponse?.body()?.recommendIntake ?: 0).toString(),
                        onCalendarClick = ::showCalendarBottomSheet,
                        onAlarmClick = { findNavController().navigate(R.id.action_homeFragment_to_noticeFragment) },
                        onProfileClick = { findNavController().navigate(R.id.action_homeFragment_to_profileFragment) },
                        onDogClick = homeViewModel::setSelectedDogPos,
                        onAddDogClick = ::navigateToDogAdd,
                        onDateClick = ::selectDateAt,
                        onWeekSwipe = ::moveCalendarWeek,
                        onSymptomCardClick = { findNavController().navigate(R.id.action_homeFragment_to_symptomFragment) },
                        onExcretaCardClick = { findNavController().navigate(R.id.action_homeFragment_to_excretaFragment) },
                        onSupplementCardClick = { findNavController().navigate(R.id.action_homeFragment_to_supplementFragment) },
                        onWeightCardClick = { findNavController().navigate(R.id.action_homeFragment_to_weightFragment) },
                        onFeedCardClick = { findNavController().navigate(R.id.action_homeFragment_to_feedFragment) },
                    )
                }
            }
        }
    }

    private fun fetchTokens() {
        userViewModel.accessTokenPreferencesLiveData.observe(viewLifecycleOwner) { accessToken ->
            if (accessToken != null) {
                currentAccessToken = accessToken
                fetchHomeIfReady()
            }
        }
        userViewModel.refreshTokenPreferencesLiveData.observe(viewLifecycleOwner) { refreshToken ->
            if (refreshToken != null) {
                currentRefreshToken = refreshToken
                fetchHomeIfReady()
            }
        }
    }

    // 토큰이 모두 준비된 시점에만 프로필·반려견 목록을 조회한다
    private fun fetchHomeIfReady() {
        if (currentAccessToken.isEmpty() || currentRefreshToken.isEmpty()) return

        homeViewModel.getUserProfile(currentAccessToken)
        homeViewModel.getDogList(currentAccessToken)
    }

    private fun reissueAccessToken() {
        if (currentRefreshToken.isNotEmpty()) {
            userViewModel.getNewAccessToken(currentRefreshToken)
        }
    }

    private fun observeReissueResponse() {
        userViewModel.reissueResponse.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                when (response.code()) {
                    200 -> {
                        userViewModel.setAccessToken(response.body()?.accessToken)
                    }
                    401 -> {
                        CustomSnackBar.make(
                            requireView(),
                            R.drawable.snackbar_error_16dp,
                            getString(R.string.snack_bar_refresh_expire),
                        ).show()
                        if (findNavController().currentDestination?.id == R.id.homeFragment) {
                            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
                        }
                    }
                }
            }
        }
    }

    private fun observeUserProfile() {
        homeViewModel.homeProfileResponse.observe(viewLifecycleOwner) { profileResponse ->
            if (profileResponse != null && profileResponse.code() != 200) {
                if (profileResponse.code() == 401) {
                    reissueAccessToken()
                } else {
                    showErrorSnackBar()
                }
            }
        }
    }

    private fun observeDogList() {
        homeViewModel.homeDogList.observe(viewLifecycleOwner) { dogListResponse ->
            if (dogListResponse != null) {
                when (dogListResponse.code()) {
                    200 -> {
                        // 등록된 강아지가 존재할 경우 기본값 첫 번째 강아지로 설정
                        if (!dogListResponse.body()?.dogs.isNullOrEmpty() &&
                            homeViewModel.homeSelectedDogPos.value == null
                        ) {
                            homeViewModel.setSelectedDogPos(0)
                        }
                    }
                    401 -> reissueAccessToken()
                }
            }
        }
    }

    private fun observeSelectedDate() {
        homeViewModel.homeSelectedDate.observe(viewLifecycleOwner) { selectedDate ->
            if (selectedDate != null) {
                homeViewModel.updateDateList(selectedDate)
                if (homeViewModel.homeSelectedDogId.value != null) {
                    fetchDailyRecords()
                }
            }
        }
    }

    private fun observeSelectedDogPos() {
        homeViewModel.homeSelectedDogPos.observe(viewLifecycleOwner) { selectedDogPos ->
            val dogs = homeViewModel.homeDogList.value?.body()?.dogs
            if (selectedDogPos != null && !dogs.isNullOrEmpty()) {
                val selectedDog = dogs[selectedDogPos]
                homeViewModel.setSelectedDogId(selectedDog.dogId)
                dogViewModel.setDogId(selectedDog.dogId)
                dogViewModel.setDogName(selectedDog.name)
            }
        }
    }

    private fun observeSelectedDogId() {
        homeViewModel.homeSelectedDogId.observe(viewLifecycleOwner) { selectedDogId ->
            if (selectedDogId != null) {
                fetchDailyRecords()
            }
        }
    }

    private fun fetchDailyRecords() {
        val dogId = homeViewModel.homeSelectedDogId.value ?: return
        val selectedDate = homeViewModel.homeSelectedDate.value ?: return
        if (currentAccessToken.isEmpty()) return

        homeViewModel.postDogWeight(currentAccessToken, WeightPostRequest(dogId, getCurrentDate()))
        homeViewModel.getDogFeed(dogId, dateToString(selectedDate), currentAccessToken)
        homeViewModel.getDogSupplements(dogId, dateToString(selectedDate), currentAccessToken)
        homeViewModel.getDogExcreta(dogId, dateFormatter(selectedDate), currentAccessToken)
        homeViewModel.getDogSymptom(dogId, dateFormatter(selectedDate), currentAccessToken)
    }

    private fun observeDailyRecords() {
        homeViewModel.homeDogWeightPost.observe(viewLifecycleOwner) { responseCode ->
            when (responseCode) {
                null -> {}
                200 -> {
                    val dogId = homeViewModel.homeSelectedDogId.value
                    val selectedDate = homeViewModel.homeSelectedDate.value
                    if (dogId != null && selectedDate != null) {
                        homeViewModel.getDogWeight(dogId, dateToString(selectedDate), currentAccessToken)
                    }
                }
                401 -> reissueAccessToken()
                else -> showErrorSnackBar()
            }
        }

        homeViewModel.homeDogWeight.observe(viewLifecycleOwner) { dogWeightResponse ->
            if (dogWeightResponse != null) {
                when (dogWeightResponse.code()) {
                    200 -> dogViewModel.setDogWeight(dogWeightResponse.body()?.weight!!)
                    400 -> reissueAccessToken()
                    else -> showErrorSnackBar()
                }
            }
        }

        homeViewModel.homeDogFeed.observe(viewLifecycleOwner) { dogFeedResponse ->
            if (dogFeedResponse != null && dogFeedResponse.code() != 200) {
                if (dogFeedResponse.code() == 401) {
                    reissueAccessToken()
                } else {
                    showErrorSnackBar()
                }
            }
        }

        homeViewModel.homeDogSupplements.observe(viewLifecycleOwner) { dogSupplementsResponse ->
            if (dogSupplementsResponse != null && dogSupplementsResponse.code() != 200) {
                if (dogSupplementsResponse.code() == 401) {
                    reissueAccessToken()
                } else {
                    showErrorSnackBar()
                }
            }
        }

        homeViewModel.homeDogExcreta.observe(viewLifecycleOwner) { dogExcretaResponse ->
            if (dogExcretaResponse != null && dogExcretaResponse.code() != 200) {
                if (dogExcretaResponse.code() == 401) {
                    reissueAccessToken()
                } else {
                    showErrorSnackBar()
                }
            }
        }

        homeViewModel.homeDogSymptom.observe(viewLifecycleOwner) { dogSymptomResponse ->
            if (dogSymptomResponse == null || (dogSymptomResponse.code() != 200 && dogSymptomResponse.code() != 401)) {
                showErrorSnackBar()
            } else if (dogSymptomResponse.code() == 401) {
                reissueAccessToken()
            }
        }
    }

    private fun showErrorSnackBar() {
        CustomSnackBar.make(
            requireView(),
            R.drawable.snackbar_error_16dp,
            getString(R.string.snack_bar_failure),
        ).show()
    }

    override fun onDateSubmit(str: String) {
        homeViewModel.setSelectedDate(stringToDate(str))
    }

    private fun showCalendarBottomSheet() {
        val modalBottomSheet = CalendarBottomSheetFragment()
        modalBottomSheet.setDateSubmitListener(this@HomeFragment)
        modalBottomSheet.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerCalendarDialogTheme)
        modalBottomSheet.show(requireActivity().supportFragmentManager, modalBottomSheet.tag)
    }

    private fun navigateToDogAdd() {
        val bundle = Bundle()
        bundle.putBoolean("isFirstRegister", false)
        findNavController().navigate(R.id.action_homeFragment_to_dogAddOnBoardingFragment, bundle)
    }

    private fun selectDateAt(position: Int) {
        homeViewModel.setSelectedDatePos(position)
        homeViewModel.setSelectedDate(homeViewModel.homeDateList.value!![position])
    }

    private fun moveCalendarWeek(days: Int) {
        val baseDate = homeViewModel.homeSelectedDate.value ?: return
        val calendar = Calendar.getInstance()
        calendar.time = baseDate
        calendar.add(Calendar.DAY_OF_YEAR, days)
        homeViewModel.setSelectedDate(calendar.time)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
