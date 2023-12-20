package com.project.meongcare.excreta.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.meongcare.R
import com.project.meongcare.databinding.ItemExcretaRecordBinding
import com.project.meongcare.excreta.model.entities.ExcretaGetResponse

class ExcretaAdapter : ListAdapter<ExcretaGetResponse, ExcretaAdapter.ExcretaViewHolder>(diffUtil) {
    inner class ExcretaViewHolder(private val binding: ItemExcretaRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ExcretaGetResponse) {
            binding.run {
                textviewExcretarecordType.text = item.excretaType
                textviewExcretarecordTime.text = item.time
                root.setOnClickListener {
                    it.findNavController()
                        .navigate(R.id.action_excretaFragment_to_excretaInfoFragment)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ExcretaViewHolder {
        val itemExcretaBinding = ItemExcretaRecordBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return ExcretaViewHolder(itemExcretaBinding)
    }

    override fun onBindViewHolder(holder: ExcretaViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ExcretaGetResponse>() {
            override fun areItemsTheSame(
                oldItem: ExcretaGetResponse,
                newItem: ExcretaGetResponse,
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ExcretaGetResponse,
                newItem: ExcretaGetResponse,
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
