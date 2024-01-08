package com.project.meongcare.supplement.view

import android.os.Bundle
import android.util.Log
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
import com.project.meongcare.databinding.FragmentSupplementRoutineEditBinding
import com.project.meongcare.databinding.ItemSupplementRoutineEditBinding
import com.project.meongcare.supplement.model.data.repository.SupplementRepository
import com.project.meongcare.supplement.viewmodel.SupplementViewModel
import com.project.meongcare.supplement.viewmodel.SupplementViewModelFactory

class SupplementRoutineEditFragment : Fragment() {
    lateinit var fragmentSupplementRoutineEditBinding: FragmentSupplementRoutineEditBinding
    lateinit var mainActivity: MainActivity
    lateinit var navController: NavController
    lateinit var supplementViewModel: SupplementViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mainActivity = activity as MainActivity
        fragmentSupplementRoutineEditBinding =
            FragmentSupplementRoutineEditBinding.inflate(layoutInflater)

        navController = findNavController()

        val factory = SupplementViewModelFactory(SupplementRepository())
        supplementViewModel = ViewModelProvider(this, factory)[SupplementViewModel::class.java]

        supplementViewModel.run {
            getSupplementInfos(1)
            supplementIdListAllCheck.observe(viewLifecycleOwner) {
                Log.d("루틴 변경", it.toString())
                Log.d("루틴 변경2", supplementIdList.value.toString())
                fragmentSupplementRoutineEditBinding.run {
                    recyclerViewSupplementRoutineEdit.run {
                        adapter = SupplementRoutineEditRecyclerViewAdapter()
                        layoutManager = LinearLayoutManager(context)
                    }
                }
            }
            supplementInfoList.observe(viewLifecycleOwner) {
                fragmentSupplementRoutineEditBinding.recyclerViewSupplementRoutineEdit.run {
                    adapter = SupplementRoutineEditRecyclerViewAdapter()
                    layoutManager = LinearLayoutManager(context)
                }
            }
        }

        fragmentSupplementRoutineEditBinding.run {
            toolbarSupplementRoutineEdit.setNavigationOnClickListener { navController.popBackStack() }

            imageViewSupplementRoutineEditDeleteAllCheck.setOnClickListener { view ->
                val isAllSelected = !view.isSelected
                view.isSelected = isAllSelected
                val temp = supplementViewModel.supplementInfoList.value!!.map { it.supplementsId }
                    .toMutableList()
                supplementViewModel.setAllItemsChecked(
                    imageViewSupplementRoutineEditDeleteAllCheck.isSelected,
                    temp
                )
            }

            buttonSupplementRoutineEditCancel.setOnClickListener {
                navController.popBackStack()
            }

            buttonSupplementRoutineEditComplete.setOnClickListener {
                includeSupplementRoutineEditDeleteDialog.run {
                    root.visibility = View.VISIBLE
                    buttonDeleteDialogCancel.setOnClickListener {
                        includeSupplementRoutineEditDeleteDialog.root.visibility = View.GONE
                    }
                    buttonDeleteDialogDelete.setOnClickListener {
                        supplementViewModel.deleteSupplements(supplementViewModel.supplementIdList.value!!.toIntArray())
                        navController.popBackStack()
                    }
                }
            }

        }
        return fragmentSupplementRoutineEditBinding.root
    }

    inner class SupplementRoutineEditRecyclerViewAdapter :
        RecyclerView.Adapter<SupplementRoutineEditRecyclerViewAdapter.SupplementRoutineEditHolder>() {

        inner class SupplementRoutineEditHolder(itemSupplementRoutineEditBinding: ItemSupplementRoutineEditBinding) :
            RecyclerView.ViewHolder(itemSupplementRoutineEditBinding.root) {
            val itemSupplementRoutineEditName: TextView
            val itemSupplementRoutineEditCheckImg: ImageView
            val itemSupplementRoutineEditAlarm: ImageView

            init {
                itemSupplementRoutineEditName =
                    itemSupplementRoutineEditBinding.textViewItemSupplementRoutineEditName
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
            return supplementViewModel.supplementInfoList.value!!.size
        }

        override fun onBindViewHolder(
            holder: SupplementRoutineEditHolder,
            position: Int,
        ) {
            Log.d("루틴 편집20", supplementViewModel.supplementInfoList.value!!.toString())
            holder.itemSupplementRoutineEditName.text =
                supplementViewModel.supplementInfoList.value!![position].name

            val supplementsId =
                supplementViewModel.supplementInfoList.value!![position].supplementsId
            Log.d("루틴 편집3", supplementsId.toString())

            holder.itemSupplementRoutineEditCheckImg.isSelected =
                supplementViewModel.supplementIdList.value!!.contains(supplementsId)
            Log.d("루틴 편집4", supplementsId.toString())

            holder.itemSupplementRoutineEditCheckImg.setOnClickListener {
                supplementViewModel.updateSupplementIds(mutableListOf(supplementsId))
                holder.itemSupplementRoutineEditCheckImg.isSelected =
                    !holder.itemSupplementRoutineEditCheckImg.isSelected

                if (supplementViewModel.supplementIdListAllCheck.value!!) {
                    fragmentSupplementRoutineEditBinding.imageViewSupplementRoutineEditDeleteAllCheck.isSelected =
                        false
                }
            }

            holder.itemSupplementRoutineEditName.setOnClickListener {
                navController.navigate(R.id.action_supplementRoutineEdit_to_supplementInfo)
            }

            holder.itemSupplementRoutineEditAlarm.isSelected =
                !supplementViewModel.supplementInfoList.value!![position].pushAgreement

            holder.itemSupplementRoutineEditAlarm.setOnClickListener {
                val supplementsId =
                    supplementViewModel.supplementInfoList.value!![position].supplementsId
                it.isSelected = !it.isSelected
                val isAlarmTrue = it.isSelected
                supplementViewModel.patchSupplementAlarm(supplementsId, !isAlarmTrue)
            }
        }
    }
}
