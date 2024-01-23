package com.project.meongcare.weight.view

import android.content.Context
import android.view.LayoutInflater
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.project.meongcare.databinding.WeightMarkerBinding

class WeightCustomMarker(context: Context, layoutResource: Int) :
    MarkerView(context, layoutResource) {
    private val binding: WeightMarkerBinding =
        WeightMarkerBinding.inflate(LayoutInflater.from(context))

    override fun refreshContent(
        e: Entry,
        highlight: Highlight,
    ) {
        binding.textviewWeightMarker.text = "${e.y}"
    }

    override fun getOffsetForDrawingAtPoint(
        xpos: Float,
        ypos: Float,
    ): MPPointF {
        return MPPointF(-width / 2f, -height - 10f)
    }
}
