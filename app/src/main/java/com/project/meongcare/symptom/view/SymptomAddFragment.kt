package com.project.meongcare.symptom.view

import android.os.Bundle
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
import androidx.navigation.fragment.findNavController
import com.project.meongcare.MainActivity
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentSymptomAddBinding
import com.project.meongcare.symptom.model.data.repository.SymptomRepository
import com.project.meongcare.symptom.model.entities.ToAddSymptom
import com.project.meongcare.symptom.view.SymptomUtils.Companion.getSymptomName
import com.project.meongcare.symptom.view.SymptomUtils.Companion.hideKeyboard
import com.project.meongcare.symptom.view.SymptomUtils.Companion.showCalendarBottomSheet
import com.project.meongcare.symptom.viewmodel.SymptomViewModel

class SymptomAddFragment : Fragment() {
    lateinit var fragmentSymptomAddBinding: FragmentSymptomAddBinding
    lateinit var mainActivity: MainActivity
    lateinit var symptomViewModel: SymptomViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentSymptomAddBinding = FragmentSymptomAddBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        val navController = findNavController()

        symptomViewModel = mainActivity.symptomViewModel

        symptomViewModel.run {
            symptomItemTitle.observe(viewLifecycleOwner) { title ->
                if (title != null) {
                    fragmentSymptomAddBinding.run {
                        includeItemSymptomAdd.run {
                            imageViewItemSymptomAdd.setImageResource(symptomItemImgId.value!!)
                            textViewItemSymptomAdd.text = title.trim()
                        }
                    }
                    symptomViewModel.symptomItemVisibility.value = View.VISIBLE
                }
            }

            symptomDateText.observe(viewLifecycleOwner) {
                if (it != null) {
                    fragmentSymptomAddBinding.run {
                        textViewSymptomAddDate.run {
                            text = symptomViewModel.symptomDateText.value
                            setTextColor(ContextCompat.getColor(mainActivity, R.color.black))
                            setTextAppearance(R.style.Typography_Body1_Medium)
                        }
                        buttonSymptomAddDate.setBackgroundResource(R.drawable.all_rect_gray1_r5)
                    }
                }
            }

            symptomItemVisibility.observe(viewLifecycleOwner) {
                if (it == View.VISIBLE) {
                    fragmentSymptomAddBinding.run {
                        textViewSymptomAddSelectSymptom.run {
                            text = "증상을 선택해주세요"
                            setTextColor(ContextCompat.getColor(mainActivity, R.color.gray4))
                        }

                        buttonSymptomAddSelectSymptom.setBackgroundResource(R.drawable.all_rect_gray1_r5)
                    }
                }
            }
        }

        fragmentSymptomAddBinding.run {
            toolbarSymptomAdd.run {
                setNavigationOnClickListener {
                    navController.navigate(R.id.action_symptomAdd_to_symptom)
                }
            }

            buttonSymptomAddDate.setOnClickListener {
                showCalendarBottomSheet(parentFragmentManager, symptomViewModel)
            }

            isNullDate()

            timepickerSymptomAdd.run {
                setOnTimeChangedListener { timePicker, hour, minute ->
                    symptomViewModel.symptomTimeHour = hour
                    symptomViewModel.symptomTimeMinute = minute
                }
            }

            isNullTimePickerValue()

            buttonSymptomAddSelectSymptom.setOnClickListener {
                navController.navigate(R.id.action_symptomAdd_to_symptomSelect)
            }

            isNullAddItem()

            editTextSymptomAddCustom.setOnEditorActionListener { _, actionId, keyEvent ->
                if (
                    (
                        actionId == EditorInfo.IME_ACTION_DONE ||
                            (
                                keyEvent != null && keyEvent.action == KeyEvent.ACTION_DOWN &&
                                    keyEvent.keyCode == KeyEvent.KEYCODE_ENTER
                            )
                    ) && editTextSymptomAddCustom.text.trim().isNotEmpty()
                ) {
                    layoutSymptomAddList.visibility = View.VISIBLE
                    includeItemSymptomAdd.run {
                        symptomViewModel.run {
                            symptomItemImgId.value = R.drawable.symptom_stethoscope
                            symptomItemTitle.value = editTextSymptomAddCustom.text.toString().trim()
                        }
                    }
                    editTextSymptomAddCustom.text.clear()
                    editTextSymptomAddCustom.clearFocus()
                    hideKeyboard(editTextSymptomAddCustom)
                    return@setOnEditorActionListener true
                }
                false
            }

            buttonSymptomAddToSymptom.setOnClickListener {
                val dateTimeString =
                    if (!symptomViewModel.symptomDateText.value.isNullOrEmpty()) {
                        "${symptomViewModel.symptomDateText.value}T${
                            String.format(
                                "%02d:%02d",
                                timepickerSymptomAdd.hour,
                                timepickerSymptomAdd.minute,
                            )
                        }:00"
                    } else {
                        isNullInput(textViewSymptomAddDate, buttonSymptomAddDate)
                        null
                    }

                val addItemName =
                    if (layoutSymptomAddList.visibility == View.VISIBLE) {
                        getSymptomName(symptomViewModel.symptomItemImgId.value!!)
                    } else {
                        isNullInput(textViewSymptomAddSelectSymptom, buttonSymptomAddSelectSymptom)
                        null
                    }

                val addItemTitle = symptomViewModel.symptomItemTitle.value

                if (dateTimeString != null && addItemName != null && addItemTitle != null) {
                    Log.d("Symptom문제", dateTimeString)
                    val toAddSymptom = ToAddSymptom(1, addItemName, addItemTitle, dateTimeString)
                    Log.d("Symptom문제", toAddSymptom.toString())
                    SymptomRepository.addSymptom(toAddSymptom)
                    symptomViewModel.clearLiveData()
                    navController.navigate(R.id.action_symptomAdd_to_symptom)
                }
            }
        }
        return fragmentSymptomAddBinding.root
    }

    private fun FragmentSymptomAddBinding.isNullAddItem() {
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

    private fun FragmentSymptomAddBinding.isNullTimePickerValue() {
        if (symptomViewModel.symptomTimeHour != null && symptomViewModel.symptomTimeMinute != null) {
            timepickerSymptomAdd.run {
                hour = symptomViewModel.symptomTimeHour!!
                minute = symptomViewModel.symptomTimeMinute!!
            }
        }
    }

    private fun FragmentSymptomAddBinding.isNullDate() {
        if (!symptomViewModel.symptomDateText.value.isNullOrEmpty()) {
            textViewSymptomAddDate.run {
                text = symptomViewModel.symptomDateText.value
                setTextColor(ContextCompat.getColor(mainActivity, R.color.black))
                setTextAppearance(R.style.Typography_Body1_Medium)
            }
        }
    }

    private fun isNullInput(
        textView: TextView,
        layout: LinearLayout,
    ) {
        textView.run {
            text = "필수 입력 값입니다."
            setTextAppearance(R.style.Typography_Body1_Regular)
            setTextColor(ContextCompat.getColor(mainActivity, R.color.sub1))
        }
        layout.run {
            setBackgroundResource(R.drawable.all_rect_gray1_r5_outline_sub1)
        }
    }
}

