package com.project.meongcare.symptom.view// SymptomRecyclerViewAdapter.kt

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.meongcare.databinding.ItemSymptomBinding
import com.project.meongcare.symptom.utils.SymptomUtils.Companion.convertDateToTime
import com.project.meongcare.symptom.utils.SymptomUtils.Companion.getSymptomImg
import com.project.meongcare.symptom.viewmodel.SymptomViewModel
import java.util.*

class SymptomRecyclerViewAdapter(
    private val symptomViewModel: SymptomViewModel,
    private val onItemClick: (Int) -> Unit,
) : RecyclerView.Adapter<SymptomRecyclerViewAdapter.SymptomViewHolder>() {

    inner class SymptomViewHolder(itemSymptomBinding: ItemSymptomBinding) :
        RecyclerView.ViewHolder(itemSymptomBinding.root) {
        val itemSymptomName: TextView
        val itemSymptomTime: TextView
        val itemSymptomImg: ImageView

        init {
            itemSymptomName = itemSymptomBinding.textViewItemSymptom
            itemSymptomTime = itemSymptomBinding.textViewItemSymptomTime
            itemSymptomImg = itemSymptomBinding.imageViewItemSymptom
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SymptomViewHolder {
        val itemSymptomBinding = ItemSymptomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val allViewHolder = SymptomViewHolder(itemSymptomBinding)

        itemSymptomBinding.root.layoutParams =
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
        holder: SymptomViewHolder,
        position: Int,
    ) {
        holder.itemSymptomName.text = symptomViewModel.symptomList.value!![position].note
        holder.itemSymptomTime.text =
            convertDateToTime(symptomViewModel.symptomList.value!![position].dateTime)
        holder.itemSymptomImg.setImageResource(getSymptomImg(symptomViewModel.symptomList.value!![position]))
        holder.itemView.setOnClickListener {
            onItemClick.invoke(position)
        }
    }

}
