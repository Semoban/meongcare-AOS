package com.project.meongcare.weight.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentWeightBinding
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.excreta.utils.SUCCESS
import com.project.meongcare.feed.viewmodel.DogViewModel
import com.project.meongcare.feed.viewmodel.UserViewModel
import com.project.meongcare.snackbar.view.CustomSnackBar
import com.project.meongcare.toolbar.view.CalendarBottomSheetDialogFragment
import com.project.meongcare.toolbar.view.ToolbarCalendarWeek
import com.project.meongcare.toolbar.viewmodel.ToolbarViewModel
import com.project.meongcare.weight.model.entities.WeightGetRequest
import com.project.meongcare.weight.model.entities.WeightPatchRequest
import com.project.meongcare.weight.model.entities.WeightPostRequest
import com.project.meongcare.weight.viewmodel.WeightViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class WeightFragment : Fragment() {
    private var _binding: FragmentWeightBinding? = null
    private val binding get() = _binding!!

    private val weightViewModel: WeightViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val dogViewModel: DogViewModel by viewModels()
    lateinit var toolbarViewModel: ToolbarViewModel

    private var accessToken = ""
    private var dogId = 0L
    private var date = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentWeightBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        toolbarViewModel = ViewModelProvider(requireActivity())[ToolbarViewModel::class.java]
        initComposeView()
        fetchUserAndDogInfo()
        observeFetchTriggers()
        observeWeightPosted()
        observeWeightPatched()
    }

    private fun initComposeView() {
        binding.composeViewWeight.run {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SemobanTheme {
                    val selectedDate by toolbarViewModel.selectedDate.observeAsState()
                    val dateList by toolbarViewModel.dateList.observeAsState()
                    val selectedDatePos by toolbarViewModel.selectDatePosition.observeAsState()
                    val dogName by dogViewModel.dogName.observeAsState()
                    val dayWeight by weightViewModel.dayWeightGet.observeAsState()
                    val weeklyWeights by weightViewModel.weeklyWeightGet.observeAsState()
                    val monthlyWeight by weightViewModel.monthlyWeightGet.observeAsState()

                    var showEditDialog by rememberSaveable { mutableStateOf(false) }

                    Column {
                        ToolbarCalendarWeek(
                            selectedDate = selectedDate,
                            dateList = dateList.orEmpty(),
                            selectedDatePos = selectedDatePos,
                            onTitleClick = ::showCalendarBottomSheet,
                            onDateClick = toolbarViewModel::selectDateAt,
                            onWeekSwipe = toolbarViewModel::moveWeek,
                        )
                        WeightScreen(
                            dogName = dogName,
                            dailyWeight = dayWeight?.weight,
                            weeklyWeights = weeklyWeights,
                            monthlyWeight = monthlyWeight,
                            thisMonth = selectedDate?.let { convertThisMonth(it) } ?: 0F,
                            isEditable = selectedDate?.let { !isFutureDate(it) } ?: false,
                            onEditClick = { showEditDialog = true },
                        )
                    }

                    if (showEditDialog) {
                        WeightEditDialog(
                            initialWeight = dayWeight?.weight?.toString().orEmpty(),
                            onCancel = { showEditDialog = false },
                            onConfirm = { weight ->
                                showEditDialog = false
                                patchWeight(weight)
                            },
                        )
                    }
                }
            }
        }
    }

    private fun fetchUserAndDogInfo() {
        userViewModel.fetchAccessToken()
        dogViewModel.fetchDogId()
        dogViewModel.fetchDogName()
    }

    private fun observeFetchTriggers() {
        toolbarViewModel.selectedDate.observe(viewLifecycleOwner) { selectedDate ->
            date = convertSelectedDate(selectedDate)
            fetchAllWeights()
        }
        userViewModel.accessToken.observe(viewLifecycleOwner) { response ->
            accessToken = response
            fetchAllWeights()
        }
        dogViewModel.dogId.observe(viewLifecycleOwner) { response ->
            dogId = response
            fetchAllWeights()
        }
    }

    private fun fetchAllWeights() {
        if (accessToken.isEmpty() || dogId == 0L || date.isEmpty()) return

        // 오늘 날짜의 체중 기록을 먼저 생성(없으면 빈 값)한 뒤 응답을 받아 일별 체중을 조회한다
        weightViewModel.postWeight(accessToken, WeightPostRequest(dogId, LocalDate.now().toString(), null))
        weightViewModel.getWeeklyWeight(accessToken, weightGetRequest())
        weightViewModel.getMonthlyWeight(accessToken, weightGetRequest())
    }

    private fun observeWeightPosted() {
        weightViewModel.weightPosted.observe(viewLifecycleOwner) { response ->
            if (response == SUCCESS) {
                weightViewModel.getDailyWeight(accessToken, weightGetRequest())
            }
        }
    }

    private fun observeWeightPatched() {
        weightViewModel.weightPatched.observe(viewLifecycleOwner) { response ->
            if (response == null) return@observe
            if (response == SUCCESS) {
                fetchAllWeights()
                CustomSnackBar.make(requireView(), R.drawable.snackbar_success_16dp, "체중이 수정되었습니다!").show()
            } else {
                CustomSnackBar.make(
                    requireView(),
                    R.drawable.snackbar_error_16dp,
                    "서버가 불안정 하여 체중 수정에 실패하였습니다.\n잠시 후 다시 시도해 주세요.",
                ).show()
            }
        }
    }

    private fun patchWeight(weight: Double) {
        val weightPatchRequest =
            WeightPatchRequest(
                dogId,
                weight,
                date,
            )
        weightViewModel.patchWeight(accessToken, weightPatchRequest)
    }

    private fun showCalendarBottomSheet() {
        CalendarBottomSheetDialogFragment.show(parentFragmentManager, toolbarViewModel)
    }

    private fun isFutureDate(selectedDate: Date): Boolean = LocalDate.parse(convertSelectedDate(selectedDate)).isAfter(LocalDate.now())

    private fun weightGetRequest() = WeightGetRequest(dogId, date)

    private fun convertSelectedDate(selectedDate: Date): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(selectedDate.time)
    }

    private fun convertThisMonth(selectedDate: Date): Float {
        val dateFormat = SimpleDateFormat("MM", Locale.getDefault())
        return dateFormat.format(selectedDate.time).toFloat()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
