package com.project.meongcare.symptom.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.project.meongcare.MainActivity
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentSymptomInfoBinding
import com.project.meongcare.symptom.model.data.repository.SymptomRepository
import com.project.meongcare.symptom.view.SymptomUtils.Companion.convertDateToMonthDate
import com.project.meongcare.symptom.view.SymptomUtils.Companion.convertDateToTime
import com.project.meongcare.symptom.view.SymptomUtils.Companion.getSymptomImg
import com.project.meongcare.symptom.viewmodel.SymptomViewModel
import com.project.meongcare.toolbar.viewmodel.ToolbarViewModel

class SymptomInfoFragment : Fragment() {
    lateinit var fragmentSymptomInfoBinding: FragmentSymptomInfoBinding
    lateinit var mainActivity: MainActivity
    lateinit var symptomViewModel: SymptomViewModel
    lateinit var toolbarViewModel: ToolbarViewModel
    lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentSymptomInfoBinding = FragmentSymptomInfoBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        symptomViewModel = mainActivity.symptomViewModel
        toolbarViewModel = mainActivity.toolbarViewModel

        mainActivity.detachBottomNav()
        navController = findNavController()

        fragmentSymptomInfoBinding.run {
            val symptomData = symptomViewModel.infoSymptomData.value
            toolbarSymptominfo.run {
                setNavigationOnClickListener {
                    navController.navigate(R.id.action_symptomInfo_to_symptom)
                }

                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.menu_info_delete -> {
                            includeSymptomDeleteDialog.root.visibility = View.VISIBLE
                            includeSymptomDeleteDialog.run {
                                buttonSymptomDeleteDialogCancel.setOnClickListener {
                                    includeSymptomDeleteDialog.root.visibility = View.GONE
                                }
                                buttonSymptomDeleteDialogDelete.setOnClickListener {
                                    navController.navigate(R.id.action_symptomInfo_to_symptom)
                                    val symptomIdsArray = arrayOf(symptomData!!.symptomId)
                                    Log.d("배열확인", symptomIdsArray[0].toString())
                                    deleteSymptomData(symptomIdsArray)
                                }
                            }
                        }

                        R.id.menu_info_edit -> {
                            false
                        }

                        else -> {
                            false
                        }
                    }
                    true
                }
            }

            textViewSymptominfoDate.text = convertDateToMonthDate(symptomData!!.dateTime)
            textViewSymptominfoTime.text = convertDateToTime(symptomData.dateTime)
            includeItemSymptomInfo.run {
                imageViewItemSymptomAdd.setImageResource(getSymptomImg(symptomData))
                textViewItemSymptomAdd.text = symptomData.note
            }
        }
        return fragmentSymptomInfoBinding.root
    }

    fun deleteSymptomData(symptomIds: Array<Int>) {
        SymptomRepository.deleteSymptom(symptomIds)
        symptomViewModel.updateSymptomList(1, toolbarViewModel.selectedDate.value!!)
    }
}

