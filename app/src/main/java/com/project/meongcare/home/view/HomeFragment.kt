package com.project.meongcare.home.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.project.meongcare.CalendarBottomSheetFragment
import com.project.meongcare.MainActivity
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentHomeBinding
import com.project.meongcare.home.model.data.local.DogPreferences
import com.project.meongcare.home.model.data.local.DogProfileClickListener
import com.project.meongcare.home.model.data.local.HorizonCalendarItemClickListener
import com.project.meongcare.home.viewmodel.HomeViewModel
import com.project.meongcare.login.model.data.local.UserPreferences
import com.project.meongcare.login.model.data.repository.LoginRepository
import com.project.meongcare.onboarding.model.data.local.DateSubmitListener
import com.project.meongcare.snackbar.view.CustomSnackBar
import com.project.meongcare.weight.model.entities.WeightPostRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(), DateSubmitListener, DogProfileClickListener, HorizonCalendarItemClickListener {
    private lateinit var fragmentHomeBinding: FragmentHomeBinding
    private lateinit var mainActivity: MainActivity

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var currentAccessToken: String

    @Inject
    lateinit var userPreferences: UserPreferences

    @Inject
    lateinit var dogPreferences: DogPreferences

    @Inject
    lateinit var loginRepository: LoginRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getAccessToken()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        homeViewModel.getUserProfile(currentAccessToken)
        homeViewModel.getDogList(currentAccessToken)

        homeViewModel.homeProfileResponse.observe(viewLifecycleOwner) { profileResponse ->
            if (profileResponse != null && profileResponse.code() == 200) {
                Glide.with(this)
                    .load(profileResponse.body()?.imageUrl)
                    .placeholder(R.drawable.home_profile_default_image)
                    .error(R.drawable.home_profile_default_image)
                    .into(fragmentHomeBinding.imageviewHomeProfile)
            } else if (profileResponse != null && profileResponse.code() == 401) {
                lifecycleScope.launch {
                    val refreshToken = userPreferences.getRefreshToken()
                    if (refreshToken.isNotEmpty()) {
                        val response = loginRepository.getNewAccessToken(refreshToken)
                        if (response != null) {
                            when (response.code()) {
                                200 -> {
                                    userPreferences.setAccessToken(response.body()?.accessToken)
                                }
                                401 -> {
                                    CustomSnackBar.make(
                                        requireView(),
                                        R.drawable.snackbar_error_16dp,
                                        getString(R.string.snack_bar_refresh_expire),
                                    ).show()
                                    findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
                                }
                            }
                        }
                    }
                }
            } else {
                CustomSnackBar.make(
                    requireView(),
                    R.drawable.snackbar_error_16dp,
                    getString(R.string.snack_bar_failure),
                ).show()
            }
        }

        homeViewModel.homeSelectedDate.observe(viewLifecycleOwner) { selectedDate ->
            if (selectedDate != null) {
                Log.d("homeViewmodel-selectedDate", selectedDate.toString())
                // 가로 달력 날짜 selectedDate로 설정
                homeViewModel.updateDateList(selectedDate)
                if (homeViewModel.homeSelectedDogId.value != null) {
                    // 몸무게 조회
                    val weightRequest = WeightPostRequest(homeViewModel.homeSelectedDogId.value!!, getCurrentDate())
                    homeViewModel.postDogWeight(currentAccessToken, weightRequest)
                    // 사료 섭취량 조회
                    homeViewModel.getDogFeed(
                        homeViewModel.homeSelectedDogId.value!!,
                        dateToString(
                            homeViewModel.homeSelectedDate.value!!,
                        ),
                        currentAccessToken,
                    )
                    // 영양제 섭취율 조회
                    homeViewModel.getDogSupplements(
                        homeViewModel.homeSelectedDogId.value!!,
                        dateToString(
                            homeViewModel.homeSelectedDate.value!!,
                        ),
                        currentAccessToken,
                    )
                    // 대소변 횟수 조회
                    homeViewModel.getDogExcreta(
                        homeViewModel.homeSelectedDogId.value!!,
                        dateFormatter(
                            homeViewModel.homeSelectedDate.value!!,
                        ),
                        currentAccessToken,
                    )
                    // 이상증상 목록 조회
                    homeViewModel.getDogSymptom(
                        homeViewModel.homeSelectedDogId.value!!,
                        dateFormatter(
                            homeViewModel.homeSelectedDate.value!!,
                        ),
                        currentAccessToken,
                    )
                }
            }
        }

        homeViewModel.homeDateList.observe(viewLifecycleOwner) { dateList ->
            if (dateList.isNotEmpty()) {
                val adapter = fragmentHomeBinding.recyclerviewHorizonCalendar.adapter as HomeHorizonCalendarAdapter
                adapter.updateDateList(dateList)
            }
        }

        homeViewModel.homeDogList.observe(viewLifecycleOwner) { dogListResponse ->
            if (dogListResponse != null && dogListResponse.code() == 200) {
                fragmentHomeBinding.recyclerviewHomeDog.visibility = View.VISIBLE
                fragmentHomeBinding.linearlayoutDogExist.visibility = View.VISIBLE
                fragmentHomeBinding.linearlayoutDogNotExist.visibility = View.GONE
                val adapter = fragmentHomeBinding.recyclerviewHomeDog.adapter as HomeDogProfileAdapter
                adapter.updateDogProfileList(dogListResponse.body()?.dogs!!)
                if (dogListResponse.body() != null && !dogListResponse.body()?.dogs.isNullOrEmpty() &&
                    homeViewModel.homeSelectedDogPos.value == null
                ) {
                    homeViewModel.setSelectedDogPos(0)
                }
                if (dogListResponse.body()?.dogs.isNullOrEmpty()) {
                    fragmentHomeBinding.recyclerviewHomeDog.visibility = View.GONE
                    fragmentHomeBinding.linearlayoutDogExist.visibility = View.GONE
                    fragmentHomeBinding.linearlayoutDogNotExist.visibility = View.VISIBLE
                }
            } else if (dogListResponse != null && dogListResponse.code() == 401) {
                lifecycleScope.launch {
                    val refreshToken = userPreferences.getRefreshToken()
                    if (refreshToken.isNotEmpty()) {
                        val response = loginRepository.getNewAccessToken(refreshToken)
                        if (response != null) {
                            when (response.code()) {
                                200 -> {
                                    userPreferences.setAccessToken(response.body()?.accessToken!!)
                                }
                                401 -> {
                                    CustomSnackBar.make(
                                        requireView(),
                                        R.drawable.snackbar_error_16dp,
                                        getString(R.string.snack_bar_refresh_expire),
                                    ).show()
                                    findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
                                }
                            }
                        }
                    }
                }
            } else {
                fragmentHomeBinding.recyclerviewHomeDog.visibility = View.GONE
                fragmentHomeBinding.linearlayoutDogExist.visibility = View.GONE
                fragmentHomeBinding.linearlayoutDogNotExist.visibility = View.VISIBLE
            }
        }

        homeViewModel.homeSelectedDatePos.observe(viewLifecycleOwner) { selectedDatePos ->
            if (selectedDatePos != null) {
                Log.d("homeSelectedDatePos", selectedDatePos.toString())
                val adapter = fragmentHomeBinding.recyclerviewHorizonCalendar.adapter as HomeHorizonCalendarAdapter
                adapter.updateSelectedPos(selectedDatePos)
            }
        }

        homeViewModel.homeSelectedDogId.observe(viewLifecycleOwner) { selectedDogId ->
            if (selectedDogId != null) {
                // 몸무게 조회
                val weightRequest = WeightPostRequest(selectedDogId, getCurrentDate())
                homeViewModel.postDogWeight(currentAccessToken, weightRequest)
                // 사료 섭취량 조회
                homeViewModel.getDogFeed(selectedDogId, dateToString(homeViewModel.homeSelectedDate.value!!), currentAccessToken)
                // 영양제 섭취율 조회
                homeViewModel.getDogSupplements(selectedDogId, dateToString(homeViewModel.homeSelectedDate.value!!), currentAccessToken)
                // 대소변 횟수 조회
                homeViewModel.getDogExcreta(selectedDogId, dateFormatter(homeViewModel.homeSelectedDate.value!!), currentAccessToken)
                // 이상증상 목록 조회
                homeViewModel.getDogSymptom(selectedDogId, dateFormatter(homeViewModel.homeSelectedDate.value!!), currentAccessToken)
            }
        }

        homeViewModel.homeDogWeightPost.observe(viewLifecycleOwner) { responseCode ->
            if (responseCode != null && responseCode == 200) {
                homeViewModel.getDogWeight(
                    homeViewModel.homeSelectedDogId.value!!,
                    dateToString(homeViewModel.homeSelectedDate.value!!),
                    currentAccessToken,
                )
            } else if (responseCode != null && responseCode == 401) {
                lifecycleScope.launch {
                    val refreshToken = userPreferences.getRefreshToken()
                    if (refreshToken.isNotEmpty()) {
                        val response = loginRepository.getNewAccessToken(refreshToken)
                        if (response != null) {
                            when (response.code()) {
                                200 -> {
                                    userPreferences.setAccessToken(response.body()?.accessToken!!)
                                }
                                401 -> {
                                    CustomSnackBar.make(
                                        requireView(),
                                        R.drawable.snackbar_error_16dp,
                                        getString(R.string.snack_bar_refresh_expire),
                                    ).show()
                                    findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
                                }
                            }
                        }
                    }
                }
            } else {
                CustomSnackBar.make(
                    requireView(),
                    R.drawable.snackbar_error_16dp,
                    getString(R.string.snack_bar_failure),
                ).show()
            }
        }

        homeViewModel.homeSelectedDogPos.observe(viewLifecycleOwner) { selectedDogPos ->
            if (selectedDogPos != null && !homeViewModel.homeDogList.value?.body()?.dogs?.isEmpty()!!) {
                Log.d("homeSelectedDogName", homeViewModel.homeDogList.value?.body()!!.dogs[selectedDogPos].name)
                homeViewModel.setSelectedDogId(homeViewModel.homeDogList.value?.body()!!.dogs[selectedDogPos].dogId)
                val adapter = fragmentHomeBinding.recyclerviewHomeDog.adapter as HomeDogProfileAdapter
                adapter.updateSelectedPos(selectedDogPos)
                dogPreferences.setDogId(homeViewModel.homeDogList.value?.body()!!.dogs[selectedDogPos].dogId)
                dogPreferences.setDogName(homeViewModel.homeDogList.value?.body()!!.dogs[selectedDogPos].name)
            }
        }

        homeViewModel.homeDogWeight.observe(viewLifecycleOwner) { dogWeightResponse ->
            if (dogWeightResponse != null && dogWeightResponse.code() == 200) {
                Log.d("homeDogWeight", dogWeightResponse.body()?.weight?.toString()!!)
                fragmentHomeBinding.textviewHomeWeight.text = dogWeightResponse.body()?.weight?.toString()
                dogPreferences.setDogWeight(dogWeightResponse.body()?.weight!!)
            } else if (dogWeightResponse != null && dogWeightResponse.code() == 400) {
                lifecycleScope.launch {
                    val refreshToken = userPreferences.getRefreshToken()
                    if (refreshToken.isNotEmpty()) {
                        val response = loginRepository.getNewAccessToken(refreshToken)
                        if (response != null) {
                            when (response.code()) {
                                200 -> {
                                    userPreferences.setAccessToken(response.body()?.accessToken!!)
                                }
                                401 -> {
                                    CustomSnackBar.make(
                                        requireView(),
                                        R.drawable.snackbar_error_16dp,
                                        getString(R.string.snack_bar_refresh_expire),
                                    ).show()
                                    findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
                                }
                            }
                        }
                    }
                }
            } else {
                CustomSnackBar.make(
                    requireView(),
                    R.drawable.snackbar_error_16dp,
                    getString(R.string.snack_bar_failure),
                ).show()
            }
        }

        homeViewModel.homeDogFeed.observe(viewLifecycleOwner) { dogFeedResponse ->
            if (dogFeedResponse != null) {
                if (dogFeedResponse.code() == 200) {
                    Log.d("homeDogFeed", dogFeedResponse.body()?.recommendIntake.toString())
                    fragmentHomeBinding.textviewHomeFeed.text = dogFeedResponse.body()?.recommendIntake.toString()
                } else if (dogFeedResponse.code() == 401) {
                    lifecycleScope.launch {
                        val refreshToken = userPreferences.getRefreshToken()
                        if (refreshToken.isNotEmpty()) {
                            val response = loginRepository.getNewAccessToken(refreshToken)
                            if (response != null) {
                                when (response.code()) {
                                    200 -> {
                                        userPreferences.setAccessToken(response.body()?.accessToken!!)
                                    }
                                    401 -> {
                                        CustomSnackBar.make(
                                            requireView(),
                                            R.drawable.snackbar_error_16dp,
                                            getString(R.string.snack_bar_refresh_expire),
                                        ).show()
                                        findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    CustomSnackBar.make(
                        requireView(),
                        R.drawable.snackbar_error_16dp,
                        getString(R.string.snack_bar_failure),
                    ).show()
                }
            }
        }

        homeViewModel.homeDogSupplements.observe(viewLifecycleOwner) { dogSupplementsResponse ->
            if (dogSupplementsResponse != null) {
                if (dogSupplementsResponse.code() == 200) {
                    Log.d("homeDogSupplements", dogSupplementsResponse.body()?.supplementsRate.toString())
                    fragmentHomeBinding.textviewHomeNutritionPercentage.text = dogSupplementsResponse.body()?.supplementsRate.toString()
                    fragmentHomeBinding.progressbarNutrition.progress = dogSupplementsResponse.body()?.supplementsRate ?: 0
                } else if (dogSupplementsResponse.code() == 401) {
                    lifecycleScope.launch {
                        val refreshToken = userPreferences.getRefreshToken()
                        if (refreshToken.isNotEmpty()) {
                            val response = loginRepository.getNewAccessToken(refreshToken)
                            if (response != null) {
                                when (response.code()) {
                                    200 -> {
                                        userPreferences.setAccessToken(response.body()?.accessToken!!)
                                    }
                                    401 -> {
                                        CustomSnackBar.make(
                                            requireView(),
                                            R.drawable.snackbar_error_16dp,
                                            getString(R.string.snack_bar_refresh_expire),
                                        ).show()
                                        findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    CustomSnackBar.make(
                        requireView(),
                        R.drawable.snackbar_error_16dp,
                        getString(R.string.snack_bar_failure),
                    ).show()
                }
            }
        }

        homeViewModel.homeDogExcreta.observe(viewLifecycleOwner) { dogExcretaResponse ->
            if (dogExcretaResponse != null) {
                if (dogExcretaResponse.code() == 200) {
                    Log.d("homeDogExcreta", dogExcretaResponse.body()?.urineCount.toString())
                    fragmentHomeBinding.textviewHomeFecesCount.text = dogExcretaResponse.body()?.fecesCount.toString()
                    fragmentHomeBinding.textviewHomeUrineCount.text = dogExcretaResponse.body()?.urineCount.toString()
                } else if (dogExcretaResponse.code() == 401) {
                    lifecycleScope.launch {
                        val refreshToken = userPreferences.getRefreshToken()
                        if (refreshToken.isNotEmpty()) {
                            val response = loginRepository.getNewAccessToken(refreshToken)
                            if (response != null) {
                                when (response.code()) {
                                    200 -> {
                                        userPreferences.setAccessToken(response.body()?.accessToken!!)
                                    }
                                    401 -> {
                                        CustomSnackBar.make(
                                            requireView(),
                                            R.drawable.snackbar_error_16dp,
                                            getString(R.string.snack_bar_refresh_expire),
                                        ).show()
                                        findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    CustomSnackBar.make(
                        requireView(),
                        R.drawable.snackbar_error_16dp,
                        getString(R.string.snack_bar_failure),
                    ).show()
                }
            }
        }

        homeViewModel.homeDogSymptom.observe(viewLifecycleOwner) { dogSymptomResponse ->
            if (dogSymptomResponse != null) {
                if (dogSymptomResponse.code() == 200) {
                    fragmentHomeBinding.textviewHomeSymptom2.setText(R.string.home_symptom_exist)
                    fragmentHomeBinding.recyclerviewHomeSymptom.visibility = View.VISIBLE
                    dogSymptomResponse.body()?.symptomRecords?.forEach { symptoms ->
                        Log.d("homeDogSymptom", symptoms.symptomString)
                    }
                    val adapter = fragmentHomeBinding.recyclerviewHomeSymptom.adapter as HomeSymptomAdapter
                    adapter.updateSymptomList(dogSymptomResponse.body()?.symptomRecords!!)
                } else if (dogSymptomResponse.code() == 401) {
                    lifecycleScope.launch {
                        val refreshToken = userPreferences.getRefreshToken()
                        if (refreshToken.isNotEmpty()) {
                            val response = loginRepository.getNewAccessToken(refreshToken)
                            if (response != null) {
                                when (response.code()) {
                                    200 -> {
                                        userPreferences.setAccessToken(response.body()?.accessToken!!)
                                    }
                                    401 -> {
                                        CustomSnackBar.make(
                                            requireView(),
                                            R.drawable.snackbar_error_16dp,
                                            getString(R.string.snack_bar_refresh_expire),
                                        ).show()
                                        findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    CustomSnackBar.make(
                        requireView(),
                        R.drawable.snackbar_error_16dp,
                        getString(R.string.snack_bar_failure),
                    ).show()
                    fragmentHomeBinding.textviewHomeSymptom2.setText(R.string.home_symptom_not_exist)
                    fragmentHomeBinding.recyclerviewHomeSymptom.visibility = View.GONE
                }
                if (dogSymptomResponse.body()?.symptomRecords.isNullOrEmpty()) {
                    fragmentHomeBinding.textviewHomeSymptom2.setText(R.string.home_symptom_not_exist)
                    fragmentHomeBinding.recyclerviewHomeSymptom.visibility = View.GONE
                }
            } else {
                CustomSnackBar.make(
                    requireView(),
                    R.drawable.snackbar_error_16dp,
                    getString(R.string.snack_bar_failure),
                ).show()
                fragmentHomeBinding.textviewHomeSymptom2.setText(R.string.home_symptom_not_exist)
                fragmentHomeBinding.recyclerviewHomeSymptom.visibility = View.GONE
            }
        }

        fragmentHomeBinding.run {
            imageviewHomeCalendar.setOnClickListener {
                val modalBottomSheet = CalendarBottomSheetFragment()
                modalBottomSheet.setDateSubmitListener(this@HomeFragment)
                modalBottomSheet.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerCalendarDialogTheme)
                modalBottomSheet.show(mainActivity.supportFragmentManager, modalBottomSheet.tag)
            }

            imageviewHomeAlert.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_noticeFragment)
            }

            imageviewHomeProfile.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
            }

            imageviewHomeAddDog.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_dogAddOnBoardingFragment)
            }

            recyclerviewHomeDog.run {
                adapter = HomeDogProfileAdapter(layoutInflater, context, this@HomeFragment)
                layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false)
            }

            recyclerviewHorizonCalendar.run {
                adapter = HomeHorizonCalendarAdapter(layoutInflater, context, this@HomeFragment)
                layoutManager = GridLayoutManager(context, 7)
            }

            recyclerviewHomeSymptom.run {
                adapter = HomeSymptomAdapter(layoutInflater, context)
                layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false)
            }

            constraintlayoutHomeSymptom.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_symptomFragment)
            }

            constraintlayoutHomeFeces.setOnClickListener {
                // 대소변 기능 업데이트 후 삭제
                fragmentHomeBinding.run {
                    includeMedicalRecordDialog.run {
                        root.visibility = View.VISIBLE
                        constraintlayoutBg.setOnClickListener {
                            includeMedicalRecordDialog.root.visibility = View.GONE
                        }
                        buttonOk.setOnClickListener {
                            includeMedicalRecordDialog.root.visibility = View.GONE
                        }
                    }
                }
                // findNavController().navigate(R.id.action_homeFragment_to_excretaFragment)
            }

            constraintlayoutHomeNutrition.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_supplementFragment)
            }

            constraintlayoutHomeWeight.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_weightFragment)
            }

            constraintlayoutHomeFeed.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_feedFragment)
            }
        }

        return fragmentHomeBinding.root
    }

    private fun getAccessToken() {
        lifecycleScope.launch {
            userPreferences.accessToken.collectLatest { accessToken ->
                if (accessToken != null) {
                    currentAccessToken = accessToken
                }
            }
        }
    }

    override fun onDateSubmit(str: String) {
        homeViewModel.setSelectedDate(stringToDate(str))
    }

    fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return currentDate.format(formatter)
    }

    fun dateFormatter(date: Date): String {
        val currentDate = getCurrentDate()
        val selectedDate = dateToString(date)
        if (selectedDate == currentDate) {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            return LocalDate.now().atStartOfDay().format(formatter)
        } else {
            return (selectedDate + "T23:59:59")
        }
    }

    fun dateToString(date: Date): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        return dateFormat.format(date)
    }

    fun stringToDate(str: String): Date {
        val format = SimpleDateFormat("yyyy-MM-dd")
        return format.parse(str)
    }

    override fun onDogProfileClick(pos: Int) {
        homeViewModel.setSelectedDogPos(pos)
    }

    override fun onItemClick(position: Int) {
        homeViewModel.setSelectedDatePos(position)
        homeViewModel.setSelectedDate(homeViewModel.homeDateList.value!![position])
    }
}
