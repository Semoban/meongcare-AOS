package com.project.meongcare.symptom.view

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
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
import com.project.meongcare.databinding.FragmentSymptomListEditBinding
import com.project.meongcare.snackbar.view.CustomSnackBar
import com.project.meongcare.symptom.model.data.repository.SymptomRepository
import com.project.meongcare.symptom.model.entities.Symptom
import com.project.meongcare.symptom.viewmodel.SymptomViewModel
import com.project.meongcare.symptom.viewmodel.SymptomViewModelFactory
import com.project.meongcare.toolbar.viewmodel.ToolbarViewModel

class SymptomListEditFragment : Fragment() {
    lateinit var fragmentSymptomListEditBinding: FragmentSymptomListEditBinding
    lateinit var mainActivity: MainActivity
    lateinit var symptomViewModel: SymptomViewModel
    lateinit var toolbarViewModel: ToolbarViewModel
    lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentSymptomListEditBinding = FragmentSymptomListEditBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        val factory = SymptomViewModelFactory(SymptomRepository())
        symptomViewModel = ViewModelProvider(this, factory)[SymptomViewModel::class.java]

        toolbarViewModel = mainActivity.toolbarViewModel

        navController = findNavController()

        // TODO : 강아지 이름 연결 필요
        val dogName = "김대박"

        symptomViewModel.symptomList.value =
            arguments?.getParcelableArrayList<Parcelable>("symptomList") as MutableList<Symptom>
        Log.d("증상 리스트", symptomViewModel.symptomList.value.toString())

        symptomViewModel.run {
            symptomIdList.observe(viewLifecycleOwner) {
                Log.d("이상증상 변화", it.toString())
                fragmentSymptomListEditBinding.run {
                    if (it.isNotEmpty() && it.size == symptomList.value!!.size) {
                        imageViewSymptomListEditDeleteAllCheck.isSelected = true
                    }
                    recyclerViewSymptomListEdit.run {
                        adapter = SymptomListEditRecyclerViewAdapter(symptomViewModel)
                        layoutManager = LinearLayoutManager(context)
                    }
                }
            }
            symptomViewModel.deleteSymptomCode.observe(viewLifecycleOwner) { code ->
                if (code == 200) {
                    CustomSnackBar.make(
                        requireView(),
                        R.drawable.snackbar_success_16dp,
                        "삭제가 완료되었습니다."
                    ).show()
                    navController.popBackStack()
                }
            }
        }

        fragmentSymptomListEditBinding.run {
            if (symptomViewModel.symptomList.value.isNullOrEmpty()) {
                scrollViewSymptomListEdit.visibility = View.GONE
                layoutSymptomListEditButton.visibility = View.GONE
                layoutSymptomListEditNoData.visibility = View.VISIBLE
            }

            toolbarSymptomListEdit.run {
                title = "${dogName}님의 이상증상"
                setNavigationOnClickListener {
                    navController.navigate(R.id.action_symptomListEdit_to_symptom)
                }
            }

            if (symptomViewModel.symptomIdListAllCheck.value!!) {
                imageViewSymptomListEditDeleteAllCheck.isSelected = false
            }

            imageViewSymptomListEditDeleteAllCheck.run {
                val temp =
                    symptomViewModel.symptomList.value!!.map { it.symptomId }.toMutableList()
                setOnClickListener { view ->
                    view.isSelected = !view.isSelected
                    if (view.isSelected) {
                        symptomViewModel.symptomIdList.value = temp
                    } else {
                        symptomViewModel.symptomIdList.value = mutableListOf()
                    }
                }
            }

            buttonSymptomListEditCancel.setOnClickListener {
                navController.popBackStack()
            }

            buttonSymptomListEditComplete.setOnClickListener {
                if (symptomViewModel.symptomIdList.value.isNullOrEmpty()) {
                    CustomSnackBar.make(
                        requireView(),
                        R.drawable.snackbar_error_16dp,
                        "선택된 항목이 없습니다.\n항목을 선택하고 삭제해주세요."
                    ).show()
                } else {
                    includeSymptomListEditDeleteDialog.run {
                        root.visibility = View.VISIBLE
                        buttonDeleteDialogCancel.setOnClickListener {
                            includeSymptomListEditDeleteDialog.root.visibility = View.GONE
                        }
                        buttonDeleteDialogDelete.setOnClickListener {
                            deleteCheckSymptom()
                        }
                    }
                }
            }

            recyclerViewSymptomListEdit.run {
                adapter = SymptomListEditRecyclerViewAdapter(symptomViewModel)
                layoutManager = LinearLayoutManager(context)
            }
        }
        return fragmentSymptomListEditBinding.root
    }

    private fun deleteCheckSymptom() {
        val deleteArr =
            symptomViewModel.symptomIdList.value!!.toIntArray()
        symptomViewModel.deleteSymptom(deleteArr)
    }
}
