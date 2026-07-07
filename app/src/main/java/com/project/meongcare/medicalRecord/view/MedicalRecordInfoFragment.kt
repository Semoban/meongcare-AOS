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
import com.project.meongcare.databinding.FragmentMedicalRecordInfoBinding
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.medicalRecord.viewmodel.MedicalRecordViewModel
import com.project.meongcare.snackbar.view.CustomSnackBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MedicalRecordInfoFragment : Fragment() {
    private var _binding: FragmentMedicalRecordInfoBinding? = null
    private val binding get() = _binding!!

    private val medicalRecordViewModel: MedicalRecordViewModel by viewModels()

    private var medicalRecordId = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMedicalRecordInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        medicalRecordId = arguments?.getLong("medicalRecordId") ?: 0L
        initComposeView()
        medicalRecordViewModel.getMedicalRecord(medicalRecordId)
        observeMedicalRecordDeleted()
    }

    private fun initComposeView() {
        binding.composeViewMedicalRecordInfo.run {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SemobanTheme {
                    val medicalRecordResponse by medicalRecordViewModel.medicalRecord.observeAsState()

                    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

                    MedicalRecordInfoScreen(
                        record = medicalRecordResponse?.body(),
                        onBackClick = { findNavController().popBackStack() },
                        onEditClick = ::navigateToMedicalRecordInfoEdit,
                        onDeleteClick = { showDeleteDialog = true },
                    )

                    if (showDeleteDialog) {
                        MedicalRecordDeleteDialog(
                            onCancel = { showDeleteDialog = false },
                            onConfirm = {
                                showDeleteDialog = false
                                medicalRecordViewModel.deleteMedicalRecordList(
                                    intArrayOf(medicalRecordId.toInt()),
                                )
                            },
                        )
                    }
                }
            }
        }
    }

    private fun navigateToMedicalRecordInfoEdit() {
        val record = medicalRecordViewModel.medicalRecord.value?.body() ?: return
        val bundle = Bundle()
        bundle.putParcelable("medicalRecord", record)
        findNavController().navigate(R.id.action_medicalRecordInfoFragment_to_medicalRecordInfoEditFragment, bundle)
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
