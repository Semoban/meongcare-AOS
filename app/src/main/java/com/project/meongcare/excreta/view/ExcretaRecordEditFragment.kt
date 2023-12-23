package com.project.meongcare.excreta.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.meongcare.databinding.FragmentExcretaRecordEditBinding
import com.project.meongcare.excreta.model.entities.ExcretaRecord

class ExcretaRecordEditFragment : Fragment() {
    private var _binding: FragmentExcretaRecordEditBinding? = null
    private val binding get() = _binding!!

    private lateinit var excretaAdapter: ExcretaRecordEditAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentExcretaRecordEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        excretaAdapter = ExcretaRecordEditAdapter()
        initToolbar()
        initSelectAllCheckBox()
        initExcretaRecordEditRecyclerView()
        initCancelButton()
    }

    private fun initToolbar() {
        binding.toolbarExcretarecordedit.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initSelectAllCheckBox() {
        binding.apply {
            checkboxExcretarecordeditSelectall.setOnClickListener {
                excretaAdapter.currentList.map {
                    it.isChecked = checkboxExcretarecordeditSelectall.isChecked
                }
                // notifyDataSetChanged 사용하지 않는 방법으로 수정 필요
                excretaAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun initExcretaRecordEditRecyclerView() {
        binding.recyclerviewExcretarecordeditRecord.run {
            adapter = excretaAdapter
            layoutManager = LinearLayoutManager(context)
        }
        excretaAdapter.submitList(excreta)
    }

    private fun initCancelButton() {
        binding.buttonExcretarecordeditCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
