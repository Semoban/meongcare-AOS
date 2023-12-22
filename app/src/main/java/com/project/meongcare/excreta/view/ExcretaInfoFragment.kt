package com.project.meongcare.excreta.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentExcretaInfoBinding

class ExcretaInfoFragment : Fragment() {
    private var _binding: FragmentExcretaInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentExcretaInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
    }

    private fun initToolbar() {
        binding.toolbarExcretainfo.apply {
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_info_edit ->
                        findNavController().navigate(R.id.action_excretaInfoFragment_to_excretaAddFragment)
                    R.id.menu_info_delete ->
                        Log.d("대소변 정보 삭제", "대소변 정보 삭제 다이얼로그")
                }
                false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
