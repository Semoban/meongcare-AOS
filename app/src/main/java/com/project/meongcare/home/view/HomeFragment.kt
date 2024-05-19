package com.project.meongcare.home.view

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
import com.project.meongcare.medicalRecord.viewmodel.DogViewModel
import com.project.meongcare.medicalRecord.viewmodel.UserViewModel
import com.project.meongcare.onboarding.model.data.local.DateSubmitListener
import com.project.meongcare.snackbar.view.CustomSnackBar
import com.project.meongcare.weight.model.entities.WeightPostRequest
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
class HomeFragment : Fragment(), DateSubmitListener, DogProfileClickListener, HorizonCalendarItemClickListener {
    private lateinit var binding: FragmentHomeBinding

    private val homeViewModel: HomeViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val dogViewModel: DogViewModel by viewModels()

    private var currentAccessToken = ""
    private var currentRefreshToken = ""

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

        initHorizonCalendarRecyclerView()
        initDogRecyclerView()
        initSymptomRecyclerView()
        initCalendarImageView()
        initAlarmImageView()
        initProfileImageView()
        initAddDogImageView()
        initSymptomLayout()
        initExcretaLayout()
        initSupplementLayout()
        initWeightLayout()
        initFeedLayout()

        homeViewModel.homeSelectedDate.observe(viewLifecycleOwner) {
            homeViewModel.updateDateList(it)
        }
        homeViewModel.homeDateList.observe(viewLifecycleOwner) { dateList ->
            if (dateList.isNotEmpty()) {
                val adapter = binding.recyclerviewHorizonCalendar.adapter as HomeHorizonCalendarAdapter
                adapter.updateDateList(dateList)
            }
        }
        homeViewModel.homeSelectedDatePos.observe(viewLifecycleOwner) { datePos ->
            if (datePos != null) {
                val adapter = binding.recyclerviewHorizonCalendar.adapter as HomeHorizonCalendarAdapter
                adapter.updateSelectedPos(datePos)
                if (homeViewModel.homeDogList.value?.body()?.dogs.isNullOrEmpty()) {
                    getDogList()
                } else {
                    getDogInfo()
                }
            }
        }
        homeViewModel.homeSelectedDogPos.observe(viewLifecycleOwner) { dogPos ->
            if (dogPos != null && !homeViewModel.homeDogList.value?.body()?.dogs?.isNullOrEmpty()!!) {
                Log.d("선택된 강아지 이름", homeViewModel.homeDogList.value?.body()!!.dogs[dogPos].name)
                homeViewModel.setSelectedDogId(homeViewModel.homeDogList.value?.body()!!.dogs[dogPos].dogId)
                val adapter = binding.recyclerviewHomeDog.adapter as HomeDogProfileAdapter
                adapter.updateSelectedPos(dogPos)
                dogViewModel.setDogId(homeViewModel.homeDogList.value?.body()!!.dogs[dogPos].dogId)
                dogViewModel.setDogName(homeViewModel.homeDogList.value?.body()!!.dogs[dogPos].name)
                getDogInfo()
            }
        }
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
            findNavController().navigate(R.id.action_homeFragment_to_dogAddOnBoardingFragment)
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

    private fun initSupplementLayout() {
        binding.constraintlayoutHomeNutrition.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_supplementFragment)
        }
    }

    private fun initWeightLayout() {
        binding.constraintlayoutHomeWeight.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_weightFragment)
        }
    }

    private fun initFeedLayout() {
        binding.constraintlayoutHomeFeed.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_feedFragment)
        }
    }

    private fun initHorizonCalendarRecyclerView() {
        binding.recyclerviewHorizonCalendar.run {
            adapter = HomeHorizonCalendarAdapter(layoutInflater, context, this@HomeFragment)
            layoutManager = GridLayoutManager(context, 7)
        }
    }

    private fun initDogRecyclerView() {
        binding.recyclerviewHomeDog.run {
            adapter = HomeDogProfileAdapter(layoutInflater, context, this@HomeFragment)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun initSymptomRecyclerView() {
        binding.recyclerviewHomeSymptom.run {
            adapter = HomeSymptomAdapter(layoutInflater, context)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun getUserProfile() {
        homeViewModel.getUserProfile(currentAccessToken)
        homeViewModel.homeProfileResponse.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                when (response.code()) {
                    200 -> {
                        Glide.with(this)
                            .load(response.body()?.imageUrl)
                            .error(R.drawable.home_profile_default_image)
                            .into(binding.imageviewHomeProfile)
                    }
                    401 -> {
                        reissueAccessToken()
                    }
                    else -> {
                        CustomSnackBar.make(
                            requireView(),
                            R.drawable.snackbar_error_16dp,
                            getString(R.string.snack_bar_failure),
                        ).show()
                    }
                }
            }
        }
    }

    private fun getDogList() {
        homeViewModel.getDogList(currentAccessToken)
        homeViewModel.homeDogList.observe(viewLifecycleOwner) { response ->
            // 통신 성공
            if (response != null && response.code() == 200) {
                if (response.body() != null && !response.body()?.dogs.isNullOrEmpty() &&
                    homeViewModel.homeSelectedDogPos.value == null
                ) {
                    binding.recyclerviewHomeDog.visibility = View.VISIBLE
                    binding.linearlayoutDogExist.visibility = View.VISIBLE
                    binding.linearlayoutDogNotExist.visibility = View.GONE
                    val adapter = binding.recyclerviewHomeDog.adapter as HomeDogProfileAdapter
                    adapter.updateDogProfileList(response.body()?.dogs!!)
                    homeViewModel.setSelectedDogPos(0)
                }
                if (response.body()?.dogs.isNullOrEmpty()) {
                    binding.recyclerviewHomeDog.visibility = View.GONE
                    binding.linearlayoutDogExist.visibility = View.GONE
                    binding.linearlayoutDogNotExist.visibility = View.VISIBLE
                }
            } else if (response != null && response.code() == 401) {
                reissueAccessToken()
            } else {
                binding.recyclerviewHomeDog.visibility = View.GONE
                binding.linearlayoutDogExist.visibility = View.GONE
                binding.linearlayoutDogNotExist.visibility = View.VISIBLE
            }
        }
    }

    private fun getDogInfo() {
        if (homeViewModel.homeSelectedDogId.value != null) {
            val weightRequest = WeightPostRequest(homeViewModel.homeSelectedDogId.value!!, getCurrentDate())
            postDogWeight(weightRequest)
            getDogFeed()
            getDogSupplements()
            getDogExcreta()
            getDogSymptom()
        }
    }

    private fun postDogWeight(weightRequest: WeightPostRequest) {
        homeViewModel.postDogWeight(currentAccessToken, weightRequest)
        homeViewModel.homeDogWeightPost.observe(viewLifecycleOwner) { responseCode ->
            if (responseCode != null && responseCode == 200) {
                getDogWeight()
            } else if (responseCode != null && responseCode == 400) {
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
        homeViewModel.homeDogWeight.observe(viewLifecycleOwner) { response ->
            if (response != null && response.code() == 200) {
                binding.textviewHomeWeight.text = response.body()?.weight?.toString()
                dogViewModel.setDogWeight(response.body()?.weight!!)
            } else if (response != null && response.code() == 401) {
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
        homeViewModel.homeDogFeed.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                if (response.code() == 200) {
                    binding.textviewHomeFeed.text = response.body()?.recommendIntake.toString()
                } else if (response.code() == 401) {
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
        homeViewModel.homeDogSupplements.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                if (response.code() == 200) {
                    binding.textviewHomeNutritionPercentage.text = response.body()?.supplementsRate.toString()
                    binding.progressbarNutrition.progress = response.body()?.supplementsRate ?: 0
                } else if (response.code() == 401) {
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
        homeViewModel.homeDogExcreta.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                if (response.code() == 200) {
                    binding.textviewHomeFecesCount.text = response.body()?.fecesCount.toString()
                    binding.textviewHomeUrineCount.text = response.body()?.urineCount.toString()
                } else if (response.code() == 401) {
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
        homeViewModel.homeDogSymptom.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                if (response.code() == 200) {
                    binding.textviewHomeSymptomSub.setText(R.string.home_symptom_exist)
                    binding.recyclerviewHomeSymptom.visibility = View.VISIBLE
                    val adapter = binding.recyclerviewHomeSymptom.adapter as HomeSymptomAdapter
                    adapter.updateSymptomList(response.body()?.symptomRecords!!)
                } else if (response.code() == 401) {
                    if (currentRefreshToken.isNotEmpty()) {
                        reissueAccessToken()
                    }
                } else {
                    CustomSnackBar.make(
                        requireView(),
                        R.drawable.snackbar_error_16dp,
                        getString(R.string.snack_bar_failure),
                    ).show()
                    binding.textviewHomeSymptomSub.setText(R.string.home_symptom_not_exist)
                    binding.recyclerviewHomeSymptom.visibility = View.GONE
                }
                if (response.body()?.symptomRecords.isNullOrEmpty()) {
                    binding.textviewHomeSymptomSub.setText(R.string.home_symptom_not_exist)
                    binding.recyclerviewHomeSymptom.visibility = View.GONE
                }
            } else {
                CustomSnackBar.make(
                    requireView(),
                    R.drawable.snackbar_error_16dp,
                    getString(R.string.snack_bar_failure),
                ).show()
                binding.textviewHomeSymptomSub.setText(R.string.home_symptom_not_exist)
                binding.recyclerviewHomeSymptom.visibility = View.GONE
            }
        }
    }

    override fun onDogProfileClick(pos: Int) {
        homeViewModel.setSelectedDogPos(pos)
    }

    override fun onItemClick(date: Date) {
        homeViewModel.setSelectedDate(date)
    }

    override fun onDateSubmit(str: String) {
        homeViewModel.setSelectedDate(stringToDate(str))
    }
}
