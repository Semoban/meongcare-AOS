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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentSupplementBinding
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.medicalRecord.viewmodel.DogViewModel
import com.project.meongcare.medicalRecord.viewmodel.UserViewModel
import com.project.meongcare.supplement.viewmodel.SupplementViewModel
import com.project.meongcare.toolbar.viewmodel.ToolbarViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
class SupplementFragment : Fragment() {
    private var _binding: FragmentSupplementBinding? = null
    private val binding get() = _binding!!

    private val supplementViewModel: SupplementViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val dogViewModel: DogViewModel by viewModels()
    private lateinit var toolbarViewModel: ToolbarViewModel

    private var accessToken = ""
    private var dogId = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSupplementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        toolbarViewModel = ViewModelProvider(requireActivity())[ToolbarViewModel::class.java]
        initComposeView()
        fetchSupplements()
    }

    private fun initComposeView() {
        binding.composeViewSupplement.run {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SemobanTheme {
                    val dogName by dogViewModel.dogNamePreferencesLiveData.observeAsState()
                    val supplements by supplementViewModel.supplementList.observeAsState()
                    val selectedDate by toolbarViewModel.selectedDate.observeAsState()

                    SupplementScreen(
                        dogName = dogName,
                        supplements = supplements.orEmpty(),
                        showCheck = selectedDate?.after(Date()) != true,
                        onAddClick = ::navigateToSupplementAdd,
                        onEditClick = ::navigateToSupplementRoutineEdit,
                        onItemClick = ::navigateToSupplementInfo,
                        onCheckClick = { supplementsRecordId ->
                            supplementViewModel.checkSupplement(accessToken, supplementsRecordId)
                        },
                    )
                }
            }
        }
    }

    private fun fetchSupplements() {
        userViewModel.accessTokenPreferencesLiveData.observe(viewLifecycleOwner) { accessToken ->
            if (accessToken != null) {
                this.accessToken = accessToken
                fetchSupplementsIfReady()
            }
        }
        dogViewModel.dogIdPreferencesLiveData.observe(viewLifecycleOwner) { dogId ->
            if (dogId != null) {
                this.dogId = dogId
                fetchSupplementsIfReady()
            }
        }
        toolbarViewModel.selectedDate.observe(viewLifecycleOwner) { date ->
            if (date != null) {
                fetchSupplementsIfReady()
            }
        }
    }

    // 날짜·토큰·dogId가 모두 준비된 시점에만 목록을 조회한다
    private fun fetchSupplementsIfReady() {
        val date = toolbarViewModel.selectedDate.value ?: return
        if (accessToken.isEmpty() || dogId == 0L) return

        supplementViewModel.getSupplements(accessToken, dogId, date)
    }

    private fun navigateToSupplementAdd() {
        findNavController().navigate(R.id.action_supplement_to_supplementAdd)
    }

    private fun navigateToSupplementRoutineEdit() {
        findNavController().navigate(R.id.action_supplement_to_supplementRoutineEdit)
    }

    private fun navigateToSupplementInfo(supplementsId: Int) {
        val bundle = Bundle()
        bundle.putInt("supplementsId", supplementsId)
        findNavController().navigate(R.id.action_supplement_to_supplementInfo, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
