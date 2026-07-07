package com.project.meongcare.medicalRecord.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentMedicalRecordBinding
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.medicalRecord.viewmodel.DogViewModel
import com.project.meongcare.medicalRecord.viewmodel.MedicalRecordViewModel
import com.project.meongcare.medicalRecord.viewmodel.UserViewModel
import com.project.meongcare.snackbar.view.CustomSnackBar
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class MedicalRecordFragment : Fragment() {
    private var _binding: FragmentMedicalRecordBinding? = null
    private val binding get() = _binding!!

    private val dogViewModel: DogViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val medicalRecordViewModel: MedicalRecordViewModel by viewModels()

    private var accessToken = ""
    private var dogId = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMedicalRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initComposeView()
        fetchUserAndDogInfo()
        observeSelectedDate()
        initTodayDate()
    }

    private fun initTodayDate() {
        if (medicalRecordViewModel.selectedDate.value == null) {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            medicalRecordViewModel.getCurrentDate(today)
        }
    }

    private fun initComposeView() {
        binding.composeViewMedicalRecord.run {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SemobanTheme {
                    val dogName by dogViewModel.dogNamePreferencesLiveData.observeAsState()
                    val selectedDate by medicalRecordViewModel.selectedDate.observeAsState()
                    val medicalRecordResponse by medicalRecordViewModel.medicalRecordList.observeAsState()

                    MedicalRecordScreen(
                        dogName = dogName,
                        selectedDate = selectedDate,
                        records = medicalRecordResponse?.body()?.records.orEmpty(),
                        onDateSelected = { date -> medicalRecordViewModel.getCurrentDate(date) },
                        onAddClick = ::navigateToMedicalRecordAdd,
                        onEditClick = ::navigateToMedicalRecordEdit,
                        onItemClick = ::navigateToMedicalRecordInfo,
                    )
                }
            }
        }
    }

    private fun fetchUserAndDogInfo() {
        dogViewModel.dogIdPreferencesLiveData.observe(viewLifecycleOwner) { dogId ->
            if (dogId != null) {
                this.dogId = dogId
                fetchMedicalRecordListIfReady()
            }
        }
        userViewModel.accessTokenPreferencesLiveData.observe(viewLifecycleOwner) { accessToken ->
            if (accessToken != null) {
                this.accessToken = accessToken
                fetchMedicalRecordListIfReady()
            }
        }
    }

    private fun observeSelectedDate() {
        medicalRecordViewModel.selectedDate.observe(viewLifecycleOwner) { date ->
            if (date != null) {
                fetchMedicalRecordListIfReady()
            }
        }
    }

    // 날짜·토큰·dogId가 모두 준비된 시점에만 목록을 조회한다
    private fun fetchMedicalRecordListIfReady() {
        val date = medicalRecordViewModel.selectedDate.value ?: return
        if (accessToken.isEmpty() || dogId == 0L) return

        medicalRecordViewModel.getMedicalRecordList(
            dogId,
            date + "T00:00:00",
            accessToken,
        )
    }

    private fun navigateToMedicalRecordAdd() {
        findNavController().navigate(R.id.action_medicalRecordFragment_to_medicalRecordAddFragment)
    }

    private fun navigateToMedicalRecordEdit() {
        if (medicalRecordViewModel.selectedDate.value == null) {
            CustomSnackBar.make(
                requireView(),
                R.drawable.snackbar_error_16dp,
                getString(R.string.medicalrecrod_empty_date),
            ).show()
        } else if (medicalRecordViewModel.medicalRecordList.value?.body()?.records?.size == 0) {
            CustomSnackBar.make(
                requireView(),
                R.drawable.snackbar_error_16dp,
                getString(R.string.medicalrecord_no_list),
            ).show()
        } else {
            val bundle = Bundle()
            bundle.putString("selectedDate", "${medicalRecordViewModel.selectedDate.value}")
            findNavController().navigate(R.id.action_medicalRecordFragment_to_medicalRecordEditFragment, bundle)
        }
    }

    private fun navigateToMedicalRecordInfo(medicalRecordId: Long) {
        val bundle = Bundle()
        bundle.putLong("medicalRecordId", medicalRecordId)
        findNavController().navigate(R.id.action_medicalRecordFragment_to_medicalRecordInfoFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
