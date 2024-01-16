package com.project.meongcare.supplement.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.meongcare.databinding.ItemSupplementAddTimeBinding
import com.project.meongcare.supplement.utils.SupplementUtils.Companion.convertDateToTime
import com.project.meongcare.supplement.viewmodel.SupplementViewModel

class SupplementInfoTimeRecyclerViewAdapter(private val supplementViewModel: SupplementViewModel) :
    RecyclerView.Adapter<SupplementInfoTimeRecyclerViewAdapter.SupplementInfoTimeViewHolder>() {
    val intakeList =
        supplementViewModel.supplementDetail.value!!.intakeInfos.sortedBy { it.intakeTime }

    inner class SupplementInfoTimeViewHolder(itemSupplementInfoTimeBinding: ItemSupplementAddTimeBinding) :
        RecyclerView.ViewHolder(itemSupplementInfoTimeBinding.root) {
        val itemSupplementInfoTimeTime: TextView
        val itemSupplementInfoTimeAmount: TextView

        init {
            itemSupplementInfoTimeTime =
                itemSupplementInfoTimeBinding.textViewItemSupplementAddTime
            itemSupplementInfoTimeAmount =
                itemSupplementInfoTimeBinding.textViewItemSupplementAddTimeAmount
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SupplementInfoTimeViewHolder {
        val itemSupplementInfoTimeBinding =
            ItemSupplementAddTimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val allViewHolder = SupplementInfoTimeViewHolder(itemSupplementInfoTimeBinding)

        itemSupplementInfoTimeBinding.root.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )

        return allViewHolder
    }

    override fun getItemCount(): Int {
        return intakeList.size
    }

    override fun onBindViewHolder(
        holder: SupplementInfoTimeViewHolder,
        position: Int,
    ) {
        val intakeCountString = intakeList[position].intakeCount
        val intakeUnitString = supplementViewModel.supplementDetail.value!!.intakeUnit
        holder.itemSupplementInfoTimeTime.text =
            convertDateToTime(intakeList[position].intakeTime)
        holder.itemSupplementInfoTimeAmount.text = "$intakeCountString$intakeUnitString"
    }
}
