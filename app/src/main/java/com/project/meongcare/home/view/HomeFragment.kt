package com.project.meongcare.home.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.project.meongcare.CalendarBottomSheetFragment
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentHomeBinding
import com.project.meongcare.home.model.data.local.DogProfileClickListener
import com.project.meongcare.home.model.data.local.HorizonCalendarItemClickListener
import com.project.meongcare.home.util.HomeDateUtil.dateFormatter
import com.project.meongcare.home.util.HomeDateUtil.dateToString
import com.project.meongcare.home.util.HomeDateUtil.getCurrentDate
import com.project.meongcare.home.util.HomeDateUtil.stringToDate
import com.project.meongcare.home.viewmodel.HomeViewModel
import com.project.meongcare.medicalrecord.viewmodel.DogViewModel
import com.project.meongcare.medicalrecord.viewmodel.UserViewModel
import com.project.meongcare.onboarding.model.data.local.DateSubmitListener
import com.project.meongcare.snackbar.view.CustomSnackBar
import com.project.meongcare.weight.model.entities.WeightPostRequest
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

@AndroidEntryPoint
class HomeFragment : Fragment(), DateSubmitListener, DogProfileClickListener, HorizonCalendarItemClickListener {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var currentAccessToken: String
    private lateinit var currentRefreshToken: String
    private lateinit var loadingDialog: LoadingDialog

    private val homeViewModel: HomeViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val dogViewModel: DogViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        getAccessToken()

        val currentDate = LocalDate.now()
        setSelectedDate(Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant()))

        initCalendarImageView()
        initAlarmImageView()
        initProfileImageView()
        initAddDogImageView()
        initDogRecyclerView()
        initCalendarRecyclerView()
        initSymptomRecyclerView()
        initSymptomLayout()
        initExcretaLayout()
        initWeightLayout()
        initSupplementLayout()
        initFeedLayout()
    }

    private fun getAccessToken() {
        userViewModel.accessTokenPreferencesLiveData.observe(viewLifecycleOwner) { accessToken ->
            if (accessToken != null) {
                currentAccessToken = accessToken
                getRefreshToken()
            }
        }
    }

    private fun getRefreshToken() {
        userViewModel.refreshTokenPreferencesLiveData.observe(viewLifecycleOwner) { refreshToken ->
            if (refreshToken != null) {
                currentRefreshToken = refreshToken

                getUserProfile()
                getDogList()
            }
        }
    }

    private fun reissueAccessToken() {
        userViewModel.getNewAccessToken(currentRefreshToken)
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
                        findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
                    }
                }
            }
        }
    }

    private fun getUserProfile() {
        homeViewModel.getUserProfile(currentAccessToken)
        homeViewModel.homeProfileResponse.observe(viewLifecycleOwner) { profileResponse ->
            if (profileResponse != null && profileResponse.code() == 200) {
                Glide.with(this)
                    .load(profileResponse.body()?.imageUrl)
                    .placeholder(R.drawable.home_profile_default_image)
                    .error(R.drawable.home_profile_default_image)
                    .into(binding.imageviewHomeProfile)
            } else {
                if (profileResponse?.code() == 401) {
                    if (currentRefreshToken.isNotEmpty()) {
                        reissueAccessToken()
                    }
                } else {
                    CustomSnackBar.make(
                        requireView(),
                        R.drawable.snackbar_error_16dp,
                        getString(R.string.snack_bar_failure),
                    ).show()
                }
                Glide.with(this)
                    .load(R.drawable.home_profile_default_image)
                    .into(binding.imageviewHomeProfile)
            }
        }
    }

    private fun getDogList() {
        homeViewModel.getDogList(currentAccessToken)
        homeViewModel.homeDogList.observe(viewLifecycleOwner) { dogListResponse ->
            if (dogListResponse != null && dogListResponse.code() == 200) {
                binding.recyclerviewHomeDog.visibility = View.VISIBLE
                binding.linearlayoutDogExist.visibility = View.VISIBLE
                binding.linearlayoutDogNotExist.visibility = View.GONE
                val adapter = binding.recyclerviewHomeDog.adapter as HomeDogProfileAdapter
                adapter.updateDogProfileList(dogListResponse.body()?.dogs!!)
                if (dogListResponse.body() != null && !dogListResponse.body()?.dogs.isNullOrEmpty() &&
                    homeViewModel.homeSelectedDogPos.value == null
                ) {
                    // 등록된 강아지가 존재할 경우 기본값 첫 번째 강아지로 설정
                    setSelectedDogPos(0)
                }
                if (dogListResponse.body()?.dogs.isNullOrEmpty()) {
                    binding.recyclerviewHomeDog.visibility = View.GONE
                    binding.linearlayoutDogExist.visibility = View.GONE
                    binding.linearlayoutDogNotExist.visibility = View.VISIBLE
                }
            } else if (dogListResponse != null && dogListResponse.code() == 401) {
                if (currentRefreshToken.isNotEmpty()) {
                    reissueAccessToken()
                }
            } else {
                binding.recyclerviewHomeDog.visibility = View.GONE
                binding.linearlayoutDogExist.visibility = View.GONE
                binding.linearlayoutDogNotExist.visibility = View.VISIBLE
            }
        }
    }

    private fun setSelectedDate(date: Date) {
        homeViewModel.setSelectedDate(date)
        homeViewModel.homeSelectedDate.observe(viewLifecycleOwner) { selectedDate ->
            if (selectedDate != null) {
                Log.d("homeViewmodel-selectedDate", selectedDate.toString())
                // 가로 달력 날짜 selectedDate로 설정
                getDateList(selectedDate)
                if (homeViewModel.homeSelectedDogId.value != null) {
                    // 몸무게 조회
                    val weightRequest = WeightPostRequest(homeViewModel.homeSelectedDogId.value!!, getCurrentDate())
                    postDogWeight(weightRequest)
                    // 사료 섭취량 조회
                    getDogFeed()
                    // 영양제 섭취율 조회
                    getDogSupplements()
                    // 대소변 횟수 조회
                    getDogExcreta()
                    // 이상증상 목록 조회
                    getDogSymptom()
                }
            }
        }
    }

    private fun getDateList(selectedDate: Date) {
        homeViewModel.updateDateList(selectedDate)
        homeViewModel.homeDateList.observe(viewLifecycleOwner) { dateList ->
            if (dateList.isNotEmpty()) {
                val adapter = binding.recyclerviewHorizonCalendar.adapter as HomeHorizonCalendarAdapter
                adapter.updateDateList(dateList)
            }
        }
    }

    private fun setSelectedDatePos(position: Int) {
        homeViewModel.setSelectedDatePos(position)
        homeViewModel.homeSelectedDatePos.observe(viewLifecycleOwner) { selectedDatePos ->
            if (selectedDatePos != null) {
                Log.d("homeSelectedDatePos", selectedDatePos.toString())
                val adapter = binding.recyclerviewHorizonCalendar.adapter as HomeHorizonCalendarAdapter
                adapter.updateSelectedPos(selectedDatePos)
            }
        }
    }

    private fun setSelectedDogId(dogId: Long) {
        homeViewModel.setSelectedDogId(dogId)
        homeViewModel.homeSelectedDogId.observe(viewLifecycleOwner) { selectedDogId ->
            if (selectedDogId != null) {
                // 몸무게 조회
                val weightRequest = WeightPostRequest(selectedDogId, getCurrentDate())
                postDogWeight(weightRequest)
                // 사료 섭취량 조회
                getDogFeed()
                // 영양제 섭취율 조회
                getDogSupplements()
                // 대소변 횟수 조회
                getDogExcreta()
                // 이상증상 목록 조회
                getDogSymptom()
            }
        }
    }

    private fun setSelectedDogPos(position: Int) {
        homeViewModel.setSelectedDogPos(position)
        homeViewModel.homeSelectedDogPos.observe(viewLifecycleOwner) { selectedDogPos ->
            if (selectedDogPos != null && !homeViewModel.homeDogList.value?.body()?.dogs?.isNullOrEmpty()!!) {
                Log.d("homeSelectedDogName", homeViewModel.homeDogList.value?.body()!!.dogs[selectedDogPos].name)
                setSelectedDogId(homeViewModel.homeDogList.value?.body()!!.dogs[selectedDogPos].dogId)
                val adapter = binding.recyclerviewHomeDog.adapter as HomeDogProfileAdapter
                adapter.updateSelectedPos(selectedDogPos)
                dogViewModel.setDogId(homeViewModel.homeDogList.value?.body()!!.dogs[selectedDogPos].dogId)
                dogViewModel.setDogName(homeViewModel.homeDogList.value?.body()!!.dogs[selectedDogPos].name)
            }
        }
    }

    private fun postDogWeight(weightRequest: WeightPostRequest) {
        homeViewModel.postDogWeight(currentAccessToken, weightRequest)
        homeViewModel.homeDogWeightPost.observe(viewLifecycleOwner) { responseCode ->
            if (responseCode != null && responseCode == 200) {
                getDogWeight()
            } else if (responseCode != null && responseCode == 401) {
                if (currentRefreshToken.isNotEmpty()) {
                    reissueAccessToken()
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

    private fun getDogWeight() {
        homeViewModel.getDogWeight(
            homeViewModel.homeSelectedDogId.value!!,
            dateToString(homeViewModel.homeSelectedDate.value!!),
            currentAccessToken,
        )
        homeViewModel.homeDogWeight.observe(viewLifecycleOwner) { dogWeightResponse ->
            if (dogWeightResponse != null && dogWeightResponse.code() == 200) {
                Log.d("homeDogWeight", dogWeightResponse.body()?.weight?.toString()!!)
                binding.textviewHomeWeight.text = dogWeightResponse.body()?.weight?.toString()
                dogViewModel.setDogWeight(dogWeightResponse.body()?.weight!!)
            } else if (dogWeightResponse != null && dogWeightResponse.code() == 400) {
                if (currentRefreshToken.isNotEmpty()) {
                    reissueAccessToken()
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

    private fun getDogFeed() {
        homeViewModel.getDogFeed(
            homeViewModel.homeSelectedDogId.value!!,
            dateToString(homeViewModel.homeSelectedDate.value!!),
            currentAccessToken,
        )
        homeViewModel.homeDogFeed.observe(viewLifecycleOwner) { dogFeedResponse ->
            if (dogFeedResponse != null) {
                if (dogFeedResponse.code() == 200) {
                    Log.d("homeDogFeed", dogFeedResponse.body()?.recommendIntake.toString())
                    binding.textviewHomeFeed.text = dogFeedResponse.body()?.recommendIntake.toString()
                } else if (dogFeedResponse.code() == 401) {
                    reissueAccessToken()
                } else {
                    CustomSnackBar.make(
                        requireView(),
                        R.drawable.snackbar_error_16dp,
                        getString(R.string.snack_bar_failure),
                    ).show()
                }
            }
        }
    }

    private fun getDogSupplements() {
        homeViewModel.getDogSupplements(
            homeViewModel.homeSelectedDogId.value!!,
            dateToString(homeViewModel.homeSelectedDate.value!!),
            currentAccessToken,
        )
        homeViewModel.homeDogSupplements.observe(viewLifecycleOwner) { dogSupplementsResponse ->
            if (dogSupplementsResponse != null) {
                if (dogSupplementsResponse.code() == 200) {
                    Log.d("homeDogSupplements", dogSupplementsResponse.body()?.supplementsRate.toString())
                    binding.textviewHomeNutritionPercentage.text = dogSupplementsResponse.body()?.supplementsRate.toString()
                    binding.progressbarNutrition.progress = dogSupplementsResponse.body()?.supplementsRate ?: 0
                } else if (dogSupplementsResponse.code() == 401) {
                    if (currentRefreshToken.isNotEmpty()) {
                        reissueAccessToken()
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
    }

    private fun getDogExcreta() {
        homeViewModel.getDogExcreta(
            homeViewModel.homeSelectedDogId.value!!,
            dateFormatter(homeViewModel.homeSelectedDate.value!!),
            currentAccessToken,
        )
        homeViewModel.homeDogExcreta.observe(viewLifecycleOwner) { dogExcretaResponse ->
            if (dogExcretaResponse != null) {
                if (dogExcretaResponse.code() == 200) {
                    Log.d("homeDogExcreta", dogExcretaResponse.body()?.urineCount.toString())
                    binding.textviewHomeFecesCount.text = dogExcretaResponse.body()?.fecesCount.toString()
                    binding.textviewHomeUrineCount.text = dogExcretaResponse.body()?.urineCount.toString()
                } else if (dogExcretaResponse.code() == 401) {
                    if (currentRefreshToken.isNotEmpty()) {
                        reissueAccessToken()
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
    }

    private fun getDogSymptom() {
        homeViewModel.getDogSymptom(
            homeViewModel.homeSelectedDogId.value!!,
            dateFormatter(homeViewModel.homeSelectedDate.value!!),
            currentAccessToken,
        )
        homeViewModel.homeDogSymptom.observe(viewLifecycleOwner) { dogSymptomResponse ->
            if (dogSymptomResponse != null) {
                if (dogSymptomResponse.code() == 200) {
                    binding.textviewHomeSymptom2.setText(R.string.home_symptom_exist)
                    binding.recyclerviewHomeSymptom.visibility = View.VISIBLE
                    dogSymptomResponse.body()?.symptomRecords?.forEach { symptoms ->
                        Log.d("homeDogSymptom", symptoms.symptomString)
                    }
                    val adapter = binding.recyclerviewHomeSymptom.adapter as HomeSymptomAdapter
                    adapter.updateSymptomList(dogSymptomResponse.body()?.symptomRecords!!)
                } else if (dogSymptomResponse.code() == 401) {
                    if (currentRefreshToken.isNotEmpty()) {
                        reissueAccessToken()
                    }
                } else {
                    CustomSnackBar.make(
                        requireView(),
                        R.drawable.snackbar_error_16dp,
                        getString(R.string.snack_bar_failure),
                    ).show()
                    binding.textviewHomeSymptom2.setText(R.string.home_symptom_not_exist)
                    binding.recyclerviewHomeSymptom.visibility = View.GONE
                }
                if (dogSymptomResponse.body()?.symptomRecords.isNullOrEmpty()) {
                    binding.textviewHomeSymptom2.setText(R.string.home_symptom_not_exist)
                    binding.recyclerviewHomeSymptom.visibility = View.GONE
                }
            } else {
                CustomSnackBar.make(
                    requireView(),
                    R.drawable.snackbar_error_16dp,
                    getString(R.string.snack_bar_failure),
                ).show()
                binding.textviewHomeSymptom2.setText(R.string.home_symptom_not_exist)
                binding.recyclerviewHomeSymptom.visibility = View.GONE
            }
        }
    }

    override fun onDateSubmit(str: String) {
        setSelectedDate(stringToDate(str))
    }

    override fun onDogProfileClick(pos: Int) {
        setSelectedDogPos(pos)
    }

    override fun onItemClick(position: Int) {
        setSelectedDatePos(position)
        setSelectedDate(homeViewModel.homeDateList.value!![position])
    }

    fun getLoadingDialog(): LoadingDialog {
        val progressDialog = LoadingDialog(requireContext())
        progressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)
        return progressDialog
    }

    private fun initCalendarImageView() {
        binding.imageviewHomeCalendar.setOnClickListener {
            val modalBottomSheet = CalendarBottomSheetFragment()
            modalBottomSheet.setDateSubmitListener(this@HomeFragment)
            modalBottomSheet.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerCalendarDialogTheme)
            modalBottomSheet.show(requireActivity().supportFragmentManager, modalBottomSheet.tag)
        }
    }

    private fun initAlarmImageView() {
        binding.imageviewHomeAlert.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_noticeFragment)
        }
    }

    private fun initProfileImageView() {
        binding.imageviewHomeProfile.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }
    }

    private fun initAddDogImageView() {
        binding.imageviewHomeAddDog.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("isFirstRegister", false)
            findNavController().navigate(R.id.action_homeFragment_to_dogAddOnBoardingFragment, bundle)
        }
    }

    private fun initDogRecyclerView() {
        binding.recyclerviewHomeDog.run {
            adapter = HomeDogProfileAdapter(layoutInflater, context, this@HomeFragment)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun initCalendarRecyclerView() {
        binding.recyclerviewHorizonCalendar.run {
            adapter = HomeHorizonCalendarAdapter(layoutInflater, context, this@HomeFragment)
            layoutManager = GridLayoutManager(context, 7)
        }
    }

    private fun initSymptomRecyclerView() {
        binding.recyclerviewHomeSymptom.run {
            adapter = HomeSymptomAdapter(layoutInflater, context)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun initSymptomLayout() {
        binding.constraintlayoutHomeSymptom.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_symptomFragment)
        }
    }

    private fun initExcretaLayout() {
        binding.constraintlayoutHomeFeces.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_excretaFragment)
        }
    }

    private fun initWeightLayout() {
        binding.constraintlayoutHomeWeight.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_weightFragment)
        }
    }

    private fun initSupplementLayout() {
        binding.constraintlayoutHomeNutrition.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_supplementFragment)
        }
    }

    private fun initFeedLayout() {
        binding.constraintlayoutHomeFeed.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_feedFragment)
        }
    }
}
