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
import com.project.meongcare.symptom.model.entities.ToEditSymptom
import com.project.meongcare.symptom.utils.SymptomUtils.Companion.convertDateToSimpleTime
import com.project.meongcare.symptom.utils.SymptomUtils.Companion.convertDateToTime
import com.project.meongcare.symptom.utils.SymptomUtils.Companion.convertSimpleDateToMonthDate
import com.project.meongcare.symptom.utils.SymptomUtils.Companion.getSymptomImg
import com.project.meongcare.symptom.utils.SymptomUtils.Companion.getSymptomName
import com.project.meongcare.symptom.utils.SymptomUtils.Companion.showCalendarBottomSheet
import com.project.meongcare.symptom.view.bottomSheet.SymptomBottomSheetDialogFragment
import com.project.meongcare.symptom.viewmodel.SymptomViewModel
import com.project.meongcare.symptom.viewmodel.SymptomViewModelFactory
import java.time.LocalDate
import java.util.Locale

class SymptomEditFragment : Fragment(), SymptomBottomSheetDialogFragment.OnDateSelectedListener {
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

        initSymptomEdit()
        restoreSelectedItemFromPreferences()

        symptomViewModel.patchSymptomIsSuccess.observe(viewLifecycleOwner) {
            checkSuccessToEdit(it)
        }

        val timeText = convertDateToTime(symptomViewModel.infoSymptomData.value!!.dateTime)

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SemobanTheme {
                    val dogName by symptomViewModel.dogName.observeAsState()
                    val dateText by symptomViewModel.symptomDateText.observeAsState()
                    val itemImgRes by symptomViewModel.symptomItemImgId.observeAsState(R.drawable.symptom_etc_record)
                    val itemTitle by symptomViewModel.symptomItemTitle.observeAsState()

                    SymptomEditScreen(
                        dogName = dogName,
                        dateText = dateText,
                        timeText = timeText,
                        selectedItemImgRes = itemImgRes,
                        selectedItemTitle = itemTitle,
                        initialHour = symptomViewModel.symptomTimeHour,
                        initialMinute = symptomViewModel.symptomTimeMinute,
                        onDateClick = {
                            showCalendarBottomSheet(parentFragmentManager, this@SymptomEditFragment)
                        },
                        onSelectSymptomClick = {
                            findNavController().navigate(R.id.action_symptomEdit_to_symptomSelect)
                        },
                        onTimeChanged = { hour, minute ->
                            symptomViewModel.symptomTimeHour = hour
                            symptomViewModel.symptomTimeMinute = minute
                        },
                        onCustomItemEntered = ::setItemCustom,
                        onCancel = { findNavController().popBackStack() },
                        onComplete = ::editSymptom,
                    )
                }
            }
        }
    }

    private fun initSymptomEdit() {
        if (symptomViewModel.infoSymptomData.value == null) {
            symptomViewModel.infoSymptomData.value = arguments?.getParcelable<Symptom>("symptomData")
            symptomViewModel.symptomDateText.value =
                convertSimpleDateToMonthDate(symptomViewModel.infoSymptomData.value!!.dateTime)
            symptomViewModel.symptomItemImgId.value =
                getSymptomImg(symptomViewModel.infoSymptomData.value!!)
            symptomViewModel.symptomItemTitle.value = symptomViewModel.infoSymptomData.value!!.note
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

    private fun setItemCustom(customTitle: String) {
        editor.putInt("symptomItemImgId", R.drawable.symptom_etc_record)
        editor.putString("symptomItemTitle", customTitle)
        editor.apply()
        symptomViewModel.symptomItemImgId.value = R.drawable.symptom_etc_record
        symptomViewModel.symptomItemTitle.value = customTitle
    }

    private fun editSymptom(
        usePicker: Boolean,
        hour: Int,
        minute: Int,
    ) {
        val dateText = symptomViewModel.symptomDateText.value
        val dateTimeString =
            if (usePicker) {
                "${dateText}T${String.format(Locale.getDefault(), "%02d:%02d", hour, minute)}:00"
            } else {
                "${dateText}T${convertDateToSimpleTime(symptomViewModel.infoSymptomData.value!!.dateTime)}"
            }
        val toEditSymptom =
            ToEditSymptom(
                symptomViewModel.infoSymptomData.value!!.symptomId,
                dateTimeString,
                getSymptomName(symptomViewModel.symptomItemImgId.value!!),
                symptomViewModel.symptomItemTitle.value!!,
            )
        symptomViewModel.patchSymptom(toEditSymptom)
    }

    private fun checkSuccessToEdit(isSuccess: Boolean?) {
        if (isSuccess == true) {
            showSuccessSnackbar()
            findNavController().popBackStack(R.id.symptomFragment, false)
        } else if (isSuccess == false) {
            showFailSnackbar()
        }
    }

    private fun showSuccessSnackbar() {
        CustomSnackBar.make(
            activity?.findViewById(android.R.id.content)!!,
            R.drawable.snackbar_success_16dp,
            "수정이 완료되었습니다",
        ).show()
    }

    private fun showFailSnackbar() {
        CustomSnackBar.make(
            activity?.findViewById(android.R.id.content)!!,
            R.drawable.snackbar_error_16dp,
            "수정에 실패하였습니다.\n잠시 후 다시 시도해주세요",
        ).show()
    }

    override fun onDateSelected(date: LocalDate) {
        symptomViewModel.updateSymptomDate(date, isEditSymptom)
    }

    override fun onDestroy() {
        super.onDestroy()
        editor.remove("symptomItemTitle")
        editor.remove("symptomItemImgID")
        editor.apply()
    }
}
