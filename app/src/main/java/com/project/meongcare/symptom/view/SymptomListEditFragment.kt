package com.project.meongcare.symptom.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.project.meongcare.MainActivity
import com.project.meongcare.databinding.FragmentSymptomListEditBinding
import com.project.meongcare.symptom.viewmodel.SymptomViewModel

class SymptomListEditFragment : Fragment() {
    lateinit var fragmentSymptomListEditBinding: FragmentSymptomListEditBinding
    lateinit var mainActivity: MainActivity
    lateinit var symptomViewModel: SymptomViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        fragmentSymptomListEditBinding = FragmentSymptomListEditBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        symptomViewModel = ViewModelProvider(this)[SymptomViewModel::class.java]
        fragmentSymptomListEditBinding.run {}
        return fragmentSymptomListEditBinding.root
    }
}
