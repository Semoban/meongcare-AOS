package com.project.meongcare.onboarding.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.project.meongcare.databinding.FragmentDogVarietySearchBinding
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.onboarding.viewmodel.DogTypeSharedViewModel
import com.project.meongcare.onboarding.viewmodel.DogTypeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DogVarietySearchFragment : Fragment() {
    private var _binding: FragmentDogVarietySearchBinding? = null
    private val binding get() = _binding!!

    private val dogTypeViewModel: DogTypeViewModel by viewModels()
    private val dogTypeSharedViewModel: DogTypeSharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDogVarietySearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initComposeView()
    }

    private fun initComposeView() {
        binding.composeViewDogVarietySearch.run {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SemobanTheme {
                    val dogTypes by dogTypeViewModel.dogTypeList.observeAsState()

                    DogVarietySearchScreen(
                        dogTypes = dogTypes.orEmpty(),
                        onBackClick = { findNavController().popBackStack() },
                        onQueryChange = dogTypeViewModel::searchDogType,
                        onDogTypeSelect = ::selectDogType,
                    )
                }
            }
        }
    }

    private fun selectDogType(dogType: String) {
        dogTypeSharedViewModel.setDogType(dogType)
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
