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
import com.project.meongcare.symptom.utils.SymptomUtils.Companion.getSymptomName
import com.project.meongcare.symptom.utils.SymptomUtils.Companion.showCalendarBottomSheet
import com.project.meongcare.symptom.view.bottomSheet.SymptomBottomSheetDialogFragment
import com.project.meongcare.symptom.viewmodel.SymptomViewModel
import com.project.meongcare.symptom.viewmodel.SymptomViewModelFactory
import java.time.LocalDate
import java.util.Locale

class SymptomAddFragment : Fragment(), SymptomBottomSheetDialogFragment.OnDateSelectedListener {
    lateinit var symptomViewModel: SymptomViewModel
    var isEditSymptom = false
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

        restoreSelectedItemFromPreferences()

        symptomViewModel.addSymptomCode.observe(viewLifecycleOwner) {
            if (it == 200) {
                showSuccessSnackbar()
                findNavController().popBackStack()
            } else {
                showFailSnackbar()
            }
        }

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SemobanTheme {
                    val dateText by symptomViewModel.symptomDateText.observeAsState()
                    val itemImgRes by symptomViewModel.symptomItemImgId.observeAsState(R.drawable.symptom_etc_record)
                    val itemTitle by symptomViewModel.symptomItemTitle.observeAsState()

                    SymptomAddScreen(
                        dateText = dateText,
                        selectedItemImgRes = itemImgRes,
                        selectedItemTitle = itemTitle,
                        initialHour = symptomViewModel.symptomTimeHour,
                        initialMinute = symptomViewModel.symptomTimeMinute,
                        onBack = { findNavController().popBackStack() },
                        onDateClick = {
                            showCalendarBottomSheet(parentFragmentManager, this@SymptomAddFragment)
                        },
                        onSelectSymptomClick = ::navigateToSymptomSelect,
                        onTimeChanged = { hour, minute ->
                            symptomViewModel.symptomTimeHour = hour
                            symptomViewModel.symptomTimeMinute = minute
                        },
                        onComplete = ::addSymptom,
                    )
                }
            }
        }
    }

    private fun restoreSelectedItemFromPreferences() {
        if (sharedPreferences.getInt("symptomItemImgId", 0) != 0 &&
            sharedPreferences.getString("symptomItemTitle", "") != ""
        ) {
            symptomViewModel.symptomItemImgId.value = sharedPreferences.getInt("symptomItemImgId", 0)
            symptomViewModel.symptomItemTitle.value = sharedPreferences.getString("symptomItemTitle", "")
        }
    }

    private fun navigateToSymptomSelect() {
        editor.putBoolean("isEditSymptom", isEditSymptom)
        editor.apply()
        findNavController().navigate(R.id.action_symptomAdd_to_symptomSelect)
    }

    private fun addSymptom(
        hour: Int,
        minute: Int,
    ) {
        val addItemName = getSymptomName(symptomViewModel.symptomItemImgId.value!!)
        val addItemTitle = symptomViewModel.symptomItemTitle.value!!
        val dateTimeString =
            "${symptomViewModel.symptomDateText.value}T${
                String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
            }:00"
        symptomViewModel.addSymptomData(addItemName, addItemTitle, dateTimeString)
    }

    private fun showSuccessSnackbar() {
        CustomSnackBar.make(
            activity?.findViewById(android.R.id.content)!!,
            R.drawable.snackbar_success_16dp,
            getString(R.string.symptom_add_success),
        ).show()
    }

    private fun showFailSnackbar() {
        CustomSnackBar.make(
            activity?.findViewById(android.R.id.content)!!,
            R.drawable.snackbar_error_16dp,
            getString(R.string.symptom_add_failure),
        ).show()
    }

    override fun onDateSelected(date: LocalDate) {
        symptomViewModel.updateSymptomDate(date, isEditSymptom)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        editor.remove("symptomItemTitle")
        editor.remove("symptomItemImgID")
        editor.apply()
    }
}
