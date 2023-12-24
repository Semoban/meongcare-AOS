package com.project.meongcare.supplement.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.meongcare.MainActivity
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentSupplementBinding
import com.project.meongcare.databinding.ItemSupplementBinding
import com.project.meongcare.supplement.viewmodel.SupplementViewModel
import com.project.meongcare.toolbar.viewmodel.ToolbarViewModel

class SupplementFragment : Fragment() {
    lateinit var fragmentSupplementBinding: FragmentSupplementBinding
    lateinit var mainActivity: MainActivity
    lateinit var toolbarViewModel: ToolbarViewModel
    lateinit var supplementViewModel: SupplementViewModel
    lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentSupplementBinding = FragmentSupplementBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        toolbarViewModel = mainActivity.toolbarViewModel
        supplementViewModel = ViewModelProvider(this)[SupplementViewModel::class.java]

        supplementViewModel.run {
            if (toolbarViewModel.selectedDate.value != null) {
                updateSupplementList(1, toolbarViewModel.selectedDate.value!!)
            }
            supplementList.observe(viewLifecycleOwner) {
                fragmentSupplementBinding.run {
                    if (supplementViewModel.supplementList.value.isNullOrEmpty()) {
                        recyclerViewSupplement.visibility = View.GONE
                        textViewSupplementEdit.visibility = View.GONE
                        layoutSupplementNoData.visibility = View.VISIBLE
                    } else {
                        recyclerViewSupplement.visibility = View.VISIBLE
                        textViewSupplementEdit.visibility = View.VISIBLE
                        layoutSupplementNoData.visibility = View.GONE
                    }
                    recyclerViewSupplement.run {
                        adapter = SupplementRecyclerViewAdapter()
                        layoutManager = LinearLayoutManager(context)
                    }
                }
            }
        }

        navController = findNavController()

        return fragmentSupplementBinding.root
    }

    inner class SupplementRecyclerViewAdapter :
        RecyclerView.Adapter<SupplementRecyclerViewAdapter.SupplementViewHolder>() {
        inner class SupplementViewHolder(itemSupplementBinding: ItemSupplementBinding) :
            RecyclerView.ViewHolder(itemSupplementBinding.root) {
            val itemSupplementName: TextView
            val itemSupplementTime: TextView
            val itemSupplementUnit: TextView
            val itemSupplementCheckImg: ImageView

            init {
                itemSupplementName = itemSupplementBinding.textViewItemSupplementName
                itemSupplementTime = itemSupplementBinding.textViewItemSupplementTime
                itemSupplementUnit = itemSupplementBinding.textViewItemSupplementUnit
                itemSupplementCheckImg = itemSupplementBinding.imageViewItemSupplementCheck

                itemSupplementBinding.root.setOnClickListener {
                    navController.navigate(R.id.action_supplement_to_supplementInfo)
                }
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int,
        ): SupplementViewHolder {
            val itemSupplementBinding = ItemSupplementBinding.inflate(layoutInflater)
            val allViewHolder = SupplementViewHolder(itemSupplementBinding)

            itemSupplementBinding.root.layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )

            return allViewHolder
        }

        override fun getItemCount(): Int {
            return supplementViewModel.supplementList.value!!.size
        }

        override fun onBindViewHolder(
            holder: SupplementViewHolder,
            position: Int,
        ) {
            holder.itemSupplementName.text =
                supplementViewModel.supplementList.value!![position].name
            holder.itemSupplementTime.text =
                SupplementUtils.convertDateToTime(supplementViewModel.supplementList.value!![position].intakeTime)

            val intakeCount = supplementViewModel.supplementList.value!![position].intakeCount
            val intakeUnit = supplementViewModel.supplementList.value!![position].intakeUnit
            holder.itemSupplementUnit.text = "$intakeCount$intakeUnit"

            val intakeStatus = supplementViewModel.supplementList.value!![position].intakeStatus
            if (intakeStatus) {
                holder.itemSupplementCheckImg.setImageResource(R.drawable.all_un_check_20dp)
            } else {
                holder.itemSupplementCheckImg.setImageResource(R.drawable.all_check_20dp)
            }
        }
    }
}
