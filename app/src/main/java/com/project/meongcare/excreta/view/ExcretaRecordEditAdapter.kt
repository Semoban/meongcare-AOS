package com.project.meongcare.excreta.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.meongcare.databinding.ItemExcretaRecordEditBinding
import com.project.meongcare.excreta.model.entities.Excreta
import com.project.meongcare.excreta.model.entities.ExcretaRecord

class ExcretaRecordEditAdapter : ListAdapter<ExcretaRecord, ExcretaRecordEditAdapter.ExcretaRecordEditViewHolder>(diffUtil) {
    inner class ExcretaRecordEditViewHolder(private val binding: ItemExcretaRecordEditBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ExcretaRecord) {
            binding.apply {
                checkboxExcretarecordedit.isChecked = item.isChecked
                includeExcretarecordeditRecord.apply {
                    textviewExcretarecordType.text = Excreta.valueOf(item.excretaType).type
                    textviewExcretarecordTime.text = ExcretaAdapter.convertToTimeFormat(item.time)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ExcretaRecordEditViewHolder {
        val itemExcretaRecordEditBinding = ItemExcretaRecordEditBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return ExcretaRecordEditViewHolder(itemExcretaRecordEditBinding)
    }

    override fun onBindViewHolder(holder: ExcretaRecordEditViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ExcretaRecord>() {
            override fun areItemsTheSame(
                oldItem: ExcretaRecord,
                newItem: ExcretaRecord,
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ExcretaRecord,
                newItem: ExcretaRecord,
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
