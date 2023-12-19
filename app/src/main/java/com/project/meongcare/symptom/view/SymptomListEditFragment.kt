package com.project.meongcare.symptom.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.project.meongcare.MainActivity
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentSymptomListEditBinding
import com.project.meongcare.symptom.viewmodel.SymptomViewModel

class SymptomListEditFragment : Fragment() {
    lateinit var fragmentSymptomListEditBinding: FragmentSymptomListEditBinding
    lateinit var mainActivity: MainActivity
    lateinit var symptomViewModel: SymptomViewModel
    lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        fragmentSymptomListEditBinding = FragmentSymptomListEditBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        symptomViewModel = mainActivity.symptomViewModel
        navController = findNavController()

        // TODO : 강아지 이름 연결 필요
        val dogName = "김대박"

        fragmentSymptomListEditBinding.run {
            toolbarSymptomListEdit.run {
                title = "${dogName}님의 이상증상"
                setNavigationOnClickListener{
                    navController.navigate(R.id.action_symptomListEdit_to_symptom)
                }
            }
        }
        return fragmentSymptomListEditBinding.root
    }
}
