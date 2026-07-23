package com.project.meongcare.symptom.view

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.project.meongcare.R
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.snackbar.view.CustomSnackBar
import com.project.meongcare.symptom.model.data.repository.SymptomRepository
import com.project.meongcare.symptom.model.entities.Symptom
import com.project.meongcare.symptom.utils.SymptomUtils.Companion.convertDateToMonthDate
import com.project.meongcare.symptom.utils.SymptomUtils.Companion.convertDateToTime
import com.project.meongcare.symptom.utils.SymptomUtils.Companion.getSymptomImg
import com.project.meongcare.symptom.utils.SymptomUtils.Companion.getSymptomTitle
import com.project.meongcare.symptom.viewmodel.SymptomViewModel
import com.project.meongcare.symptom.viewmodel.SymptomViewModelFactory

class SymptomInfoFragment : Fragment() {
    lateinit var symptomViewModel: SymptomViewModel
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        editor = sharedPreferences.edit()

        val factory = SymptomViewModelFactory(SymptomRepository())
        symptomViewModel = ViewModelProvider(this, factory)[SymptomViewModel::class.java]

        symptomViewModel.deleteSymptomCode.observe(viewLifecycleOwner) {
            if (it == 200) {
                showSuccessSnackbar()
                findNavController().popBackStack()
            } else {
                showFailSnackbar()
            }
        }

        val symptomData = arguments?.getParcelable<Symptom>("symptomData")!!

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SemobanTheme {
                    val dogName by symptomViewModel.dogName.observeAsState()

                    SymptomInfoScreen(
                        dogName = dogName,
                        dateText = convertDateToMonthDate(symptomData.dateTime),
                        timeText = convertDateToTime(symptomData.dateTime),
                        symptomImgRes = getSymptomImg(symptomData),
                        symptomTitle = getSymptomTitle(requireContext(), symptomData.symptomString, symptomData.note),
                        onBack = { findNavController().popBackStack() },
                        onEdit = { navigateToSymptomEdit(symptomData) },
                        onDelete = {
                            symptomViewModel.deleteSymptom(intArrayOf(symptomData.symptomId))
                        },
                    )
                }
            }
        }
    }

    private fun navigateToSymptomEdit(symptomData: Symptom) {
        editor.remove("symptomItemTitle")
        editor.remove("symptomItemImgID")
        editor.apply()
        val bundle = Bundle()
        bundle.putParcelable("symptomData", symptomData)
        findNavController().navigate(R.id.action_symptomInfo_to_symptomEdit, bundle)
    }

    private fun showSuccessSnackbar() {
        CustomSnackBar.make(
            requireView(),
            R.drawable.snackbar_success_16dp,
            getString(R.string.symptom_delete_success),
        ).show()
    }

    private fun showFailSnackbar() {
        CustomSnackBar.make(
            requireView(),
            R.drawable.snackbar_error_16dp,
            getString(R.string.symptom_delete_failure),
        ).show()
    }
}
