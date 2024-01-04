package com.project.meongcare.supplement.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.project.meongcare.MainActivity
import com.project.meongcare.databinding.FragmentSupplementAddBinding
import com.project.meongcare.supplement.view.SupplementUtils.Companion.showCycleBottomSheet
import com.project.meongcare.supplement.viewmodel.SupplementViewModel

class SupplementAddFragment : Fragment() {
    lateinit var fragmentSupplementAddBinding: FragmentSupplementAddBinding
    lateinit var mainActivity: MainActivity
    lateinit var supplementViewModel: SupplementViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        fragmentSupplementAddBinding = FragmentSupplementAddBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        supplementViewModel = ViewModelProvider(this)[SupplementViewModel::class.java]

        supplementViewModel.run {
            supplementCycle.observe(viewLifecycleOwner){
                fragmentSupplementAddBinding.textViewSupplementAddCycleCount.text = it.toString()
            }
        }

        fragmentSupplementAddBinding.run {
            imageViewSupplementAddCycleToBottomsheet.setOnClickListener {
                showCycleBottomSheet(parentFragmentManager,supplementViewModel)
            }
        }

        return fragmentSupplementAddBinding.root
    }
}
