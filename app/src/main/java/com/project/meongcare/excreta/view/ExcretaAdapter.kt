package com.project.meongcare.excreta.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.meongcare.R
import com.project.meongcare.databinding.ItemExcretaRecordBinding
import com.project.meongcare.excreta.model.entities.Excreta
import com.project.meongcare.excreta.model.entities.ExcretaRecord
import com.project.meongcare.excreta.utils.ExcretaDateTimeUtils.convertToTimeFormat

class ExcretaAdapter : ListAdapter<ExcretaRecord, ExcretaAdapter.ExcretaViewHolder>(diffUtil) {
    inner class ExcretaViewHolder(private val binding: ItemExcretaRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ExcretaRecord) {
            binding.run {
                val excretaTime = convertToTimeFormat(item.time)
                textviewExcretarecordType.text = Excreta.valueOf(item.excretaType).type
                textviewExcretarecordTime.text = excretaTime
                root.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putLong("excretaId", item.excretaId)
                    bundle.putString("excretaTime", excretaTime)
                    it.findNavController()
                        .navigate(R.id.action_excretaFragment_to_excretaInfoFragment, bundle)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ExcretaViewHolder {
        val itemExcretaBinding =
            ItemExcretaRecordBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )

        return ExcretaViewHolder(itemExcretaBinding)
    }

    override fun onBindViewHolder(
        holder: ExcretaViewHolder,
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
