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
import com.project.meongcare.databinding.FragmentSymptomAddBinding
import com.project.meongcare.snackbar.view.CustomSnackBar
import com.project.meongcare.symptom.model.data.repository.SymptomRepository
import com.project.meongcare.symptom.utils.SymptomUtils.Companion.getSymptomName
import com.project.meongcare.symptom.utils.SymptomUtils.Companion.hideKeyboard
import com.project.meongcare.symptom.utils.SymptomUtils.Companion.showCalendarBottomSheet
import com.project.meongcare.symptom.view.bottomSheet.SymptomBottomSheetDialogFragment
import com.project.meongcare.symptom.viewmodel.SymptomViewModel
import com.project.meongcare.symptom.viewmodel.SymptomViewModelFactory
import com.project.meongcare.toolbar.viewmodel.ToolbarViewModel
import java.time.LocalDate

class SymptomAddFragment : Fragment(), SymptomBottomSheetDialogFragment.OnDateSelectedListener {
    lateinit var fragmentSymptomAddBinding: FragmentSymptomAddBinding
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
        fragmentSymptomAddBinding = FragmentSymptomAddBinding.inflate(layoutInflater)
        manActivity = activity as MainActivity

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        editor = sharedPreferences.edit()

        toolbarViewModel = manActivity.toolbarViewModel

        val factory = SymptomViewModelFactory(SymptomRepository())
        symptomViewModel = ViewModelProvider(this, factory)[SymptomViewModel::class.java]

        symptomViewModel.run {
            if (sharedPreferences.getInt("symptomItemImgId", 0) != 0 && sharedPreferences.getString(
                    "symptomItemTitle",
                    "",
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

            checkAddSuccess()
        }

        fragmentSymptomAddBinding.run {
            toolbarSymptomAdd.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            buttonSymptomAddDate.setOnClickListener {
                showCalendarBottomSheet(parentFragmentManager, this@SymptomAddFragment)
            }

            timepickerSymptomAdd.setOnTimeChangedListener { timePicker, hour, minute ->
                symptomViewModel.symptomTimeHour = hour
                symptomViewModel.symptomTimeMinute = minute
            }

            buttonSymptomAddSelectSymptom.setOnClickListener {
                editor.putBoolean("isEditSymptom", isEditSymptom)
                editor.apply()
                findNavController().navigate(R.id.action_symptomAdd_to_symptomSelect)
            }

            checkNullAndSetEssential()

            buttonSymptomAddToSymptom.setOnClickListener {
                addSymptom()
            }
        }
        return fragmentSymptomAddBinding.root
    }

    private fun checkAddSuccess() {
        symptomViewModel.addSymptomCode.observe(viewLifecycleOwner) {
            if (it == 200) {
                Log.d("이상증상 추가 코드1", it.toString())
                showSuccessSnackbar()
                findNavController().popBackStack()
            } else {
                Log.d("이상증상 추가 코드2", it.toString())
                showFailSnackbar()
            }
        }
    }

    private fun getItemTitle(it: String?) {
        if (it != null) {
            fragmentSymptomAddBinding.run {
                includeItemSymptomAdd.run {
                    imageViewItemSymptomAdd.setImageResource(symptomViewModel.symptomItemImgId.value!!)
                    textViewItemSymptomAdd.text = it.trim()
                }
            }
            symptomViewModel.symptomItemVisibility.value = View.VISIBLE
        }
    }

    private fun setGetItem(visible: Int?) {
        if (visible == View.VISIBLE) {
            fragmentSymptomAddBinding.textViewSymptomAddSelectSymptom.run {
                text = "증상을 선택해주세요"
                setTextColor(ContextCompat.getColor(manActivity, R.color.gray4))
            }
            fragmentSymptomAddBinding.buttonSymptomAddSelectSymptom.setBackgroundResource(R.drawable.all_rect_gray1_r5)
        }
    }

    private fun setInputDate(dateText: String?) {
        if (dateText != null) {
            fragmentSymptomAddBinding.textViewSymptomAddDate.run {
                text = symptomViewModel.symptomDateText.value
                setTextColor(ContextCompat.getColor(manActivity, R.color.black))
                setTextAppearance(R.style.Typography_Body1_Medium)
            }
            fragmentSymptomAddBinding.buttonSymptomAddDate.setBackgroundResource(R.drawable.all_rect_gray1_r5)
        }
    }


    private fun addSymptom() {
        val dateTimeString = getDateTimeString()
        val addItemName = getAddItemName()
        val addItemTitle = symptomViewModel.symptomItemTitle.value
        checkNullAndAddData(addItemName, addItemTitle, dateTimeString)
    }

    private fun getAddItemName() =
        if (fragmentSymptomAddBinding.layoutSymptomAddList.visibility == View.VISIBLE) {
            getSymptomName(symptomViewModel.symptomItemImgId.value!!)
        } else {
            checkNullInput(
                fragmentSymptomAddBinding.textViewSymptomAddSelectSymptom,
                fragmentSymptomAddBinding.buttonSymptomAddSelectSymptom,
            )
            null
        }

    private fun getDateTimeString() =
        if (!symptomViewModel.symptomDateText.value.isNullOrEmpty()) {
            "${symptomViewModel.symptomDateText.value}T${
                String.format(
                    "%02d:%02d",
                    fragmentSymptomAddBinding.timepickerSymptomAdd.hour,
                    fragmentSymptomAddBinding.timepickerSymptomAdd.minute,
                )
            }:00"
        } else {
            checkNullInput(
                fragmentSymptomAddBinding.textViewSymptomAddDate,
                fragmentSymptomAddBinding.buttonSymptomAddDate,
            )
            null
        }

    private fun checkNullAddItem() {
        fragmentSymptomAddBinding.run {
            if (!symptomViewModel.symptomItemTitle.value.isNullOrEmpty()) {
                layoutSymptomAddList.visibility = View.VISIBLE
                includeItemSymptomAdd.run {
                    imageViewItemSymptomAdd.setImageResource(symptomViewModel.symptomItemImgId.value!!)
                    textViewItemSymptomAdd.text = symptomViewModel.symptomItemTitle.value!!.trim()
                }
            } else {
                layoutSymptomAddList.visibility = View.GONE
            }
        }
    }

    private fun checkNullTimePickerValue() {
        if (symptomViewModel.symptomTimeHour != null && symptomViewModel.symptomTimeMinute != null) {
            fragmentSymptomAddBinding.timepickerSymptomAdd.run {
                hour = symptomViewModel.symptomTimeHour!!
                minute = symptomViewModel.symptomTimeMinute!!
            }
        }
    }

    private fun checkNullDate() {
        if (!symptomViewModel.symptomDateText.value.isNullOrEmpty()) {
            fragmentSymptomAddBinding.textViewSymptomAddDate.run {
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
        checkNullAddItem()
    }

    private fun checkNullAndAddData(
        addItemName: String?,
        addItemTitle: String?,
        dateTimeString: String?,
    ) {
        if (dateTimeString != null && addItemName != null && addItemTitle != null) {
            symptomViewModel.addSymptomData(addItemName, addItemTitle, dateTimeString)
        }
    }

    private fun showSuccessSnackbar() {
        CustomSnackBar.make(
            activity?.findViewById(android.R.id.content)!!,
            R.drawable.snackbar_success_16dp,
            "추가가 완료되었습니다",
        ).show()
    }

    private fun showFailSnackbar() {
        CustomSnackBar.make(
            activity?.findViewById(android.R.id.content)!!,
            R.drawable.snackbar_error_16dp,
            "추가에 실패하였습니다.\n잠시 후 다시 시도해주세요",
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

