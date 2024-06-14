package com.project.meongcare.recordShare.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.project.meongcare.databinding.FragmentRecordShareBinding

class RecordShareFragment : Fragment() {
    private lateinit var binding: FragmentRecordShareBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentRecordShareBinding.inflate(inflater)
        binding.imagebuttonRecordshareBack.setOnClickListener {
            findNavController().popBackStack()
        }
        return binding.root
    }
}
