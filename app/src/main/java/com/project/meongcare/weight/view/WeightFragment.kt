package com.project.meongcare.weight.view

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentWeightBinding
import java.text.DecimalFormat

class WeightFragment : Fragment() {
    private var _binding: FragmentWeightBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentWeightBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        showWeightEditDialog()
        initWeeklyRecordChart()
        initMonthlyRecordChart()
    }

    private fun showWeightEditDialog() {
        binding.run {
            textviewWeightEditbutton.setOnClickListener {
                layoutWeightEdit.root.visibility = View.VISIBLE
            }
        }
    }

    private fun initWeeklyRecordChart() {
        val weightWeeklyData =
            listOf(
                Entry(1f, 4.67f),
                Entry(2f, 5f),
                Entry(3f, 4.8f),
                Entry(4f, 4.7f),
            )

        val weightWeeklyDataSet = LineDataSet(weightWeeklyData, "")

        val lineColor = resources.getColor(R.color.main4, null)

        val typo = Typeface.createFromAsset(requireContext().assets, "pretendard_medium.otf")

        weightWeeklyDataSet.apply {
            valueTextSize = 12F
            valueTypeface = typo
            valueTextColor = resources.getColor(R.color.gray3, null)
            valueFormatter = WeightDataFormatter()
            color = lineColor
            setCircleColor(lineColor)
            setDrawCircleHole(false)
            setDrawFilled(true)
            fillDrawable =
                ContextCompat.getDrawable(requireContext(), R.drawable.weight_weekly_chart_gradient)
        }

        binding.linechartWeightWeeklyrecord.apply {
            data = LineData(weightWeeklyDataSet)

            xAxis.apply {
                granularity = 1F
                textSize = 14F
                typeface = typo
                textColor = resources.getColor(R.color.gray4, null)
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = WeekFormatter()
                spaceMin = 0.2F
                spaceMax = 0.2F
                setDrawGridLines(false)
                axisLineColor = resources.getColor(R.color.white, null)
            }

            axisLeft.apply {
                setDrawLabels(false)
                setDrawAxisLine(false)
                gridColor = resources.getColor(R.color.gray2, null)
                gridLineWidth = 1F
            }

            axisRight.apply {
                setDrawLabels(false)
                setDrawAxisLine(false)
                setDrawGridLines(false)
            }

            description.isEnabled = false
            legend.xOffset = -50f
            setTouchEnabled(true)
            setScaleEnabled(false)
            setPinchZoom(false)
            setDrawMarkers(true)
            marker = WeightCustomMarker(context, R.layout.weight_marker)
            animateY(1200)
        }
    }

    private fun initMonthlyRecordChart() {
        val weightMonthlyData =
            listOf(
                BarEntry(10F, 4.23F),
                BarEntry(11F, 4.46F),
            )

        val weightMonthlyDataSet = BarDataSet(weightMonthlyData, "")

        val barColors =
            listOf(
                resources.getColor(R.color.gray3, null),
                resources.getColor(R.color.main4, null),
            )

        val weightLabelColors =
            listOf(
                resources.getColor(R.color.gray5, null),
                resources.getColor(R.color.main4, null),
            )

        val typo = Typeface.createFromAsset(requireContext().assets, "pretendard_medium.otf")

        weightMonthlyDataSet.colors = barColors

        binding.barchartWeightMonthlyrecord.apply {
            data = BarData(weightMonthlyDataSet)

            data.apply {
                barWidth = 0.5F
                setValueTextSize(14F)
                setValueTypeface(typo)
                setValueTextColors(weightLabelColors)
                setValueFormatter(WeightDataFormatter())
            }

            xAxis.apply {
                granularity = 1F

                position = XAxis.XAxisPosition.BOTTOM
                textSize = 14F
                typeface = typo
                valueFormatter = MonthFormatter()
                setDrawGridLines(false)
            }

            axisLeft.apply {
                axisMinimum = 0F
                granularity = 1F
                setDrawLabels(false)
                setDrawAxisLine(false)
                gridColor = resources.getColor(R.color.gray2, null)
                gridLineWidth = 1F
            }

            axisRight.apply {
                setDrawLabels(false)
                setDrawAxisLine(false)
                setDrawGridLines(false)
            }

            description.isEnabled = false
            legend.xOffset = -50f
            setTouchEnabled(false)
            setScaleEnabled(false)
            setPinchZoom(false)
            animateY(1000)
        }
    }

    class WeekFormatter : ValueFormatter() {
        private val format = DecimalFormat("#주")

        override fun getFormattedValue(value: Float): String {
            return format.format(value)
        }
    }

    class MonthFormatter : ValueFormatter() {
        private val format = DecimalFormat("#월")

        override fun getFormattedValue(value: Float): String {
            return format.format(value)
        }
    }

    class WeightDataFormatter : ValueFormatter() {
        private val format = DecimalFormat("0.00kg")

        override fun getFormattedValue(value: Float): String {
            return format.format(value)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
