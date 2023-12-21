package com.project.meongcare.excreta.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentExcretaBinding
import com.project.meongcare.excreta.model.entities.ExcretaGetResponse

class ExcretaFragment : Fragment() {
    private var _binding: FragmentExcretaBinding? = null
    private val binding get() = _binding!!

    private lateinit var excretaAdapter: ExcretaAdapter
    private val excreta = listOf(
        ExcretaGetResponse(1L, "오전 10:00", "대변"),
        ExcretaGetResponse(2L, "오전 11:00", "소변"),
        ExcretaGetResponse(3L, "오전 10:00", "대변"),
        ExcretaGetResponse(4L, "오전 11:00", "소변"),
        ExcretaGetResponse(5L, "오전 10:00", "대변"),
        ExcretaGetResponse(6L, "오전 11:00", "소변"),
        ExcretaGetResponse(7L, "오전 10:00", "대변"),
        ExcretaGetResponse(8L, "오전 11:00", "소변"),
        ExcretaGetResponse(9L, "오전 10:00", "대변"),
        ExcretaGetResponse(10L, "오전 11:00", "소변"),
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentExcretaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        excretaAdapter = ExcretaAdapter()
        initExcretaAddButton()
        initExcretaEditButton()
        initExcretaRecordRecyclerView()
    }

    private fun initExcretaAddButton() {
        binding.textviewExcretaAddbutton.setOnClickListener {
            findNavController().navigate(R.id.action_excretaFragment_to_excretaAddFragment)
        }
    }

    private fun initExcretaEditButton() {
        binding.textviewExcretaEditbutton.setOnClickListener {
            findNavController().navigate(R.id.action_excretaFragment_to_excretaRecordEditFragment)
        }
    }

    private fun initExcretaRecordRecyclerView() {
        binding.recyclerviewExcretaRecord.run {
            adapter = excretaAdapter
            layoutManager = LinearLayoutManager(context)
        }
        excretaAdapter.submitList(excreta)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
