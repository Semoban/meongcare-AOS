package com.project.meongcare.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.meongcare.R
import com.project.meongcare.databinding.ItemHomeSymptomBinding
import com.project.meongcare.home.model.entities.Symptom
import com.project.meongcare.symptom.model.entities.SymptomType

class HomeSymptomAdapter(
    private val layoutInflater: LayoutInflater,
    private val context: Context,
) : RecyclerView.Adapter<HomeSymptomAdapter.SymptomViewHolder>() {
    private var symptomList: List<Symptom> = emptyList()

    fun updateSymptomList(newList: List<Symptom>) {
        this.symptomList = newList
        notifyDataSetChanged()
    }

    inner class SymptomViewHolder(itemHomeSymptomBinding: ItemHomeSymptomBinding) :
        RecyclerView.ViewHolder(itemHomeSymptomBinding.root) {
        val imageviewSymptom: ImageView
        val textviewSymptom: TextView

        init {
            imageviewSymptom = itemHomeSymptomBinding.imageviewSymptom
            textviewSymptom = itemHomeSymptomBinding.textviewSymptom
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SymptomViewHolder {
        val itemHomeSymptomBinding = ItemHomeSymptomBinding.inflate(layoutInflater)
        itemHomeSymptomBinding.root.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
        return SymptomViewHolder(itemHomeSymptomBinding)
    }

    override fun getItemCount(): Int {
        return symptomList.size
    }

    override fun onBindViewHolder(
        holder: SymptomViewHolder,
        position: Int,
    ) {
        Glide.with(context)
            .load(getSymptomImg(symptomList[position].symptomString))
            .into(holder.imageviewSymptom)

        holder.textviewSymptom.text = symptomList[position].note
    }

    fun getSymptomImg(symptomType: String): Int {
        return when (symptomType) {
            SymptomType.WEIGHT_LOSS.symptomName -> R.drawable.all_weighing_machine
            SymptomType.HIGH_FEVER.symptomName -> R.drawable.all_temperature_measurement
            SymptomType.COUGH.symptomName -> R.drawable.symptom_cough
            SymptomType.DIARRHEA.symptomName -> R.drawable.symptom_diarrhea
            SymptomType.LOSS_OF_APPETITE.symptomName -> R.drawable.symptom_loss_appetite
            SymptomType.ACTIVITY_DECREASE.symptomName -> R.drawable.symptom_amount_activity
            else -> R.drawable.symptom_etc_record
        }
    }
}
