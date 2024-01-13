package com.project.meongcare.symptom.view

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.project.meongcare.MainActivity
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentSymptomEditBinding
import com.project.meongcare.snackbar.view.CustomSnackBar
import com.project.meongcare.symptom.model.data.repository.SymptomRepository
import com.project.meongcare.symptom.model.entities.Symptom
import com.project.meongcare.symptom.model.entities.ToEditSymptom
import com.project.meongcare.symptom.utils.SymptomUtils.Companion.convertDateToTime
import com.project.meongcare.symptom.utils.SymptomUtils.Companion.convertSimpleDateToMonthDate
import com.project.meongcare.symptom.utils.SymptomUtils.Companion.getSymptomImg
import com.project.meongcare.symptom.utils.SymptomUtils.Companion.getSymptomName
import com.project.meongcare.symptom.utils.SymptomUtils.Companion.hideKeyboard
import com.project.meongcare.symptom.utils.SymptomUtils.Companion.showCalendarBottomSheet
import com.project.meongcare.symptom.viewmodel.SymptomViewModel
import com.project.meongcare.symptom.viewmodel.SymptomViewModelFactory
import com.project.meongcare.toolbar.viewmodel.ToolbarViewModel
import java.time.LocalDate

class SymptomEditFragment : Fragment(), SymptomBottomSheetDialogFragment.OnDateSelectedListener {
    lateinit var fragmentSymptomEditBinding: FragmentSymptomEditBinding
    lateinit var manActivity: MainActivity
    lateinit var toolbarViewModel: ToolbarViewModel
    lateinit var symptomViewModel: SymptomViewModel
    var isEditSymptom = false
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    override fun onCreateView(
        inflater: LayoutInflater,
        contaner: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentSymptomEditBinding = FragmentSymptomEditBinding.inflate(layoutInflater)
        manActivity = activity as MainActivity

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        editor = sharedPreferences.edit()

        toolbarViewModel = manActivity.toolbarViewModel

        val factory = SymptomViewModelFactory(SymptomRepository())
        symptomViewModel = ViewModelProvider(this, factory)[SymptomViewModel::class.java]

        initSymptomEdit()

        symptomViewModel.run {
            if (sharedPreferences.getInt("symptomItemImgId", 0) != 0 && sharedPreferences.getString(
                    "symptomItemTitle",
                    ""
                ) != ""
            ) {
                symptomItemImgId.value = sharedPreferences.getInt("symptomItemImgId", 0)
                symptomItemTitle.value = sharedPreferences.getString("symptomItemTitle", "")
            }
            symptomItemTitle.observe(viewLifecycleOwner) {
                getItemTitle(it)
            }

            symptomDateText.observe(viewLifecycleOwner) {
                setInputDate(it)
            }

            symptomItemVisibility.observe(viewLifecycleOwner) {
                setGetItem(it)
            }

            patchSymptomIsSuccess.observe(viewLifecycleOwner) {
                checkSuccessToEdit(it)
            }
        }

        fragmentSymptomEditBinding.run {

            buttonSymptomEditDate.setOnClickListener {
                showCalendarBottomSheet(parentFragmentManager, this@SymptomEditFragment)
            }

            buttonSymptomEditTime.setOnClickListener {
                it.visibility = View.GONE
                timepickerSymptomEdit.visibility = View.VISIBLE
            }

            timepickerSymptomEdit.setOnTimeChangedListener { timePicker, hour, minute ->
                symptomViewModel.symptomTimeHour = hour
                symptomViewModel.symptomTimeMinute = minute
            }

            buttonSymptomEditSelectSymptom.setOnClickListener {
                findNavController().navigate(R.id.action_symptomEdit_to_symptomSelect)
            }

            checkNullAndSetEssential()
            getItemCustom()

            buttonSymptomEditCancel.setOnClickListener {
                findNavController().popBackStack()
            }
            buttonSymptomEditComplete.setOnClickListener {
                addSymptom()
            }

        }
        return fragmentSymptomEditBinding.root
    }

    private fun initSymptomEdit() {
        symptomViewModel.infoSymptomData.value = arguments?.getParcelable<Symptom>("symptomData")
        symptomViewModel.symptomDateText.value =
            convertSimpleDateToMonthDate(symptomViewModel.infoSymptomData.value!!.dateTime)
        symptomViewModel.symptomItemImgId.value =
            getSymptomImg(symptomViewModel.infoSymptomData.value!!)
        symptomViewModel.symptomItemTitle.value = symptomViewModel.infoSymptomData.value!!.note
        fragmentSymptomEditBinding.textViewSymptomEditTime.text =
            convertDateToTime(symptomViewModel.infoSymptomData.value!!.dateTime)
    }

    private fun getItemCustom() {
        val editTextItemCustom = fragmentSymptomEditBinding.editTextSymptomEditCustom
        editTextItemCustom.setOnEditorActionListener { t, a, k ->
            if ((a == EditorInfo.IME_ACTION_DONE || (k != null && k.action == KeyEvent.ACTION_DOWN && k.keyCode == KeyEvent.KEYCODE_ENTER)) && t.text.trim()
                    .isNotEmpty()
            ) {
                fragmentSymptomEditBinding.layoutItemSymptomEdit.visibility = View.VISIBLE
                setItemCustom()
                setClearEditTextSymptomEditCustom()
                hideKeyboard(editTextItemCustom)
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun setItemCustom() {
        symptomViewModel.run {
            editor.putInt("symptomItemImgId", R.drawable.symptom_etc_record)
            editor.putString(
                "symptomItemTitle",
                fragmentSymptomEditBinding.editTextSymptomEditCustom.text.toString().trim(),
            )
            editor.apply()
            symptomViewModel.symptomItemImgId.value = R.drawable.symptom_etc_record
            symptomViewModel.symptomItemTitle.value =
                fragmentSymptomEditBinding.editTextSymptomEditCustom.text.toString().trim()
        }
    }

    private fun getItemTitle(it: String?) {
        if (it != null) {
            fragmentSymptomEditBinding.run {
                includeItemSymptomEdit.run {
                    imageViewItemSymptomAdd.setImageResource(symptomViewModel.symptomItemImgId.value!!)
                    textViewItemSymptomAdd.text = it.trim()
                }
            }
            symptomViewModel.symptomItemVisibility.value = View.VISIBLE
        }
    }

    private fun checkSuccessToEdit(it: Boolean) {
        if (it) {
            symptomViewModel.clearLiveData()
            CustomSnackBar(
                requireView(),
                R.drawable.snackbar_success_16dp,
                "수정이 완료되었습니다."
            )
            findNavController().popBackStack(R.id.symptomFragment, false)
        } else {
            CustomSnackBar(
                requireView(),
                R.drawable.snackbar_error_16dp,
                "서버 에러입니다.\n잠시 후에 다시 시도해주세요."
            )
        }
    }

    private fun setGetItem(visible: Int?) {
        if (visible == View.VISIBLE) {
            fragmentSymptomEditBinding.textViewSymptomEditSelectSymptom.run {
                text = "증상을 선택해주세요"
                setTextColor(ContextCompat.getColor(manActivity, R.color.gray4))
            }
            fragmentSymptomEditBinding.buttonSymptomEditSelectSymptom.setBackgroundResource(R.drawable.all_rect_gray1_r5)
        }
    }

    private fun setInputDate(dateText: String?) {
        if (dateText != null) {
            fragmentSymptomEditBinding.textViewSymptomEditDate.run {
                text = symptomViewModel.symptomDateText.value
                setTextColor(ContextCompat.getColor(manActivity, R.color.black))
                setTextAppearance(R.style.Typography_Body1_Medium)
            }
            fragmentSymptomEditBinding.buttonSymptomEditDate.setBackgroundResource(R.drawable.all_rect_gray1_r5)
        }
    }

    private fun setClearEditTextSymptomEditCustom() {
        fragmentSymptomEditBinding.editTextSymptomEditCustom.text.clear()
        fragmentSymptomEditBinding.editTextSymptomEditCustom.clearFocus()
    }

    private fun addSymptom() {
        val dateTimeString = getDateTimeString()
        val addItemName = getEditItemName()
        val addItemTitle = symptomViewModel.symptomItemTitle.value
        checkNullAndEditData(dateTimeString, addItemName, addItemTitle)
    }

    private fun getEditItemName() =
        if (fragmentSymptomEditBinding.layoutItemSymptomEdit.visibility == View.VISIBLE) {
            getSymptomName(symptomViewModel.symptomItemImgId.value!!)
        } else {
            checkNullInput(
                fragmentSymptomEditBinding.textViewSymptomEditSelectSymptom,
                fragmentSymptomEditBinding.buttonSymptomEditSelectSymptom
            )
            null
        }

    private fun getDateTimeString() = if (!symptomViewModel.symptomDateText.value.isNullOrEmpty()) {
        "${symptomViewModel.symptomDateText.value}T${
            String.format(
                "%02d:%02d",
                fragmentSymptomEditBinding.timepickerSymptomEdit.hour,
                fragmentSymptomEditBinding.timepickerSymptomEdit.minute,
            )
        }:00"
    } else {
        checkNullInput(
            fragmentSymptomEditBinding.textViewSymptomEditDate,
            fragmentSymptomEditBinding.buttonSymptomEditDate
        )
        null
    }

    private fun checkNullEditItem() {
        fragmentSymptomEditBinding.run {
            if (!symptomViewModel.symptomItemTitle.value.isNullOrEmpty()) {
                layoutItemSymptomEdit.visibility = View.VISIBLE
                includeItemSymptomEdit.run {
                    imageViewItemSymptomAdd.setImageResource(symptomViewModel.symptomItemImgId.value!!)
                    textViewItemSymptomAdd.text = symptomViewModel.symptomItemTitle.value!!.trim()
                }
            } else {
                layoutItemSymptomEdit.visibility = View.GONE
            }
        }
    }

    private fun checkNullTimePickerValue() {
        if (symptomViewModel.symptomTimeHour != null && symptomViewModel.symptomTimeMinute != null) {
            fragmentSymptomEditBinding.timepickerSymptomEdit.run {
                hour = symptomViewModel.symptomTimeHour!!
                minute = symptomViewModel.symptomTimeMinute!!
            }
        }
    }

    private fun checkNullDate() {
        if (!symptomViewModel.symptomDateText.value.isNullOrEmpty()) {
            fragmentSymptomEditBinding.textViewSymptomEditDate.run {
                text = symptomViewModel.symptomDateText.value
                setTextColor(ContextCompat.getColor(manActivity, R.color.black))
                setTextAppearance(R.style.Typography_Body1_Medium)
            }
        }
    }

    private fun checkNullInput(
        textView: TextView,
        layout: LinearLayout,
    ) {
        textView.run {
            text = "필수 입력 값입니다."
            setTextAppearance(R.style.Typography_Body1_Regular)
            setTextColor(ContextCompat.getColor(manActivity, R.color.sub1))
        }
        layout.run {
            setBackgroundResource(R.drawable.all_rect_gray1_r5_outline_sub1)
        }
    }

    private fun checkNullAndSetEssential() {
        checkNullDate()
        checkNullTimePickerValue()
        checkNullEditItem()
    }

    private fun checkNullAndEditData(
        dateTimeString: String?,
        addItemName: String?,
        addItemTitle: String?,
    ) {
        if (dateTimeString != null && addItemName != null && addItemTitle != null) {
            val toEditSymptom = ToEditSymptom(
                symptomViewModel.infoSymptomData.value!!.symptomId,
                dateTimeString,
                addItemName,
                addItemTitle,
            )
            Log.d("이상증상 편집 확인", toEditSymptom.toString())
            symptomViewModel.patchSymptom(toEditSymptom)
        }
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

