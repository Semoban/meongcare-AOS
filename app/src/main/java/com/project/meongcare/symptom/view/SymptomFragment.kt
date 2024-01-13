package com.project.meongcare.symptom.view

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.meongcare.MainActivity
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentSymptomBinding
import com.project.meongcare.symptom.model.data.repository.SymptomRepository
import com.project.meongcare.symptom.viewmodel.SymptomViewModel
import com.project.meongcare.symptom.viewmodel.SymptomViewModelFactory
import com.project.meongcare.toolbar.viewmodel.ToolbarViewModel

class SymptomFragment : Fragment() {
    lateinit var fragmentSymptomBinding: FragmentSymptomBinding
    lateinit var mainActivity: MainActivity
    lateinit var symptomViewModel: SymptomViewModel
    lateinit var toolbarViewModel: ToolbarViewModel
    lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentSymptomBinding = FragmentSymptomBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        toolbarViewModel = mainActivity.toolbarViewModel
        val factory = SymptomViewModelFactory(SymptomRepository())
        symptomViewModel = ViewModelProvider(this, factory)[SymptomViewModel::class.java]

        val dogName = "김대박"

        navController = findNavController()

        toolbarViewModel.run {
            selectedDate.observe(viewLifecycleOwner) {
                if (toolbarViewModel.selectedDate.value != null) {
                    symptomViewModel.getSymptomList(1, toolbarViewModel.selectedDate.value!!)
                }
            }
        }

        symptomViewModel.run {
            clearLiveData()
            getSymptomList(1, toolbarViewModel.selectedDate.value!!)
            symptomList.observe(viewLifecycleOwner) {
                updateVisiblity()
                setRecyclerViewAdapter()
            }
        }


        fragmentSymptomBinding.run {
            textViewSymptomDogName.text = dogName
            textViewSymptomAdd.setOnClickListener {
                navController.navigate(R.id.action_symptom_to_symptomAdd)
            }
            textViewSymptomEdit.setOnClickListener {
                val bundle = Bundle()
                bundle.putParcelableArrayList(
                    "symptomList",
                    symptomViewModel.symptomList.value!! as ArrayList<out Parcelable>
                )
                navController.navigate(R.id.action_symptom_to_symptomListEdit, bundle)
            }
        }
        return fragmentSymptomBinding.root
    }

    private fun setRecyclerViewAdapter() {
        fragmentSymptomBinding.recyclerViewSymptom.run {
            adapter = SymptomRecyclerViewAdapter(symptomViewModel) {
                val bundle = Bundle()
                symptomViewModel.updateSymptomData(it)
                bundle.putParcelable("symptomData", symptomViewModel.infoSymptomData.value)
                navController.navigate(R.id.action_symptom_to_symptomInfo, bundle)
            }

            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun updateVisiblity() {
        if (symptomViewModel.symptomList.value.isNullOrEmpty()) {
            fragmentSymptomBinding.recyclerViewSymptom.visibility = View.GONE
            fragmentSymptomBinding.textViewSymptomEdit.visibility = View.GONE
            fragmentSymptomBinding.layoutSymptomNoData.visibility = View.VISIBLE
        } else {
            fragmentSymptomBinding.recyclerViewSymptom.visibility = View.VISIBLE
            fragmentSymptomBinding.textViewSymptomEdit.visibility = View.VISIBLE
            fragmentSymptomBinding.layoutSymptomNoData.visibility = View.GONE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        symptomViewModel.getSymptomList(1, toolbarViewModel.selectedDate.value!!)
    }

}

