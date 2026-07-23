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
import com.project.meongcare.databinding.FragmentSupplementInfoBinding
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.medicalRecord.viewmodel.UserViewModel
import com.project.meongcare.snackbar.view.CustomSnackBar
import com.project.meongcare.supplement.viewmodel.SupplementViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SupplementInfoFragment : Fragment() {
    private var _binding: FragmentSupplementInfoBinding? = null
    private val binding get() = _binding!!

    private val supplementViewModel: SupplementViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    private var accessToken = ""
    private var supplementsId = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSupplementInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        supplementsId = arguments?.getInt("supplementsId") ?: 0
        initComposeView()
        fetchSupplementDetail()
        observeSupplementDeleteCode()
    }

    private fun initComposeView() {
        binding.composeViewSupplementInfo.run {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SemobanTheme {
                    val detail by supplementViewModel.supplementDetail.observeAsState()
                    val isRoutineActive by supplementViewModel.routineIsClicked.observeAsState()

                    SupplementInfoScreen(
                        detail = detail,
                        isRoutineActive = isRoutineActive == true,
                        onBackClick = { findNavController().popBackStack() },
                        onRoutineToggle = {
                            supplementViewModel.patchSupplementActive(
                                accessToken,
                                supplementsId,
                                isRoutineActive != true,
                            )
                        },
                        onDeleteConfirm = {
                            supplementViewModel.deleteSupplement(accessToken, supplementsId)
                        },
                    )
                }
            }
        }
    }

    private fun fetchSupplementDetail() {
        userViewModel.accessTokenPreferencesLiveData.observe(viewLifecycleOwner) { accessToken ->
            if (accessToken != null) {
                this.accessToken = accessToken
                supplementViewModel.getSupplementDetail(accessToken, supplementsId)
            }
        }
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
            activity?.findViewById(android.R.id.content)!!,
            R.drawable.snackbar_success_16dp,
            getString(R.string.supplement_delete_success),
        ).show()
    }

    private fun showFailSnackbar() {
        CustomSnackBar.make(
            activity?.findViewById(android.R.id.content)!!,
            R.drawable.snackbar_error_16dp,
            getString(R.string.supplement_delete_failure),
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
