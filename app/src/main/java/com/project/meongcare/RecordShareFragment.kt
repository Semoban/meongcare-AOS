package com.project.meongcare

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project.meongcare.databinding.FragmentRecordShareBinding

class RecordShareFragment : Fragment() {

    private lateinit var binding: FragmentRecordShareBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecordShareBinding.inflate(inflater)
        return binding.root
    }
}