package com.project.meongcare.excreta.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.project.meongcare.MainActivity
import com.project.meongcare.databinding.FragmentExcretaAddBinding

class ExcretaAddFragment : Fragment() {
    private var _binding: FragmentExcretaAddBinding? = null
    private val binding get() = _binding!!

    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentExcretaAddBinding.inflate(inflater, container, false)
        mainActivity = activity as MainActivity
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.detachBottomNav()
        initToolbar()
        initPhotoAttachModalBottomSheet()
        initCalendarModalBottomSheet()
        toggleExcretaCheckboxesOnClick()
        initExcretaAddCompletionButton()
    }

    private fun initToolbar() {
        binding.toolbarExcretaadd.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initPhotoAttachModalBottomSheet() {
        val photoAttachModalBottomSheet = PhotoAttachModalBottomSheet()
        binding.cardviewExcretaaddImage.setOnClickListener {
            photoAttachModalBottomSheet.show(
                mainActivity.supportFragmentManager, PhotoAttachModalBottomSheet.TAG
            )
        }
    }

    private fun initCalendarModalBottomSheet() {
        val calendarModalBottomSheet = CalendarModalBottomSheet()
        binding.textviewExcretaaddDate.setOnClickListener {
            calendarModalBottomSheet.show(
                mainActivity.supportFragmentManager, CalendarModalBottomSheet.TAG
            )
        }
    }

    private fun toggleExcretaCheckboxesOnClick() {
        binding.run {
            checkboxExcretaaddFeces.setOnClickListener {
                checkboxExcretaaddUrine.isChecked = !checkboxExcretaaddFeces.isChecked
            }
            checkboxExcretaaddUrine.setOnClickListener {
                checkboxExcretaaddFeces.isChecked = !checkboxExcretaaddUrine.isChecked
            }
        }
    }

    private fun initExcretaAddCompletionButton() {
        binding.buttonExcretaaddCompletion.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
