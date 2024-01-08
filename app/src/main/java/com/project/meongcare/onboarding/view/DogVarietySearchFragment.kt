package com.project.meongcare.onboarding.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.project.meongcare.databinding.FragmentDogVarietySearchBinding

class DogVarietySearchFragment : Fragment() {
    private lateinit var binding: FragmentDogVarietySearchBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentDogVarietySearchBinding.inflate(inflater)

        binding.imageViewBack.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }
}
