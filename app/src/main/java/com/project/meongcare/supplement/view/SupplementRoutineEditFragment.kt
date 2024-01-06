package com.project.meongcare.supplement.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.meongcare.MainActivity
import com.project.meongcare.databinding.FragmentSupplementRoutineEditBinding
import com.project.meongcare.databinding.ItemSupplementRoutineEditBinding
import com.project.meongcare.supplement.model.entities.Supplement

class SupplementRoutineEditFragment : Fragment() {
    lateinit var fragmentSupplementRoutineEditBinding: FragmentSupplementRoutineEditBinding
    lateinit var mainActivity: MainActivity
    lateinit var navController: NavController
    var supplementsList = mutableListOf<Supplement>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mainActivity = activity as MainActivity
        fragmentSupplementRoutineEditBinding =
            FragmentSupplementRoutineEditBinding.inflate(layoutInflater)

        navController = findNavController()

        supplementsList = arguments?.getParcelableArrayList<Supplement>("supplements_key")!!

        Log.d("루틴 편집", supplementsList.toString())

        fragmentSupplementRoutineEditBinding.run {
            toolbarSupplementRoutineEdit.setNavigationOnClickListener { navController.popBackStack() }
            recyclerViewSupplementRoutineEdit.run {
                adapter = SupplementRoutineEditRecyclerViewAdapter()
                layoutManager = LinearLayoutManager(context)
            }

            buttonSupplementRoutineEditCancel.setOnClickListener {
                navController.popBackStack()
            }
        }
        return fragmentSupplementRoutineEditBinding.root
    }

    inner class SupplementRoutineEditRecyclerViewAdapter :
        RecyclerView.Adapter<SupplementRoutineEditRecyclerViewAdapter.SupplementRoutineEditHolder>() {
        inner class SupplementRoutineEditHolder(itemSupplementRoutineEditBinding: ItemSupplementRoutineEditBinding) :
            RecyclerView.ViewHolder(itemSupplementRoutineEditBinding.root) {
            val itemSupplementRoutineEditName: TextView
            val itemSupplementRoutineEditAmount: TextView
            val itemSupplementRoutineEditCheckImg: ImageView
            val itemSupplementRoutineEditAlarm: ImageView

            init {
                itemSupplementRoutineEditName =
                    itemSupplementRoutineEditBinding.textViewItemSupplementRoutineEditName
                itemSupplementRoutineEditAmount =
                    itemSupplementRoutineEditBinding.textViewItemSupplementRoutineEditAmount
                itemSupplementRoutineEditCheckImg =
                    itemSupplementRoutineEditBinding.imageViewItemSupplementRoutineEditCheck
                itemSupplementRoutineEditAlarm =
                    itemSupplementRoutineEditBinding.imageViewItemSupplementRoutineEditAlarm
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int,
        ): SupplementRoutineEditHolder {
            val itemSupplementRoutineEditBinding =
                ItemSupplementRoutineEditBinding.inflate(layoutInflater)
            val allViewHolder = SupplementRoutineEditHolder(itemSupplementRoutineEditBinding)

            itemSupplementRoutineEditBinding.root.layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )

            return allViewHolder
        }

        override fun getItemCount(): Int {
            return supplementsList.size
        }

        override fun onBindViewHolder(
            holder: SupplementRoutineEditHolder,
            position: Int,
        ) {
            holder.itemSupplementRoutineEditName.text = supplementsList[position].name
            val intakeCount = supplementsList[position].intakeCount
            val intakeUnit = supplementsList[position].intakeUnit
            holder.itemSupplementRoutineEditAmount.text = "$intakeCount$intakeUnit"

            val supplementsRecordId = supplementsList[position].supplementsRecordId

            holder.itemSupplementRoutineEditCheckImg.setOnClickListener {
                it.isSelected = !it.isSelected
            }
        }
    }
}
