package com.project.meongcare.excreta.view

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

class ExcretaAdapter : ListAdapter<ExcretaRecord, ExcretaAdapter.ExcretaViewHolder>(diffUtil) {
    inner class ExcretaViewHolder(private val binding: ItemExcretaRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ExcretaRecord) {
            binding.run {
                textviewExcretarecordType.text = Excreta.valueOf(item.excretaType).type
                textviewExcretarecordTime.text = convertToTimeFormat(item.time)
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

        fun convertToTimeFormat(date: String): String {
            val hour = date.substring(HOUR_START, HOUR_END).toInt()
            val minute = date.substring(MINUTE_START, MINUTE_END).toInt()

            if (hour == NOON) return String.format("$AFTERNOON $TIME_FORM", hour, minute)
            if (hour > NOON) return String.format("$AFTERNOON $TIME_FORM", hour - NOON, minute)
            return String.format("$MORNING $TIME_FORM", hour, minute)
        }

        private const val HOUR_START = 11
        private const val HOUR_END = 13
        private const val MINUTE_START = 14
        private const val MINUTE_END = 16
        private const val NOON = 12
        private const val MORNING = "오전"
        private const val AFTERNOON = "오후"
        private const val TIME_FORM = "%02d:%02d"
    }
}
