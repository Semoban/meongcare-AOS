package com.project.meongcare.medicalRecord.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.meongcare.R
import com.project.meongcare.medicalRecord.model.entities.MedicalRecordGet

class MedicalRecordInfoEditFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (arguments != null) {
            val medicalRecord = arguments?.getParcelable<MedicalRecordGet>("medicalRecord")
            if (medicalRecord != null) {
                Log.d("진료기록 수정 화면", medicalRecord.toString())
            }
        }
        return inflater.inflate(R.layout.fragment_medical_record_info_edit, container, false)
    }

}