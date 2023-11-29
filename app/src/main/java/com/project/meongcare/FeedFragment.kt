package com.project.meongcare

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.project.meongcare.databinding.FragmentFeedBinding

class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initNutrientPieChart()
    }

    private fun initNutrientPieChart() {
        val nutrientRatio = listOf(
            PieEntry(25f),
            PieEntry(15f),
            PieEntry(35f),
            PieEntry(25f),
        )

        val pieColors = listOf(
            ContextCompat.getColor(requireContext(), R.color.main3),
            ContextCompat.getColor(requireContext(), R.color.sub7),
            ContextCompat.getColor(requireContext(), R.color.sub6),
            ContextCompat.getColor(requireContext(), R.color.sub8),
        )

        val dataSet = PieDataSet(nutrientRatio, "")
        dataSet.colors = pieColors
        dataSet.setDrawValues(false)

        binding.piechartFeedNutrient.apply {
            data = PieData(dataSet)
            description.isEnabled = false
            legend.isEnabled = false
            isRotationEnabled = true
            holeRadius = 60f
            setTouchEnabled(false)
            animateY(1200, Easing.EaseInOutCubic)
            animate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
