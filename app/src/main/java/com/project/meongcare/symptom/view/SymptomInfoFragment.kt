package com.project.meongcare.symptom.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.project.meongcare.MainActivity
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentSymptomInfoBinding
import com.project.meongcare.symptom.model.entities.Symptom
import com.project.meongcare.symptom.model.entities.SymptomType
import com.project.meongcare.symptom.viewmodel.SymptomViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class SymptomInfoFragment : Fragment() {
    lateinit var fragmentSymptomInfoBinding: FragmentSymptomInfoBinding
    lateinit var mainActivity: MainActivity
    lateinit var symptomViewModel: SymptomViewModel
    lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentSymptomInfoBinding = FragmentSymptomInfoBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        symptomViewModel = mainActivity.symptomViewModel

        mainActivity.detachBottomNav()
        navController = findNavController()

        fragmentSymptomInfoBinding.run {
            toolbarSymptominfo.setNavigationOnClickListener {
                navController.navigate(R.id.action_symptomInfo_to_symptom)
            }
            val symptomData = symptomViewModel.infoSymptomData.value
            textViewSymptominfoDate.text = converToDateToMonthDate(symptomData!!.dateTime)
            textViewSymptominfoTime.text = converToDateToTime(symptomData.dateTime)
            includeItemSymptomInfo.run {
                imageViewItemSymptomAdd.setImageResource(getSymptomImg(symptomData))
                textViewItemSymptomAdd.text = symptomData.note
            }
        }
        return fragmentSymptomInfoBinding.root
    }

    fun converToDateToMonthDate(localMili: String): String {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val dateTime = LocalDateTime.parse(localMili, inputFormatter)

        // LocalDateTime을 오전/오후 시간 형식으로 포맷
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일", Locale.getDefault())
        return dateTime.format(outputFormatter)
    }

    fun converToDateToTime(localMili: String): String {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val dateTime = LocalDateTime.parse(localMili, inputFormatter)

        // LocalDateTime을 오전/오후 시간 형식으로 포맷
        val outputFormatter = DateTimeFormatter.ofPattern("a h:mm", Locale.getDefault())
        return dateTime.format(outputFormatter)
    }

    fun getSymptomImg(symptomData: Symptom): Int {
        return when (symptomData.symptomString) {
            SymptomType.WEIGHT_LOSS.symptomName -> R.drawable.all_weighing_machine
            SymptomType.HIGH_FEVER.symptomName -> R.drawable.all_temperature_measurement
            SymptomType.COUGH.symptomName -> R.drawable.symptom_cough
            SymptomType.DIARRHEA.symptomName -> R.drawable.symptom_diarrhea
            SymptomType.LOSS_OF_APPETITE.symptomName -> R.drawable.symptom_loss_appetite
            SymptomType.ACTIVITY_DECREASE.symptomName -> R.drawable.symptom_amount_activity
            else -> R.drawable.symptom_stethoscope
        }
    }
}

