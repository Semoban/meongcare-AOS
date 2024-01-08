package com.project.meongcare.supplement.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.meongcare.MainActivity
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentSupplementInfoBinding
import com.project.meongcare.databinding.ItemSupplementAddTimeBinding
import com.project.meongcare.supplement.model.data.repository.SupplementRepository
import com.project.meongcare.supplement.model.entities.DetailSupplement
import com.project.meongcare.supplement.utils.SupplementUtils
import com.project.meongcare.supplement.viewmodel.SupplementViewModel
import com.project.meongcare.supplement.viewmodel.SupplementViewModelFactory

class SupplementInfoFragment : Fragment() {
    lateinit var fragmentSupplementInfoBinding: FragmentSupplementInfoBinding
    lateinit var mainActivity: MainActivity
    lateinit var supplementViewModel: SupplementViewModel
    lateinit var navController: NavController
    var supplementId: Int? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentSupplementInfoBinding = FragmentSupplementInfoBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        navController = findNavController()
        supplementId = arguments?.getInt("supplementsId")
        Log.d("영양제 정보1", supplementId.toString())

        val factory = SupplementViewModelFactory(SupplementRepository())
        supplementViewModel = ViewModelProvider(this, factory)[SupplementViewModel::class.java]

        supplementViewModel.run {
            getSupplementDetail(supplementId!!)
            supplementDetail.observe(viewLifecycleOwner) {
                fragmentSupplementInfoBinding.run {
                    textViewSupplementInfoName.text = it.name
                    textViewSupplementInfoBrandName.text = it.brand
                    textViewSupplementInfoCycleCount.text = it.intakeCycle.toString()

                    setButtonSelected(it)

                    recyclerViewSupplementInfoTimeList.run {
                        adapter = SupplementInfoTimeRecyclerViewAdapter()
                        layoutManager = LinearLayoutManager(context)
                    }
                }
            }

        }

        fragmentSupplementInfoBinding.run {
            toolbarSupplementInfo.run {
                setNavigationOnClickListener {
                    navController.popBackStack()
                }

                setOnMenuItemClickListener {
                    Log.d("메뉴 클릭","성공")
                    includeSupplementInfoDeleteDialog.root.visibility = View.VISIBLE
                    includeSupplementInfoDeleteDialog.run {
                        buttonDeleteDialogCancel.setOnClickListener {
                            includeSupplementInfoDeleteDialog.root.visibility = View.GONE
                        }
                        buttonDeleteDialogDelete.setOnClickListener {
                            supplementViewModel.deleteSupplement(supplementId!!,navController)
                        }
                    }

                    false
                }
            }

            // Todo : supplementViewModel.supplementDetail.value.isActive 로 교체
            var temp = true
            buttonSupplementInfoRoutine.run {
                this.isSelected = temp
                val selected = ContextCompat.getColor(context, R.color.white)
                val unselected = ContextCompat.getColor(context, R.color.gray4)

                textViewButtonSupplementInfoRoutine.run {
                    if (isSelected) {
                        setTextColor(unselected)
                        text = "루틴 중단"
                    } else {
                        setTextColor(selected)
                        text = "루틴 시작하기"
                    }
                }

                Log.d("루틴 버튼2",textViewButtonSupplementInfoRoutine.currentTextColor.toString())
            }

            buttonSupplementInfoRoutine.setOnClickListener {
                temp = !temp
                it.isSelected = !it.isSelected
                val activeStatus = it.isSelected
                supplementViewModel.patchSupplementActive(supplementId!!, activeStatus,mainActivity,textViewButtonSupplementInfoRoutine)
            }

        }

        return fragmentSupplementInfoBinding.root
    }

    inner class SupplementInfoTimeRecyclerViewAdapter :
        RecyclerView.Adapter<SupplementInfoTimeRecyclerViewAdapter.SupplementInfoTimeViewHolder>() {

        val intakeList =
            supplementViewModel.supplementDetail.value!!.intakeInfos.sortedBy { it.intakeTime }

        inner class SupplementInfoTimeViewHolder(itemSupplementInfoTimeBinding: ItemSupplementAddTimeBinding) :
            RecyclerView.ViewHolder(itemSupplementInfoTimeBinding.root) {
            val itemSupplementInfoTimeTime: TextView
            val itemSupplementInfoTimeAmount: TextView

            init {
                itemSupplementInfoTimeTime =
                    itemSupplementInfoTimeBinding.textViewItemSupplementAddTime
                itemSupplementInfoTimeAmount =
                    itemSupplementInfoTimeBinding.textViewItemSupplementAddTimeAmount
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int,
        ): SupplementInfoTimeViewHolder {
            val itemSupplementInfoTimeBinding = ItemSupplementAddTimeBinding.inflate(layoutInflater)
            val allViewHolder = SupplementInfoTimeViewHolder(itemSupplementInfoTimeBinding)

            itemSupplementInfoTimeBinding.root.layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )

            return allViewHolder
        }

        override fun getItemCount(): Int {
            return intakeList.size
        }

        override fun onBindViewHolder(
            holder: SupplementInfoTimeViewHolder,
            position: Int,
        ) {
            val intakeCountString = intakeList[position].intakeCount
            val intakeUnitString = supplementViewModel.supplementDetail.value!!.intakeUnit
            holder.itemSupplementInfoTimeTime.text =
                SupplementUtils.convertDateToTime(intakeList[position].intakeTime)
            holder.itemSupplementInfoTimeAmount.text = "${intakeCountString}${intakeUnitString}"
        }
    }

    private fun FragmentSupplementInfoBinding.setButtonSelected(it: DetailSupplement) {
        val selected = R.drawable.all_rect_main1_r5_outline_main3
        val selectedTextColor = ContextCompat.getColor(mainActivity, R.color.main4)

        when (it.intakeUnit) {
            "mg" -> {
                buttonSupplementInfoUnitMg.setBackgroundResource(selected)
                textViewButtonSupplementInfoUnitMg.setTextColor(selectedTextColor)
            }

            "스쿱" -> {
                buttonSupplementInfoUnitScoop.setBackgroundResource(selected)
                textViewButtonSupplementInfoUnitScoop.setTextColor(selectedTextColor)

            }

            "정" -> {
                buttonSupplementInfoUnitJung.setBackgroundResource(selected)
                textViewButtonSupplementInfoUnitJung.setTextColor(selectedTextColor)
            }
        }
    }
}
