package com.project.meongcare.excreta.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentExcretaInfoBinding
import com.project.meongcare.excreta.model.entities.Excreta
import com.project.meongcare.excreta.model.entities.ExcretaDetailGetResponse
import com.project.meongcare.excreta.viewmodel.ExcretaDetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExcretaInfoFragment : Fragment() {
    private var _binding: FragmentExcretaInfoBinding? = null
    private val binding get() = _binding!!

    private val excretaDetailViewModel: ExcretaDetailViewModel by viewModels()
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
        fetchExcretaInfo()
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

    private fun fetchExcretaInfo() {
        excretaDetailViewModel.apply {
            getExcretaDetail(getExcretaId()!!)
            excretaDetailGet.observe(viewLifecycleOwner) { response ->
                initExcretaImage(response.excretaImageURL)
                initDate(response)
                initExcretaCheckBox(response.excretaType)
                binding.textviewExcretainfoTime.text = getExcretaTime()
            }
        }
    }

    private fun initExcretaImage(excretaImageURL: String) {
        binding.apply {
            if (excretaImageURL.isNotEmpty()) {
                imageviewExcretainfoFecesIllustration.visibility = View.INVISIBLE
                Glide.with(this@ExcretaInfoFragment)
                    .load(excretaImageURL)
                    .into(imageviewExcretainfoPicture)
            }
        }
    }

    private fun initDate(response: ExcretaDetailGetResponse) {
        val year = response.dateTime.substring(YEAR_START..YEAR_END)
        val month = response.dateTime.substring(MONTH_START..MONTH_END)
        val day = response.dateTime.substring(DAY_START..DAY_END)
        val date = "${year}년 ${month}월 ${day}일"
        binding.textviewExcretainfoDate.text = date
    }

    private fun initExcretaCheckBox(excretaType: String) {
        binding.apply {
            if (excretaType == Excreta.FECES.toString()) {
                checkboxExcretainfoFeces.isChecked = true
            } else {
                checkboxExcretainfoUrine.isChecked = true
            }
        }
    }


    private fun getExcretaId() = arguments?.getLong("excretaId")

    private fun getExcretaTime() = arguments?.getString("excretaTime")

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val YEAR_START = 0
        const val YEAR_END = 3
        const val MONTH_START = 5
        const val MONTH_END = 6
        const val DAY_START = 8
        const val DAY_END = 9
    }
}
