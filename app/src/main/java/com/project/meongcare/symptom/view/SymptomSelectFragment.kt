package com.project.meongcare.symptom.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.project.meongcare.MainActivity
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentSymptomSelectBinding
import com.project.meongcare.symptom.viewmodel.SymptomViewModel

class SymptomSelectFragment : Fragment() {
    lateinit var fragmentSymptomSelectBinding: FragmentSymptomSelectBinding
    lateinit var mainActivity: MainActivity
    lateinit var symptomViewModel: SymptomViewModel
    private val symptomCheckImageViews = mutableListOf<ImageView>()
    lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentSymptomSelectBinding = FragmentSymptomSelectBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        symptomViewModel = mainActivity.symptomViewModel

        navController = findNavController()

        fragmentSymptomSelectBinding.run {
            symptomCheckImageViews.add(imageViewSymptomSelectCheckWeight)
            symptomCheckImageViews.add(imageViewSymptomSelectCheckHighFever)
            symptomCheckImageViews.add(imageViewSymptomSelectCheckCough)
            symptomCheckImageViews.add(imageViewSymptomSelectCheckDiarrhea)
            symptomCheckImageViews.add(imageViewSymptomSelectCheckLossOfAppetite)
            symptomCheckImageViews.add(imageViewSymptomSelectCheckActivityDecrease)

            symptomCheckImageViews.forEach { imageView ->
                imageView.setOnClickListener {
                    handleSymptomCheckClick(imageView)
                }
            }

            buttonSymptomSelectCustomCancel.setOnClickListener {
                navController.navigate(R.id.action_symptomSelect_to_symptomAdd)
            }

            buttonSymptomSelectCustomComplete.setOnClickListener {
                setAddItemToSymptomAdd()
            }
        }
        return fragmentSymptomSelectBinding.root
    }

    private fun handleSymptomCheckClick(clickedImageView: ImageView) {
        clickedImageView.setImageResource(R.drawable.all_check_20dp)
        symptomViewModel.selectCheckedImg.value = clickedImageView

        symptomCheckImageViews.filter { it != clickedImageView }.forEach {
            it.setImageResource(R.drawable.all_un_check_20dp)
        }
    }

    private fun setAddItemToSymptomAdd() {
        symptomViewModel.selectCheckedImg.value?.let { getSymptomNameFromCheck(it) }
        navController.navigate(R.id.action_symptomSelect_to_symptomAdd)
    }

    fun getSymptomNameFromCheck(symptomImg: ImageView) {
        when (symptomImg.id) {
            R.id.imageView_symptomSelect_check_weight -> {
                symptomViewModel.addSymptomItemImgId.value = R.drawable.all_weighing_machine
                symptomViewModel.addSymptomItemTitle.value =
                    mainActivity.findViewById<TextView>(R.id.textView_symptomSelect_weight_title).text.toString()
            }

            R.id.imageView_symptomSelect_check_highFever -> {
                symptomViewModel.addSymptomItemImgId.value = R.drawable.all_temperature_measurement
                symptomViewModel.addSymptomItemTitle.value =
                    mainActivity.findViewById<TextView>(R.id.textView_symptomSelect_highFever_title).text.toString()
            }

            R.id.imageView_symptomSelect_check_cough -> {
                symptomViewModel.addSymptomItemImgId.value = R.drawable.symptom_cough
                symptomViewModel.addSymptomItemTitle.value =
                    mainActivity.findViewById<TextView>(R.id.textView_symptomSelect_cough_title).text.toString()
            }

            R.id.imageView_symptomSelect_check_diarrhea -> {
                symptomViewModel.addSymptomItemImgId.value = R.drawable.symptom_diarrhea
                symptomViewModel.addSymptomItemTitle.value =
                    mainActivity.findViewById<TextView>(R.id.textView_symptomSelect_diarrhea_title).text.toString()
            }

            R.id.imageView_symptomSelect_check_lossOfAppetite -> {
                symptomViewModel.addSymptomItemImgId.value = R.drawable.symptom_loss_appetite
                symptomViewModel.addSymptomItemTitle.value =
                    mainActivity.findViewById<TextView>(R.id.textView_symptomSelect_lossOfAppetite_title).text.toString()
            }

            R.id.imageView_symptomSelect_check_activityDecrease -> {
                symptomViewModel.addSymptomItemImgId.value = R.drawable.symptom_amount_activity
                symptomViewModel.addSymptomItemTitle.value =
                    mainActivity.findViewById<TextView>(R.id.textView_symptomSelect_activityDecrease_title).text.toString()
            }
        }
    }
}
