package com.project.meongcare.symptom.view

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.project.meongcare.R
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.snackbar.view.CustomSnackBar
import com.project.meongcare.symptom.model.data.repository.SymptomRepository
import com.project.meongcare.symptom.model.entities.Symptom
import com.project.meongcare.symptom.viewmodel.SymptomViewModel
import com.project.meongcare.symptom.viewmodel.SymptomViewModelFactory

class SymptomListEditFragment : Fragment() {
    lateinit var symptomViewModel: SymptomViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val factory = SymptomViewModelFactory(SymptomRepository())
        symptomViewModel = ViewModelProvider(this, factory)[SymptomViewModel::class.java]

        symptomViewModel.symptomList.value =
            arguments?.getParcelableArrayList<Parcelable>("symptomList") as MutableList<Symptom>

        symptomViewModel.deleteSymptomCode.observe(viewLifecycleOwner) { code ->
            if (code == 200) {
                CustomSnackBar.make(
                    requireView(),
                    R.drawable.snackbar_success_16dp,
                    "삭제가 완료되었습니다.",
                ).show()
                findNavController().popBackStack()
            }
        }

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SemobanTheme {
                    val dogName by symptomViewModel.dogName.observeAsState()
                    val symptomList by symptomViewModel.symptomList.observeAsState(emptyList())
                    val checkedIds by symptomViewModel.symptomIdList.observeAsState(emptyList())

                    SymptomListEditScreen(
                        dogName = dogName,
                        symptomList = symptomList,
                        checkedIds = checkedIds,
                        onBack = { findNavController().popBackStack() },
                        onToggleAll = ::toggleAll,
                        onToggleItem = ::toggleItem,
                        onCancel = { findNavController().popBackStack() },
                        onEmptyDelete = ::showEmptySelectionSnackbar,
                        onDeleteConfirm = ::deleteCheckedSymptoms,
                    )
                }
            }
        }
    }

    private fun toggleAll() {
        val symptomIds = symptomViewModel.symptomList.value!!.map { it.symptomId }
        val allChecked = symptomViewModel.symptomIdList.value!!.size == symptomIds.size
        symptomViewModel.symptomIdList.value =
            if (allChecked) mutableListOf() else symptomIds.toMutableList()
    }

    private fun toggleItem(symptomId: Int) {
        val checkedIds = symptomViewModel.symptomIdList.value!!.toMutableList()
        if (checkedIds.contains(symptomId)) {
            checkedIds.remove(symptomId)
        } else {
            checkedIds.add(symptomId)
        }
        symptomViewModel.symptomIdList.value = checkedIds
    }

    private fun showEmptySelectionSnackbar() {
        CustomSnackBar.make(
            requireView(),
            R.drawable.snackbar_error_16dp,
            "선택된 항목이 없습니다.\n항목을 선택하고 삭제해주세요.",
        ).show()
    }

    private fun deleteCheckedSymptoms() {
        symptomViewModel.deleteSymptom(symptomViewModel.symptomIdList.value!!.toIntArray())
    }
}
