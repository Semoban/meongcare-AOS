package com.project.meongcare.supplement.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project.meongcare.MainActivity
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentSupplementAddBinding

class SupplementAddFragment : Fragment() {
    lateinit var fragmentSupplementAddBinding: FragmentSupplementAddBinding
    lateinit var mainActivity: MainActivity
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        fragmentSupplementAddBinding = FragmentSupplementAddBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        return fragmentSupplementAddBinding.root
    }
}
