package com.project.meongcare

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project.meongcare.databinding.FragmentPetAddEditBinding

class PetAddFragment : Fragment() {
    private lateinit var binding: FragmentPetAddEditBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPetAddEditBinding.inflate(inflater)
        return binding.root
    }
}