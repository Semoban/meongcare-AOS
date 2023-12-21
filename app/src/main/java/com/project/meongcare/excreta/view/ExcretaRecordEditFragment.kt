package com.project.meongcare.excreta.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.project.meongcare.MainActivity
import com.project.meongcare.databinding.FragmentExcretaRecordEditBinding
import com.project.meongcare.excreta.model.entities.ExcretaRecord

class ExcretaRecordEditFragment : Fragment() {
    private var _binding: FragmentExcretaRecordEditBinding? = null
    private val binding get() = _binding!!

    lateinit var mainActivity: MainActivity
    private val excreta = listOf(
        ExcretaRecord(1L, "오전 10:00", "대변"),
        ExcretaRecord(2L, "오전 11:00", "소변"),
        ExcretaRecord(3L, "오전 10:00", "대변"),
        ExcretaRecord(4L, "오전 11:00", "소변"),
        ExcretaRecord(5L, "오전 10:00", "대변"),
        ExcretaRecord(6L, "오전 11:00", "소변"),
        ExcretaRecord(7L, "오전 10:00", "대변"),
        ExcretaRecord(8L, "오전 11:00", "소변"),
        ExcretaRecord(9L, "오전 10:00", "대변"),
        ExcretaRecord(10L, "오전 11:00", "소변"),
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentExcretaRecordEditBinding.inflate(inflater, container, false)
        mainActivity = activity as MainActivity
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.detachBottomNav()
        initToolbar()
    }

    private fun initToolbar() {
        binding.toolbarExcretarecordedit.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
