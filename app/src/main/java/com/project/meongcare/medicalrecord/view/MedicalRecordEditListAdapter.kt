package com.project.meongcare.medicalrecord.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.meongcare.databinding.ItemMedicalRecordEditBinding
import com.project.meongcare.medicalrecord.model.data.local.MedicalRecordItemCheckListener
import com.project.meongcare.medicalrecord.model.entities.MedicalRecord
import com.project.meongcare.medicalrecord.model.utils.MedicalRecordDateUtils.showFormattedTime

class MedicalRecordEditListAdapter(
    private val medicalRecordItemCheckListener: MedicalRecordItemCheckListener,
) : ListAdapter<MedicalRecord, MedicalRecordEditListAdapter.MedicalRecordEditViewHolder>(MedicalRecordEditDiffUtil) {
    inner class MedicalRecordEditViewHolder(private val binding: ItemMedicalRecordEditBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.run {
                viewMedicalrecordedit.setOnClickListener {
                    checkboxMedicalrecordedit.isChecked = !checkboxMedicalrecordedit.isChecked
                }
                checkboxMedicalrecordedit.setOnCheckedChangeListener { buttonView, isChecked ->
                    val item = currentList[bindingAdapterPosition]
                    item.isChecked = isChecked

                    val checkedRecordIds =
                        currentList.filter { it.isChecked }
                            .map { it.medicalRecordId.toInt() }
                            .toIntArray()

                    medicalRecordItemCheckListener.onMedicalRecordItemChecked(checkedRecordIds)
                }
            }
        }

        fun bind(item: MedicalRecord) {
            binding.run {
                textviewMedicalrecordeditTime.text = showFormattedTime(item.dateTime)
                checkboxMedicalrecordedit.isChecked = item.isChecked
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MedicalRecordEditViewHolder {
        val itemMedicalRecordEditBinding = ItemMedicalRecordEditBinding.inflate(LayoutInflater.from(parent.context))
        itemMedicalRecordEditBinding.root.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
        return MedicalRecordEditViewHolder(itemMedicalRecordEditBinding)
    }

    override fun onBindViewHolder(
        holder: MedicalRecordEditViewHolder,
        position: Int,
    ) {
        holder.bind(currentList[position])
    }

    object MedicalRecordEditDiffUtil : DiffUtil.ItemCallback<MedicalRecord>() {
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
