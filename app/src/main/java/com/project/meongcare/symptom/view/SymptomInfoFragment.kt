package com.project.meongcare.symptom.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project.meongcare.R
import com.project.meongcare.symptom.model.data.repository.SymptomRepository
import java.time.LocalDateTime

class SymptomInfoFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        SymptomRepository.searchByDogId(1, LocalDateTime.now()) { symptoms ->
            Log.d("증상", symptoms.toString())
        }
        return inflater.inflate(R.layout.fragment_symptom_info, container, false)
    }
}

