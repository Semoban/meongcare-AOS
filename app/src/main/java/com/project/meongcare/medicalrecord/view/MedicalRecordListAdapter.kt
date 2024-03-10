package com.project.meongcare.medicalrecord.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.meongcare.databinding.ItemMedicalRecordBinding
import com.project.meongcare.medicalrecord.model.data.local.MedicalRecordItemClickListener
import com.project.meongcare.medicalrecord.model.entities.MedicalRecordListItem

class MedicalRecordListAdapter(
    private val medicalRecordItemClickListener: MedicalRecordItemClickListener,
) : ListAdapter<MedicalRecordListItem, MedicalRecordListAdapter.MedicalRecordViewHolder>(diffUtil) {
    inner class MedicalRecordViewHolder(private val binding: ItemMedicalRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MedicalRecordListItem) {
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

    private fun showFormattedTime(dateTime: String): String {
        val time = dateTime.substringAfterLast("T")
        val hourMinute = time.substringBeforeLast(":")

        val hour = hourMinute.substringBeforeLast(":")
        val minute = hourMinute.substringAfterLast(":")

        return if (hour.toInt() < 12){
            "오전 $hour:$minute"
        } else {
            "오후 $hour:$minute"
        }
    }

    companion object {
        val diffUtil =
            object : DiffUtil.ItemCallback<MedicalRecordListItem>() {
                override fun areItemsTheSame(
                    oldItem: MedicalRecordListItem,
                    newItem: MedicalRecordListItem
                ): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(
                    oldItem: MedicalRecordListItem,
                    newItem: MedicalRecordListItem
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
