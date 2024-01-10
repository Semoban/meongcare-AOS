package com.project.meongcare.symptom.view

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.project.meongcare.MainActivity
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentSymptomSelectBinding
import com.project.meongcare.symptom.model.data.repository.SymptomRepository
import com.project.meongcare.symptom.viewmodel.SymptomViewModel
import com.project.meongcare.symptom.viewmodel.SymptomViewModelFactory

class SymptomSelectFragment : Fragment() {
    lateinit var fragmentSymptomSelectBinding: FragmentSymptomSelectBinding
    lateinit var mainActivity: MainActivity
    lateinit var symptomViewModel: SymptomViewModel
    private val symptomCheckImageViews = mutableListOf<ImageView>()
    lateinit var navController: NavController
    var bundle: Bundle? = null
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentSymptomSelectBinding = FragmentSymptomSelectBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        editor = sharedPreferences.edit()

        val factory = SymptomViewModelFactory(SymptomRepository())
        symptomViewModel = ViewModelProvider(this, factory)[SymptomViewModel::class.java]

        navController = findNavController()

        fragmentSymptomSelectBinding.run {
            addImgViews()
            symptomCheckImageViews.forEach { imageView ->
                imageView.setOnClickListener {
                    handleSymptomCheckClick(imageView)
                }
            }

            buttonSymptomSelectCustomCancel.setOnClickListener {
                navController.popBackStack()
            }

            buttonSymptomSelectCustomComplete.setOnClickListener {
                setAddItemCheck()
            }
        }
        return fragmentSymptomSelectBinding.root
    }

    private fun addImgViews() {
        fragmentSymptomSelectBinding.run {
            symptomCheckImageViews.add(imageViewSymptomSelectCheckWeight)
            symptomCheckImageViews.add(imageViewSymptomSelectCheckHighFever)
            symptomCheckImageViews.add(imageViewSymptomSelectCheckCough)
            symptomCheckImageViews.add(imageViewSymptomSelectCheckDiarrhea)
            symptomCheckImageViews.add(imageViewSymptomSelectCheckLossOfAppetite)
            symptomCheckImageViews.add(imageViewSymptomSelectCheckActivityDecrease)
        }
    }

    private fun handleSymptomCheckClick(clickedImageView: ImageView) {
        clickedImageView.isSelected = true
        symptomViewModel.selectCheckedImg.value = clickedImageView

        symptomCheckImageViews.filter { it != clickedImageView }.forEach {
            it.isSelected = false
        }
    }


    private fun setAddItemCheck() {
        getSymptomNameFromCheck(symptomViewModel.selectCheckedImg.value!!)
        navController.popBackStack()
    }

    fun getSymptomNameFromCheck(symptomImg: ImageView) {
        when (symptomImg.id) {
            R.id.imageView_symptomSelect_check_weight -> {
                editor.putInt("symptomItemImgId", R.drawable.all_weighing_machine)
                editor.putString(
                    "symptomItemTitle",
                    mainActivity.findViewById<TextView>(R.id.textView_symptomSelect_weight_title).text.toString()
                )
                editor.apply()
            }

            R.id.imageView_symptomSelect_check_highFever -> {
                editor.putInt("symptomItemImgId", R.drawable.all_temperature_measurement)
                editor.putString(
                    "symptomItemTitle",
                    mainActivity.findViewById<TextView>(R.id.textView_symptomSelect_highFever_title).text.toString()
                )
                editor.apply()
            }

            R.id.imageView_symptomSelect_check_cough -> {
                editor.putInt("symptomItemImgId", R.drawable.symptom_cough)
                editor.putString(
                    "symptomItemTitle",
                    mainActivity.findViewById<TextView>(R.id.textView_symptomSelect_cough_title).text.toString()
                )
                editor.apply()
            }

            R.id.imageView_symptomSelect_check_diarrhea -> {
                editor.putInt("symptomItemImgId", R.drawable.symptom_diarrhea)
                editor.putString(
                    "symptomItemTitle",
                    mainActivity.findViewById<TextView>(R.id.textView_symptomSelect_diarrhea_title).text.toString()
                )
                editor.apply()
            }

            R.id.imageView_symptomSelect_check_lossOfAppetite -> {
                editor.putInt("symptomItemImgId", R.drawable.symptom_loss_appetite)
                editor.putString(
                    "symptomItemTitle",
                    mainActivity.findViewById<TextView>(R.id.textView_symptomSelect_lossOfAppetite_title).text.toString()
                )
                editor.apply()
            }

            R.id.imageView_symptomSelect_check_activityDecrease -> {
                editor.putInt("symptomItemImgId", R.drawable.symptom_amount_activity)
                editor.putString(
                    "symptomItemTitle",
                    mainActivity.findViewById<TextView>(R.id.textView_symptomSelect_activityDecrease_title).text.toString()
                )
                editor.apply()
            }
        }
    }
}
