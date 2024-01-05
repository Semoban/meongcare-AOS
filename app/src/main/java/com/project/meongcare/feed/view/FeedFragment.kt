package com.project.meongcare.feed.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentFeedBinding
import com.project.meongcare.databinding.LayoutFeedNutrientBinding
import com.project.meongcare.feed.viewmodel.FeedGetViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedFragment : Fragment() {
    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private val feedGetViewModel: FeedGetViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        updateViewVisibilityBasedOnFeedExist()
        initNutrientPieChart()
        initNutrientTable()
    }

    private fun updateViewVisibilityBasedOnFeedExist() {
        feedGetViewModel.getFeed()
        feedGetViewModel.feedGet.observe(viewLifecycleOwner) { response ->
            binding.apply {
                if (response.feedId == 0L) {
                    textviewFeedBrand.visibility = View.GONE
                    textviewFeedName.visibility = View.GONE
                    piechartFeedNutrient.visibility = View.GONE
                } else {
                    imageviewFeedBowlIllustration.visibility = View.GONE
                    buttonFeedInputGuide.visibility = View.GONE
                }
            }
        }
    }

    private fun initNutrientPieChart() {
        val nutrientRatio =
            listOf(
                PieEntry(25f),
                PieEntry(15f),
                PieEntry(35f),
                PieEntry(25f),
            )

        val pieColors =
            listOf(
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

    private fun initNutrientTable() {
        binding.run {
            initNutrientRow(
                includeFeedNutrientCrudeProtein,
                R.drawable.feed_rect_crude_protein_r5,
                "조단백",
                "25%",
            )
            initNutrientRow(
                includeFeedNutrientCrudeFat,
                R.drawable.feed_rect_crude_fat_r5,
                "조지방",
                "15%",
            )
            initNutrientRow(
                includeFeedNutrientCrudeAsh,
                R.drawable.feed_rect_crude_ash_r5,
                "조회분",
                "35%",
            )
            initNutrientRow(
                includeFeedNutrientMoisture,
                R.drawable.feed_rect_moisture_r5,
                "수분",
                "25%",
            )
        }
    }

    private fun initNutrientRow(
        nutrientRow: LayoutFeedNutrientBinding,
        nutrientColorLabel: Int,
        nutrientType: String,
        nutrientPercentage: String,
    ) {
        nutrientRow.apply {
            viewFeedNutrientColorLabel.setBackgroundResource(nutrientColorLabel)
            textviewFeedNutrientType.text = nutrientType
            textviewFeedNutrientPercentage.text = nutrientPercentage
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
