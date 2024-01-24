package com.project.meongcare.excreta.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.meongcare.databinding.ItemExcretaRecordEditBinding
import com.project.meongcare.excreta.model.data.local.ExcretaItemCheckedListener
import com.project.meongcare.excreta.model.entities.Excreta
import com.project.meongcare.excreta.model.entities.ExcretaRecord
import com.project.meongcare.excreta.utils.ExcretaDateTimeUtils.convertToTimeFormat

class ExcretaRecordEditAdapter(
    private val excretaItemCheckedListener: ExcretaItemCheckedListener,
) : ListAdapter<ExcretaRecord, ExcretaRecordEditAdapter.ExcretaRecordEditViewHolder>(diffUtil) {
    inner class ExcretaRecordEditViewHolder(private val binding: ItemExcretaRecordEditBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.checkboxExcretarecordedit.setOnCheckedChangeListener { _, isChecked ->
                val item = currentList[bindingAdapterPosition]
                item.isChecked = isChecked
                val checkedIds =
                    currentList.filter { it.isChecked }
                        .map { it.excretaId.toInt() }
                        .toIntArray()
                excretaItemCheckedListener.onItemChecked(checkedIds)
            }
        }

        fun bind(item: ExcretaRecord) {
            binding.apply {
                checkboxExcretarecordedit.isChecked = item.isChecked
                includeExcretarecordeditRecord.apply {
                    textviewExcretarecordType.text = Excreta.valueOf(item.excretaType).type
                    textviewExcretarecordTime.text = convertToTimeFormat(item.time)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ExcretaRecordEditViewHolder {
        val itemExcretaRecordEditBinding =
            ItemExcretaRecordEditBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )

        return ExcretaRecordEditViewHolder(itemExcretaRecordEditBinding)
    }

    override fun onBindViewHolder(
        holder: ExcretaRecordEditViewHolder,
        position: Int,
    ) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil =
            object : DiffUtil.ItemCallback<ExcretaRecord>() {
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
