package com.project.meongcare.medicalRecord.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentMedicalRecordEditBinding
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.medicalRecord.viewmodel.DogViewModel
import com.project.meongcare.medicalRecord.viewmodel.MedicalRecordViewModel
import com.project.meongcare.medicalRecord.viewmodel.UserViewModel
import com.project.meongcare.snackbar.view.CustomSnackBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MedicalRecordEditFragment : Fragment() {
    private var _binding: FragmentMedicalRecordEditBinding? = null
    private val binding get() = _binding!!

    private val dogViewModel: DogViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val medicalRecordViewModel: MedicalRecordViewModel by viewModels()

    private var selectedDate = ""
    private var accessToken = ""
    private var dogId = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedDate = arguments?.getString("selectedDate")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMedicalRecordEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initComposeView()
        fetchMedicalRecordList()
        observeMedicalRecordDeleted()
    }

    private fun initComposeView() {
        binding.composeViewMedicalRecordEdit.run {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SemobanTheme {
                    val dogName by dogViewModel.dogNamePreferencesLiveData.observeAsState()
                    val medicalRecordResponse by medicalRecordViewModel.medicalRecordList.observeAsState()
                    val records = medicalRecordResponse?.body()?.records.orEmpty()

                    var checkedIds by rememberSaveable { mutableStateOf(listOf<Int>()) }

                    MedicalRecordEditScreen(
                        dogName = dogName,
                        records = records,
                        checkedIds = checkedIds,
                        onBackClick = { findNavController().popBackStack() },
                        onToggleAll = {
                            checkedIds =
                                if (checkedIds.size == records.size) {
                                    emptyList()
                                } else {
                                    records.map { it.medicalRecordId.toInt() }
                                }
                        },
                        onToggleItem = { id ->
                            checkedIds =
                                if (checkedIds.contains(id)) {
                                    checkedIds - id
                                } else {
                                    checkedIds + id
                                }
                        },
                        onCancelClick = { findNavController().popBackStack() },
                        onDeleteClick = {
                            medicalRecordViewModel.deleteMedicalRecordList(checkedIds.toIntArray())
                        },
                    )
                }
            }
        }
    }

    private fun fetchMedicalRecordList() {
        dogViewModel.dogIdPreferencesLiveData.observe(viewLifecycleOwner) { dogId ->
            if (dogId != null) {
                this.dogId = dogId
                fetchAccessToken()
            }
        }
    }

    private fun fetchAccessToken() {
        userViewModel.accessTokenPreferencesLiveData.observe(viewLifecycleOwner) { accessToken ->
            if (accessToken != null) {
                this.accessToken = accessToken
                medicalRecordViewModel.getMedicalRecordList(
                    dogId,
                    selectedDate + "T00:00:00",
                    accessToken,
                )
            }
        }
    }

    private fun observeMedicalRecordDeleted() {
        medicalRecordViewModel.deleteMedicalRecordResponse.observe(viewLifecycleOwner) { response ->
            if (response != null && response == 200) {
                CustomSnackBar.make(
                    requireView(),
                    R.drawable.snackbar_success_16dp,
                    getString(R.string.medicalrecord_delete_success),
                ).show()
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
