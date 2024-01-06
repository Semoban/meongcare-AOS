package com.project.meongcare.supplement.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.project.meongcare.MainActivity
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentSupplementRoutineEditBinding
import com.project.meongcare.supplement.model.entities.Supplement

class SupplementRoutineEditFragment : Fragment() {
    lateinit var fragmentSupplementRoutineEditBinding: FragmentSupplementRoutineEditBinding
    lateinit var mainActivity: MainActivity
    lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        mainActivity = activity as MainActivity
        fragmentSupplementRoutineEditBinding = FragmentSupplementRoutineEditBinding.inflate(layoutInflater)

        navController = findNavController()

        val supplementsList = arguments?.getParcelableArrayList<Supplement>("supplements_key")
        Log.d("루틴 편집",supplementsList.toString())

        fragmentSupplementRoutineEditBinding.run {
            toolbarSupplementRoutineEdit.setNavigationOnClickListener { navController.popBackStack() }

        }
        return fragmentSupplementRoutineEditBinding.root
    }
}
