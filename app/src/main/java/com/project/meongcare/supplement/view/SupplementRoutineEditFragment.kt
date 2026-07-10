package com.project.meongcare.supplement.view

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
import com.project.meongcare.databinding.FragmentSupplementRoutineEditBinding
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.medicalRecord.viewmodel.DogViewModel
import com.project.meongcare.medicalRecord.viewmodel.UserViewModel
import com.project.meongcare.snackbar.view.CustomSnackBar
import com.project.meongcare.supplement.model.entities.SupplementDog
import com.project.meongcare.supplement.viewmodel.SupplementViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SupplementRoutineEditFragment : Fragment() {
    private var _binding: FragmentSupplementRoutineEditBinding? = null
    private val binding get() = _binding!!

    private val supplementViewModel: SupplementViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val dogViewModel: DogViewModel by viewModels()

    private var accessToken = ""
    private var dogId = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSupplementRoutineEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initComposeView()
        fetchSupplementDogs()
        observeSupplementDeleteCode()
    }

    private fun initComposeView() {
        binding.composeViewSupplementRoutineEdit.run {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SemobanTheme {
                    val supplements by supplementViewModel.supplementDogList.observeAsState()
                    val checkedIds by supplementViewModel.supplementIdList.observeAsState()

                    SupplementRoutineEditScreen(
                        supplements = supplements.orEmpty(),
                        checkedIds = checkedIds.orEmpty(),
                        onBackClick = { findNavController().popBackStack() },
                        onToggleAllClick = supplementViewModel::toggleAllSupplementIdSelection,
                        onToggleClick = supplementViewModel::toggleSupplementIdSelection,
                        onAlarmClick = ::patchSupplementAlarm,
                        onItemClick = ::navigateToSupplementInfo,
                        onDeleteConfirm = {
                            supplementViewModel.deleteSupplements(
                                accessToken,
                                supplementViewModel.supplementIdList.value.orEmpty().toIntArray(),
                            )
                        },
                    )
                }
            }
        }
    }

    private fun fetchSupplementDogs() {
        userViewModel.accessTokenPreferencesLiveData.observe(viewLifecycleOwner) { accessToken ->
            if (accessToken != null) {
                this.accessToken = accessToken
                fetchSupplementDogsIfReady()
            }
        }
        dogViewModel.dogIdPreferencesLiveData.observe(viewLifecycleOwner) { dogId ->
            if (dogId != null) {
                this.dogId = dogId
                fetchSupplementDogsIfReady()
            }
        }
    }

    // 토큰·dogId가 모두 준비된 시점에만 목록을 조회한다
    private fun fetchSupplementDogsIfReady() {
        if (accessToken.isEmpty() || dogId == 0L) return

        supplementViewModel.getSupplementDogs(accessToken, dogId)
    }

    private fun patchSupplementAlarm(supplement: SupplementDog) {
        supplementViewModel.patchSupplementAlarm(
            accessToken,
            supplement.supplementsId,
            !supplement.pushAgreement,
        )
    }

    private fun navigateToSupplementInfo(supplementsId: Int) {
        val bundle = Bundle()
        bundle.putInt("supplementsId", supplementsId)
        findNavController().navigate(R.id.action_supplementRoutineEdit_to_supplementInfo, bundle)
    }

    private fun observeSupplementDeleteCode() {
        supplementViewModel.supplementDeleteCode.observe(viewLifecycleOwner) {
            if (it == 200) {
                showSuccessSnackbar()
                findNavController().popBackStack()
            } else {
                showFailSnackbar()
            }
        }
    }

    private fun showSuccessSnackbar() {
        CustomSnackBar.make(
            requireView(),
            R.drawable.snackbar_success_16dp,
            "삭제가 완료되었습니다.",
        ).show()
    }

    private fun showFailSnackbar() {
        CustomSnackBar.make(
            requireView(),
            R.drawable.snackbar_error_16dp,
            "삭제에 실패하였습니다.\n잠시 후 다시 시도해주세요",
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
