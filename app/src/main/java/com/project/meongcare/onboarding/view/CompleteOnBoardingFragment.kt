package com.project.meongcare.onboarding.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.project.meongcare.databinding.FragmentCompleteOnBoardingBinding
import com.project.meongcare.R

class CompleteOnBoardingFragment : Fragment() {
    private lateinit var binding: FragmentCompleteOnBoardingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentCompleteOnBoardingBinding.inflate(inflater)

        binding.buttonStart.setOnClickListener {
            findNavController().navigate(R.id.action_completeOnBoardingFragment_to_homeFragment)
        }

        return binding.root
    }
}
