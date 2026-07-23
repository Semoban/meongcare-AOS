package com.project.meongcare.excreta.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.project.meongcare.R
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.excreta.utils.SUCCESS
import com.project.meongcare.excreta.viewmodel.ExcretaDeleteViewModel
import com.project.meongcare.excreta.viewmodel.ExcretaRecordViewModel
import com.project.meongcare.feed.viewmodel.DogViewModel
import com.project.meongcare.feed.viewmodel.UserViewModel
import com.project.meongcare.snackbar.view.CustomSnackBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExcretaRecordEditFragment : Fragment() {
    private val excretaRecordViewModel: ExcretaRecordViewModel by viewModels()
    private val excretaDeleteViewModel: ExcretaDeleteViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val dogViewModel: DogViewModel by viewModels()

    private val checkedIds = MutableLiveData<List<Long>>(emptyList())
    private var accessToken = ""
    private var dogId = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SemobanTheme {
                    val dogName by dogViewModel.dogName.observeAsState()
                    val excretaRecord by excretaRecordViewModel.excretaRecordGet.observeAsState()
                    val checked by checkedIds.observeAsState(emptyList())

                    ExcretaRecordEditScreen(
                        dogName = dogName,
                        excretaRecords = excretaRecord?.excretaRecords.orEmpty(),
                        checkedIds = checked,
                        onBack = { findNavController().popBackStack() },
                        onToggleAll = ::toggleAll,
                        onToggleItem = ::toggleItem,
                        onCancel = { findNavController().popBackStack() },
                        onDelete = ::deleteCheckedRecords,
                    )
                }
            }
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        dogViewModel.fetchDogId()
        dogViewModel.fetchDogName()
        dogViewModel.dogId.observe(viewLifecycleOwner) { response ->
            dogId = response
        }
        userViewModel.fetchAccessToken()
        userViewModel.accessToken.observe(viewLifecycleOwner) { response ->
            accessToken = response
            excretaRecordViewModel.getExcretaRecord(dogId, accessToken, getSelectedDateTime())
        }
        excretaDeleteViewModel.excretaDeleted.observe(viewLifecycleOwner) { response ->
            if (response == SUCCESS) {
                findNavController().popBackStack()
            }
        }
    }

    private fun toggleAll() {
        val excretaIds =
            excretaRecordViewModel.excretaRecordGet.value
                ?.excretaRecords
                .orEmpty()
                .map { it.excretaId }
        val allChecked = checkedIds.value!!.size == excretaIds.size
        checkedIds.value = if (allChecked) emptyList() else excretaIds
    }

    private fun toggleItem(excretaId: Long) {
        val currentIds = checkedIds.value!!.toMutableList()
        if (currentIds.contains(excretaId)) {
            currentIds.remove(excretaId)
        } else {
            currentIds.add(excretaId)
        }
        checkedIds.value = currentIds
    }

    private fun deleteCheckedRecords() {
        val ids = checkedIds.value!!
        if (ids.isEmpty()) {
            CustomSnackBar.make(
                requireView(),
                R.drawable.snackbar_error_16dp,
                getString(R.string.snack_bar_delete_none_selected),
            ).show()
            return
        }
        excretaDeleteViewModel.deleteExcreta(accessToken, ids.map { it.toInt() }.toIntArray())
    }

    private fun getSelectedDateTime() = arguments?.getString("selectedDateTime")!!
}
