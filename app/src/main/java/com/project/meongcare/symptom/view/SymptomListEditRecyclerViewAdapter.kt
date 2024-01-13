package com.project.meongcare.symptom.view

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.meongcare.databinding.ItemSymptomListEditBinding
import com.project.meongcare.symptom.utils.SymptomUtils
import com.project.meongcare.symptom.viewmodel.SymptomViewModel

class SymptomListEditRecyclerViewAdapter(private val symptomViewModel: SymptomViewModel) :
    RecyclerView.Adapter<SymptomListEditRecyclerViewAdapter.SymptomListEditViewHolder>() {
    inner class SymptomListEditViewHolder(itemSymptomListEditBinding: ItemSymptomListEditBinding) :
        RecyclerView.ViewHolder(itemSymptomListEditBinding.root) {
        val itemSymptomName: TextView
        val itemSymptomTime: TextView
        val itemSymptomImg: ImageView
        val itemSymptomCheck: ImageView

        init {
            itemSymptomName = itemSymptomListEditBinding.textViewItemSymptomListEdit
            itemSymptomTime = itemSymptomListEditBinding.textViewItemSymptomListEditTime
            itemSymptomImg = itemSymptomListEditBinding.imageViewItemSymptomListEdit
            itemSymptomCheck = itemSymptomListEditBinding.imageViewItemSymptomListEditCheck

            Log.d("증상 리스트3", symptomViewModel.symptomList.value!!.toString())
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SymptomListEditViewHolder {
        val itemSymptomListEditBinding =
            ItemSymptomListEditBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val allViewHolder = SymptomListEditViewHolder(itemSymptomListEditBinding)

        itemSymptomListEditBinding.root.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )

        return allViewHolder
    }

    override fun getItemCount(): Int {
        return symptomViewModel.symptomList.value!!.size
    }

    override fun onBindViewHolder(
        holder: SymptomListEditViewHolder,
        position: Int,
    ) {
        holder.itemSymptomName.text = symptomViewModel.symptomList.value!![position].note
        holder.itemSymptomTime.text =
            SymptomUtils.convertDateToTime(symptomViewModel.symptomList.value!![position].dateTime)
        holder.itemSymptomImg.setImageResource(SymptomUtils.getSymptomImg(symptomViewModel.symptomList.value!![position]))

        val symptomsId =
            symptomViewModel.symptomList.value!![position].symptomId

        holder.itemSymptomCheck.isSelected =
            symptomViewModel.symptomIdList.value!!.contains(symptomsId)

        holder.itemView.setOnClickListener {
            holder.itemSymptomCheck.isSelected = !holder.itemSymptomCheck.isSelected
            val temp2 = symptomViewModel.symptomIdList.value!!
            if (temp2.contains(symptomsId)) {
                temp2.remove(symptomsId)
                symptomViewModel.symptomIdList.value = temp2
            } else {
                temp2.add(symptomsId)
                symptomViewModel.symptomIdList.value = temp2
            }
        }
    }

}