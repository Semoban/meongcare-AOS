package com.project.meongcare.symptom.view

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.project.meongcare.MainActivity
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentSymptomBinding
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.symptom.model.data.repository.SymptomRepository
import com.project.meongcare.symptom.model.entities.Symptom
import com.project.meongcare.symptom.viewmodel.SymptomViewModel
import com.project.meongcare.symptom.viewmodel.SymptomViewModelFactory
import com.project.meongcare.toolbar.viewmodel.ToolbarViewModel

class SymptomFragment : Fragment() {
    private var _binding: FragmentSymptomBinding? = null
    private val binding get() = _binding!!

    lateinit var symptomViewModel: SymptomViewModel
    lateinit var toolbarViewModel: ToolbarViewModel
    lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSymptomBinding.inflate(inflater, container, false)

        val mainActivity = activity as MainActivity
        toolbarViewModel = mainActivity.toolbarViewModel
        val factory = SymptomViewModelFactory(SymptomRepository())
        symptomViewModel = ViewModelProvider(this, factory)[SymptomViewModel::class.java]
        navController = findNavController()

        symptomViewModel.clearLiveData()

        toolbarViewModel.selectedDate.observe(viewLifecycleOwner) { date ->
            if (date != null) {
                symptomViewModel.getSymptomList(date)
            }
        }

        initComposeView()

        return binding.root
    }

    private fun initComposeView() {
        binding.composeViewSymptom.run {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SemobanTheme {
                    val dogName by symptomViewModel.dogName.observeAsState()
                    val symptomList by symptomViewModel.symptomList.observeAsState(emptyList())

                    SymptomScreen(
                        dogName = dogName,
                        symptomList = symptomList,
                        onAddClick = ::navigateToSymptomAdd,
                        onEditClick = { navigateToSymptomListEdit(symptomList) },
                        onItemClick = ::navigateToSymptomInfo,
                    )
                }
            }
        }
    }

    private fun navigateToSymptomAdd() {
        navController.navigate(R.id.action_symptom_to_symptomAdd)
    }

    private fun navigateToSymptomListEdit(symptomList: List<Symptom>) {
        val bundle = Bundle()
        bundle.putParcelableArrayList(
            "symptomList",
            ArrayList<Parcelable>(symptomList),
        )
        navController.navigate(R.id.action_symptom_to_symptomListEdit, bundle)
    }

    private fun navigateToSymptomInfo(position: Int) {
        val bundle = Bundle()
        symptomViewModel.updateSymptomData(position)
        bundle.putParcelable("symptomData", symptomViewModel.infoSymptomData.value)
        navController.navigate(R.id.action_symptom_to_symptomInfo, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
