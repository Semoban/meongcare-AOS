package com.project.meongcare.onboarding.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.project.meongcare.databinding.ItemSearchDogVarietyBinding
import com.project.meongcare.onboarding.model.data.local.DogTypeSelectListener

class DogTypeAdapter(
    private val layoutInflater: LayoutInflater,
    private val dogTypeSelectListener: DogTypeSelectListener,
) : RecyclerView.Adapter<DogTypeAdapter.DogTypeViewHolder>() {
    private var dogTypeList: List<String> = emptyList()

    fun updateDogTypeList(newList: List<String>) {
        this.dogTypeList = newList
        notifyDataSetChanged()
    }

    inner class DogTypeViewHolder(itemBinding: ItemSearchDogVarietyBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val constraintDogType: ConstraintLayout
        val textviewItemDog: TextView

        init {
            constraintDogType = itemBinding.constraintDogType
            textviewItemDog = itemBinding.textviewItemDog
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): DogTypeViewHolder {
        val itemBinding = ItemSearchDogVarietyBinding.inflate(layoutInflater)
        itemBinding.root.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
        return DogTypeViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return dogTypeList.size
    }

    override fun onBindViewHolder(
        holder: DogTypeViewHolder,
        position: Int,
    ) {
        holder.textviewItemDog.text = dogTypeList[position]

        holder.constraintDogType.setOnClickListener {
            dogTypeSelectListener.onDogTypeSelected(dogTypeList[position])
        }
    }
}
