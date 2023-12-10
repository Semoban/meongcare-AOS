package com.project.meongcare.symptom.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.meongcare.MainActivity
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentSymptomBinding
import com.project.meongcare.databinding.ItemSymptomBinding
import com.project.meongcare.symptom.model.entities.Symptom
import com.project.meongcare.symptom.model.entities.SymptomType
import com.project.meongcare.symptom.viewmodel.SymptomViewModel
import java.util.Calendar
import java.util.Date

class SymptomFragment : Fragment() {
    lateinit var fragmentSymptomBinding: FragmentSymptomBinding
    lateinit var mainActivity: MainActivity
    lateinit var symptomViewModel: SymptomViewModel
    private val calendar = Calendar.getInstance()
    private var currentMonth = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentSymptomBinding = FragmentSymptomBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        calendar.time = Date()
        currentMonth = calendar[Calendar.MONTH]

        symptomViewModel = ViewModelProvider(this)[SymptomViewModel::class.java]

        symptomViewModel.run {
            symptomList.observe(viewLifecycleOwner) {
                if (it.isNullOrEmpty()) {
                    fragmentSymptomBinding.run {
                        textViewSymptomNoData.visibility = View.VISIBLE
                        recyclerViewSymptom.visibility = View.GONE
                    }
                }

                fragmentSymptomBinding.run {
                    textViewSymptomNoData.visibility = View.GONE
                    recyclerViewSymptom.visibility = View.VISIBLE
                    recyclerViewSymptom.run {
                        adapter = SymptomRecyclerViewAdapter()
                        layoutManager = LinearLayoutManager(context)
                    }
                }
            }
        }

        // 현재 로그인한 유저의 현재 강아지 이름
        val dogName = "김대박"

        fragmentSymptomBinding.run {
            textViewSymptomDogName.text = dogName

            textViewSymptomAdd.setOnClickListener {
//                mainActivity.replaceFragment(mainActivity.SYMPTOM_ADD_FRAGMENT, true, null)
            }

            textViewSymptomEdit.setOnClickListener {
//                mainActivity.replaceFragment(mainActivity.SYMPTOM_LIST_EDIT_FRAGMENT, true, null)
            }

        }
        return fragmentSymptomBinding.root
    }

    // 이상증상 타임라인
    inner class SymptomRecyclerViewAdapter :
        RecyclerView.Adapter<SymptomRecyclerViewAdapter.SymptomViewHolder>() {
        inner class SymptomViewHolder(itemSymptomBinding: ItemSymptomBinding) :
            RecyclerView.ViewHolder(itemSymptomBinding.root) {
            val itemSymptomName: TextView
            val itemSymptomTime: TextView
            val itemSymptomImg: ImageView

            init {
                itemSymptomName = itemSymptomBinding.textViewItemSymptom
                itemSymptomTime = itemSymptomBinding.textViewItemSymptomTime
                itemSymptomImg = itemSymptomBinding.imageViewItemSymptom
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int,
        ): SymptomViewHolder {
            val itemSymptomBinding = ItemSymptomBinding.inflate(layoutInflater)
            val allViewHolder = SymptomViewHolder(itemSymptomBinding)

            itemSymptomBinding.root.layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )

            itemSymptomBinding.root.setOnClickListener {
//                mainActivity.replaceFragment(mainActivity.SYMPTOM_INFO_FRAGMENT, true, null)
            }

            return allViewHolder
        }

        override fun getItemCount(): Int {
            return symptomViewModel.symptomList.value!!.size
        }

        override fun onBindViewHolder(
            holder: SymptomViewHolder,
            position: Int,
        ) {
            holder.itemSymptomName.text = symptomViewModel.symptomList.value!![position].note
            holder.itemSymptomTime.text = symptomViewModel.symptomList.value!![position].dateTime
            holder.itemSymptomImg.setImageResource(getSymptomImg(symptomViewModel.symptomList.value!![position]))
        }
    }

    fun getSymptomImg(symptomData: Symptom): Int {
        return when (symptomData.symptomString) {
            SymptomType.WEIGHT_LOSS.symptomName -> R.drawable.all_weighing_machine
            SymptomType.HIGH_FEVER.symptomName -> R.drawable.all_temperature_measurement
            SymptomType.COUGH.symptomName -> R.drawable.symptom_cough
            SymptomType.DIARRHEA.symptomName -> R.drawable.symptom_diarrhea
            SymptomType.LOSS_OF_APPETITE.symptomName -> R.drawable.symptom_loss_appetite
            SymptomType.ACTIVITY_DECREASE.symptomName -> R.drawable.symptom_amount_activity
            else -> R.drawable.symptom_stethoscope
        }
    }
}
