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
import com.project.meongcare.onboarding.model.data.local.DateSubmitListener
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

    @Inject
    lateinit var userPreferences: UserPreferences

    @Inject
    lateinit var dogPreferences: DogPreferences

    lateinit var currentAccessToken: String

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

        homeViewModel.homeDogList.observe(viewLifecycleOwner) { dogList ->
            if (!dogList.isNullOrEmpty()) {
                fragmentHomeBinding.recyclerviewHomeDog.visibility = View.VISIBLE
                fragmentHomeBinding.linearlayoutDogExist.visibility = View.VISIBLE
                fragmentHomeBinding.linearlayoutDogNotExist.visibility = View.GONE
                val adapter = fragmentHomeBinding.recyclerviewHomeDog.adapter as HomeDogProfileAdapter
                adapter.updateDogProfileList(dogList)
                if (homeViewModel.homeSelectedDogPos.value == null) {
                    homeViewModel.setSelectedDogPos(0)
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
            }
        }

        homeViewModel.homeSelectedDogPos.observe(viewLifecycleOwner) { selectedDogPos ->
            if (selectedDogPos != null) {
                Log.d("homeSelectedDogName", homeViewModel.homeDogList.value!![selectedDogPos].name)
                homeViewModel.setSelectedDogId(homeViewModel.homeDogList.value!![selectedDogPos].dogId)
                val adapter = fragmentHomeBinding.recyclerviewHomeDog.adapter as HomeDogProfileAdapter
                adapter.updateSelectedPos(selectedDogPos)
                dogPreferences.setDogId(homeViewModel.homeDogList.value!![selectedDogPos].dogId)
                dogPreferences.setDogName(homeViewModel.homeDogList.value!![selectedDogPos].name)
            }
        }

        homeViewModel.homeDogWeight.observe(viewLifecycleOwner) { dogWeight ->
            if (dogWeight != null) {
                Log.d("homeDogWeight", dogWeight.weight.toString())
                fragmentHomeBinding.textviewHomeWeight.text = dogWeight.weight.toString()
                dogPreferences.setDogWeight(dogWeight.weight)
            }
        }

        homeViewModel.homeDogFeed.observe(viewLifecycleOwner) { dogFeed ->
            if (dogFeed != null) {
                Log.d("homeDogFeed", dogFeed.recommendIntake.toString())
                fragmentHomeBinding.textviewHomeFeed.text = dogFeed.recommendIntake.toString()
            }
        }

        homeViewModel.homeDogSupplements.observe(viewLifecycleOwner) { dogSupplements ->
            if (dogSupplements != null) {
                Log.d("homeDogSupplements", dogSupplements.supplementsRate.toString())
                fragmentHomeBinding.textviewHomeNutritionPercentage.text = dogSupplements.supplementsRate.toString()
                fragmentHomeBinding.progressbarNutrition.progress = dogSupplements.supplementsRate
            }
        }

        homeViewModel.homeDogExcreta.observe(viewLifecycleOwner) { dogExcreta ->
            if (dogExcreta != null) {
                Log.d("homeDogExcreta", dogExcreta.fecesCount.toString())
                fragmentHomeBinding.textviewHomeFecesCount.text = dogExcreta.fecesCount.toString()
                fragmentHomeBinding.textviewHomeUrineCount.text = dogExcreta.urineCount.toString()
            }
        }

        homeViewModel.homeDogSymptom.observe(viewLifecycleOwner) { dogSymptom ->
            if (dogSymptom.symptomRecords.isNullOrEmpty()) {
                fragmentHomeBinding.textviewHomeSymptom2.setText(R.string.home_symptom_not_exist)
                fragmentHomeBinding.recyclerviewHomeSymptom.visibility = View.GONE
            } else {
                fragmentHomeBinding.textviewHomeSymptom2.setText(R.string.home_symptom_exist)
                fragmentHomeBinding.recyclerviewHomeSymptom.visibility = View.VISIBLE
                dogSymptom.symptomRecords.forEach {
                    Log.d("homeDogSymptom", it.symptomString)
                }
                val adapter = fragmentHomeBinding.recyclerviewHomeSymptom.adapter as HomeSymptomAdapter
                adapter.updateSymptomList(dogSymptom.symptomRecords)
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
                // 강아지 정보 등록 화면으로 전환
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
                // 이상 증상 홈 화면으로 전환
            }

            constraintlayoutHomeFeces.setOnClickListener {
                // 대소변 홈 화면으로 전환
            }

            constraintlayoutHomeNutrition.setOnClickListener {
                // 영양제 홈 화면으로 전환
            }

            constraintlayoutHomeWeight.setOnClickListener {
                // 체중 홈 화면으로 전환
            }

            constraintlayoutHomeFeed.setOnClickListener {
                // 사료 홈 화면으로 전환
            }
        }

        return fragmentHomeBinding.root
    }

    private fun getAccessToken() {
        lifecycleScope.launch {
            userPreferences.accessToken.collectLatest { accessToken ->
                if (accessToken != null) {
                    currentAccessToken = accessToken
                    homeViewModel.getUserProfile(currentAccessToken)
                    homeViewModel.getDogList(currentAccessToken)
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
