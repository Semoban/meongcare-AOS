package com.project.meongcare.excreta.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.project.meongcare.R
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.excreta.model.entities.Excreta
import com.project.meongcare.excreta.model.entities.ExcretaDetailGetResponse
import com.project.meongcare.excreta.utils.ExcretaDateTimeUtils.convertDateTimeFormat
import com.project.meongcare.excreta.utils.ExcretaDateTimeUtils.convertToTimeFormat
import com.project.meongcare.excreta.utils.ExcretaInfoUtils.showFailureSnackBar
import com.project.meongcare.excreta.utils.ExcretaInfoUtils.showSuccessSnackBar
import com.project.meongcare.excreta.utils.SUCCESS
import com.project.meongcare.excreta.viewmodel.ExcretaDeleteViewModel
import com.project.meongcare.excreta.viewmodel.ExcretaDetailViewModel
import com.project.meongcare.feed.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExcretaInfoFragment : Fragment() {
    private val excretaDetailViewModel: ExcretaDetailViewModel by viewModels()
    private val excretaDeleteViewModel: ExcretaDeleteViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    private var excretaInfo: ExcretaDetailGetResponse? = null
    private var accessToken = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SemobanTheme {
                    val excretaDetail by excretaDetailViewModel.excretaDetailGet.observeAsState()

                    ExcretaInfoScreen(
                        imageUrl = excretaDetail?.excretaImageURL,
                        dateText = excretaDetail?.dateTime?.let { convertDateTimeFormat(it) }.orEmpty(),
                        excretaType = excretaDetail?.excretaType?.let { Excreta.valueOf(it) },
                        timeText = excretaDetail?.dateTime?.let { convertToTimeFormat(it) }.orEmpty(),
                        onBack = { findNavController().popBackStack() },
                        onEdit = ::navigateToExcretaEdit,
                        onDelete = ::deleteExcreta,
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
        userViewModel.fetchAccessToken()
        userViewModel.accessToken.observe(viewLifecycleOwner) { response ->
            accessToken = response
            excretaDetailViewModel.getExcretaDetail(accessToken, getExcretaId())
        }
        excretaDetailViewModel.excretaDetailGet.observe(viewLifecycleOwner) { response ->
            excretaInfo = response
        }
        observeExcretaDeleted()
    }

    private fun observeExcretaDeleted() {
        excretaDeleteViewModel.excretaDeleted.observe(viewLifecycleOwner) { response ->
            if (response == null) return@observe
            if (response == SUCCESS) {
                showSuccessSnackBar(
                    requireView(),
                    getString(R.string.excreta_delete_success),
                )
                findNavController().popBackStack()
            } else {
                showFailureSnackBar(
                    requireView(),
                    getString(R.string.excreta_delete_failure),
                )
            }
        }
    }

    private fun navigateToExcretaEdit() {
        val info = excretaInfo ?: return
        val bundle =
            bundleOf(
                "excretaId" to getExcretaId(),
                "excretaInfo" to info,
            )
        findNavController().navigate(R.id.action_excretaInfoFragment_to_excretaEditFragment, bundle)
    }

    private fun deleteExcreta() {
        excretaDeleteViewModel.deleteExcreta(accessToken, intArrayOf(getExcretaId().toInt()))
    }

    private fun getExcretaId() = arguments?.getLong("excretaId")!!
}
