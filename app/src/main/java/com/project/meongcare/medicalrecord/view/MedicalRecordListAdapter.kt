package com.project.meongcare.medicalRecord.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.meongcare.databinding.ItemMedicalRecordBinding
import com.project.meongcare.medicalRecord.model.data.local.MedicalRecordItemClickListener
import com.project.meongcare.medicalRecord.model.entities.MedicalRecord
import com.project.meongcare.medicalRecord.model.utils.MedicalRecordDateUtils.showFormattedTime

class MedicalRecordListAdapter(
    private val medicalRecordItemClickListener: MedicalRecordItemClickListener,
) : ListAdapter<MedicalRecord, MedicalRecordListAdapter.MedicalRecordViewHolder>(diffUtil) {
    inner class MedicalRecordViewHolder(private val binding: ItemMedicalRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MedicalRecord) {
            binding.run {
                textviewMedicalrecordTime.text = showFormattedTime(item.dateTime)
                viewMedicalrecord.setOnClickListener {
                    medicalRecordItemClickListener.onMedicalRecordItemClick(item.medicalRecordId)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MedicalRecordViewHolder {
        val itemMedicalRecordBinding = ItemMedicalRecordBinding.inflate(LayoutInflater.from(parent.context))
        return MedicalRecordViewHolder(itemMedicalRecordBinding)
    }

    override fun onBindViewHolder(
        holder: MedicalRecordViewHolder,
        position: Int,
    ) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil =
            object : DiffUtil.ItemCallback<MedicalRecord>() {
                override fun areItemsTheSame(
                    oldItem: MedicalRecord,
                    newItem: MedicalRecord,
                ): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(
                    oldItem: MedicalRecord,
                    newItem: MedicalRecord,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
