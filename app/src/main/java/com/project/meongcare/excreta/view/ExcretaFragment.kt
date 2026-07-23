package com.project.meongcare.excreta.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentExcretaBinding
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.excreta.viewmodel.ExcretaRecordViewModel
import com.project.meongcare.feed.viewmodel.DogViewModel
import com.project.meongcare.feed.viewmodel.UserViewModel
import com.project.meongcare.toolbar.view.CalendarBottomSheetDialogFragment
import com.project.meongcare.toolbar.view.ToolbarCalendarWeek
import com.project.meongcare.toolbar.viewmodel.ToolbarViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class ExcretaFragment : Fragment() {
    private var _binding: FragmentExcretaBinding? = null
    private val binding get() = _binding!!

    private val excretaRecordViewModel: ExcretaRecordViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val dogViewModel: DogViewModel by viewModels()
    lateinit var toolbarViewModel: ToolbarViewModel

    private var accessToken = ""
    private var dogId = 0L
    private var dateTime = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentExcretaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initDogInfo()
        toolbarViewModel = ViewModelProvider(requireActivity())[ToolbarViewModel::class.java]
        toolbarViewModel.selectedDate.observe(viewLifecycleOwner) { date ->
            dateTime = convertSelectedDate(date)
            userViewModel.fetchAccessToken()
        }
        userViewModel.accessToken.observe(viewLifecycleOwner) { response ->
            accessToken = response
            excretaRecordViewModel.getExcretaRecord(dogId, accessToken, dateTime)
        }
        initComposeView()
    }

    private fun initDogInfo() {
        dogViewModel.fetchDogId()
        dogViewModel.fetchDogName()
        dogViewModel.dogId.observe(viewLifecycleOwner) { response ->
            dogId = response
        }
    }

    private fun initComposeView() {
        binding.composeViewExcreta.run {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SemobanTheme {
                    val selectedDate by toolbarViewModel.selectedDate.observeAsState()
                    val dateList by toolbarViewModel.dateList.observeAsState()
                    val selectedDatePos by toolbarViewModel.selectDatePosition.observeAsState()
                    val dogName by dogViewModel.dogName.observeAsState()
                    val excretaRecord by excretaRecordViewModel.excretaRecordGet.observeAsState()

                    Column {
                        ToolbarCalendarWeek(
                            selectedDate = selectedDate,
                            dateList = dateList.orEmpty(),
                            selectedDatePos = selectedDatePos,
                            onTitleClick = ::showCalendarBottomSheet,
                            onDateClick = toolbarViewModel::selectDateAt,
                            onWeekSwipe = toolbarViewModel::moveWeek,
                        )
                        ExcretaScreen(
                            dogName = dogName,
                            fecesCount = excretaRecord?.fecesCount ?: 0,
                            urineCount = excretaRecord?.urineCount ?: 0,
                            excretaRecords = excretaRecord?.excretaRecords.orEmpty(),
                            onAddClick = ::navigateToExcretaAdd,
                            onEditClick = ::navigateToExcretaRecordEdit,
                            onItemClick = ::navigateToExcretaInfo,
                        )
                    }
                }
            }
        }
    }

    private fun navigateToExcretaAdd() {
        val bundle = Bundle()
        bundle.putString("selectedDateTime", dateTime)
        findNavController().navigate(R.id.action_excretaFragment_to_excretaAddFragment, bundle)
    }

    private fun navigateToExcretaRecordEdit() {
        val bundle = Bundle()
        bundle.putString("selectedDateTime", dateTime)
        findNavController().navigate(R.id.action_excretaFragment_to_excretaRecordEditFragment, bundle)
    }

    private fun navigateToExcretaInfo(excretaId: Long) {
        val bundle = Bundle()
        bundle.putLong("excretaId", excretaId)
        findNavController().navigate(R.id.action_excretaFragment_to_excretaInfoFragment, bundle)
    }

    private fun showCalendarBottomSheet() {
        CalendarBottomSheetDialogFragment.show(parentFragmentManager, toolbarViewModel)
    }

    private fun convertSelectedDate(selectedDate: Date): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        return dateFormat.format(selectedDate.time)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
