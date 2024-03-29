package com.project.meongcare.symptom.view

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.project.meongcare.MainActivity
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentSymptomInfoBinding
import com.project.meongcare.snackbar.view.CustomSnackBar
import com.project.meongcare.symptom.model.data.repository.SymptomRepository
import com.project.meongcare.symptom.model.entities.Symptom
import com.project.meongcare.symptom.utils.SymptomUtils.Companion.convertDateToMonthDate
import com.project.meongcare.symptom.utils.SymptomUtils.Companion.convertDateToTime
import com.project.meongcare.symptom.utils.SymptomUtils.Companion.getSymptomImg
import com.project.meongcare.symptom.viewmodel.SymptomViewModel
import com.project.meongcare.symptom.viewmodel.SymptomViewModelFactory
import com.project.meongcare.toolbar.viewmodel.ToolbarViewModel

class SymptomInfoFragment : Fragment() {
    lateinit var fragmentSymptomInfoBinding: FragmentSymptomInfoBinding
    lateinit var mainActivity: MainActivity
    lateinit var symptomViewModel: SymptomViewModel
    lateinit var toolbarViewModel: ToolbarViewModel
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentSymptomInfoBinding = FragmentSymptomInfoBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        toolbarViewModel = mainActivity.toolbarViewModel
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        editor = sharedPreferences.edit()

        val factory = SymptomViewModelFactory(SymptomRepository())
        symptomViewModel = ViewModelProvider(this, factory)[SymptomViewModel::class.java]

        symptomViewModel.run {
            deleteSymptomCode.observe(viewLifecycleOwner) {
                if (it == 200) {
                    showSuccessSnackbar()
                    findNavController().popBackStack()
                } else {
                    showFailSnackbar()
                }
            }

            dogName.observe(viewLifecycleOwner) {
                fragmentSymptomInfoBinding.toolbarSymptominfo.title = "${it}님의 이상증상"
            }
        }
        fragmentSymptomInfoBinding.run {
            val symptomData = arguments?.getParcelable<Symptom>("symptomData")
            toolbarSymptominfo.run {
                setNavigationOnClickListener {
                    findNavController().popBackStack()
                }

                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.menu_info_delete -> {
                            includeSymptomDeleteDialog.root.visibility = View.VISIBLE
                            includeSymptomDeleteDialog.run {
                                buttonDeleteDialogCancel.setOnClickListener {
                                    includeSymptomDeleteDialog.root.visibility = View.GONE
                                }
                                buttonDeleteDialogDelete.setOnClickListener {
                                    val symptomIdsArray = intArrayOf(symptomData!!.symptomId)
                                    symptomViewModel.deleteSymptom(symptomIdsArray)
                                }
                            }
                        }

                        R.id.menu_info_edit -> {
                            editor.remove("symptomItemTitle")
                            editor.remove("symptomItemImgID")
                            editor.apply()
                            val bundle = Bundle()
                            bundle.putParcelable("symptomData", symptomData)
                            findNavController().navigate(R.id.action_symptomInfo_to_symptomEdit, bundle)
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
}

