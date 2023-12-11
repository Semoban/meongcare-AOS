package com.project.meongcare.symptom.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project.meongcare.MainActivity
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentSymptomSelectBinding

class SymptomSelectFragment : Fragment() {
    lateinit var fragmentSymptomSelectBinding: FragmentSymptomSelectBinding
    lateinit var mainActivity: MainActivity
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        fragmentSymptomSelectBinding = FragmentSymptomSelectBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        fragmentSymptomSelectBinding.run {
            toolbarSymptomSelect.run {
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.SYMPTOM_SELECT_FRAGMENT)
                }
            }
        }
        return fragmentSymptomSelectBinding.root
    }
}
