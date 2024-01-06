package com.project.meongcare.Information.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentPetEditBinding

class PetEditFragment : Fragment() {
    private lateinit var binding: FragmentPetEditBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPetEditBinding.inflate(inflater)

        return binding.root
    }
}
