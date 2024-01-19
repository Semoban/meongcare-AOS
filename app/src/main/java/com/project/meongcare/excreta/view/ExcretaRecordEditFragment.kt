package com.project.meongcare.excreta.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.meongcare.databinding.FragmentExcretaRecordEditBinding
import com.project.meongcare.excreta.model.data.local.ExcretaItemCheckedListener
import com.project.meongcare.excreta.utils.SUCCESS
import com.project.meongcare.excreta.viewmodel.ExcretaDeleteViewModel
import com.project.meongcare.excreta.viewmodel.ExcretaRecordViewModel
import com.project.meongcare.feed.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime

@AndroidEntryPoint
class ExcretaRecordEditFragment : Fragment() {
    private var _binding: FragmentExcretaRecordEditBinding? = null
    val binding get() = _binding!!

    private val excretaRecordViewModel: ExcretaRecordViewModel by viewModels()
    private val excretaDeleteViewModel: ExcretaDeleteViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    private lateinit var excretaAdapter: ExcretaRecordEditAdapter
    private var accessToken = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentExcretaRecordEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel.fetchAccessToken()
        userViewModel.accessToken.observe(viewLifecycleOwner) { response ->
            accessToken = response
        }
        excretaAdapter =
            ExcretaRecordEditAdapter(
                object : ExcretaItemCheckedListener {
                    override fun onItemChecked(checkedIds: IntArray) {
                        deleteExcretaRecords(checkedIds)
                    }
                })

        initToolbar()
        initSelectAllCheckBox()
        initExcretaRecordEditRecyclerView()
        fetchExcretaRecord()
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
    }

    private fun fetchExcretaRecord() {
        val dateTime = LocalDateTime.now().toString().slice(ExcretaFragment.DATE_TIME_START..ExcretaFragment.DATE_TIME_END)

        excretaRecordViewModel.apply {
            getExcretaRecord(accessToken, dateTime)
            excretaRecordGet.observe(viewLifecycleOwner) { response ->
                excretaAdapter.submitList(response.excretaRecords)
            }
        }
    }

    private fun initCancelButton() {
        binding.buttonExcretarecordeditCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun deleteExcretaRecords(checkIds: IntArray) {
        binding.buttonExcretarecordeditDelete.setOnClickListener {
            excretaDeleteViewModel.deleteExcreta(accessToken, checkIds)
            excretaDeleteViewModel.apply {
                excretaDeleted.observe(viewLifecycleOwner) { response ->
                    if (response == SUCCESS) {
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
