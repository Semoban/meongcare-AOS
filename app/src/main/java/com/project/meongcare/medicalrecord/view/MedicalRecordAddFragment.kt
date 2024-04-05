package com.project.meongcare.medicalRecord.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.project.meongcare.databinding.FragmentMedicalRecordAddBinding
import com.project.meongcare.medicalRecord.view.bottomSheet.MedicalRecordDateBottomSheetDialogFragment
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MedicalRecordAddFragment : Fragment(), MedicalRecordDateBottomSheetDialogFragment.OnDateSelectedListener  {
    private lateinit var binding: FragmentMedicalRecordAddBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMedicalRecordAddBinding.inflate(inflater)

        binding.run {
            textviewMedicalrecordaddSelectDate.setOnClickListener {
                showCalendarBottomSheet(parentFragmentManager, this@MedicalRecordAddFragment)
            }
        }


        return binding.root
    }


    fun showCalendarBottomSheet(
        parentFragmentManager: FragmentManager,
        onDateSelectedListener: MedicalRecordDateBottomSheetDialogFragment.OnDateSelectedListener,
    ) {
        val bottomSheetDialogFragment = MedicalRecordDateBottomSheetDialogFragment()
        bottomSheetDialogFragment.setOnDateSelecetedListener(onDateSelectedListener)
        bottomSheetDialogFragment.show(parentFragmentManager, "MedicalRecordDateBottomSheetDialogFragment")
    }

    override fun onDateSelected(date: LocalDate) {
        val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
//        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'00:00:00")
        binding.textviewMedicalrecordaddSelectDate.text = date.format(formatter)
    }

}
