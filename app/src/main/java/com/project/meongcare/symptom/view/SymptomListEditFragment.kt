package com.project.meongcare.symptom.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.meongcare.MainActivity
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentSymptomListEditBinding
import com.project.meongcare.databinding.ItemSymptomListEditBinding
import com.project.meongcare.symptom.viewmodel.SymptomViewModel

class SymptomListEditFragment : Fragment() {
    lateinit var fragmentSymptomListEditBinding: FragmentSymptomListEditBinding
    lateinit var mainActivity: MainActivity
    lateinit var symptomViewModel: SymptomViewModel
    lateinit var navController: NavController
    var isCheckedAll = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentSymptomListEditBinding = FragmentSymptomListEditBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        symptomViewModel = mainActivity.symptomViewModel
        navController = findNavController()

        // TODO : 강아지 이름 연결 필요
        val dogName = "김대박"

        mainActivity.detachBottomNav()

        symptomViewModel.run {
            createCheckedStatusMap()

            listEditSymptomCheckedStatusMap.observe(viewLifecycleOwner) { map ->
                if (map.values.all { it }){
                    fragmentSymptomListEditBinding.imageViewSymptomListEditDeleteAllCheck.setImageResource(R.drawable.all_check_20dp)
                } else {
                    fragmentSymptomListEditBinding.imageViewSymptomListEditDeleteAllCheck.setImageResource(R.drawable.all_un_check_20dp)
                }

                fragmentSymptomListEditBinding.run {
                    recyclerViewSymptomListEdit.run {
                        adapter = SymptomListEditRecyclerViewAdapter()
                        layoutManager = LinearLayoutManager(context)
                    }
                }
            }
        }

        fragmentSymptomListEditBinding.run {
            toolbarSymptomListEdit.run {
                title = "${dogName}님의 이상증상"
                setNavigationOnClickListener {
                    navController.navigate(R.id.action_symptomListEdit_to_symptom)
                }
            }

            imageViewSymptomListEditDeleteAllCheck.setOnClickListener {
                symptomViewModel.run {
                    setAllCheckedStatusToFalse()
                }
            }

            recyclerViewSymptomListEdit.run {
                adapter = SymptomListEditRecyclerViewAdapter()
                layoutManager = LinearLayoutManager(context)
            }
        }
        return fragmentSymptomListEditBinding.root
    }

    inner class SymptomListEditRecyclerViewAdapter :
        RecyclerView.Adapter<SymptomListEditRecyclerViewAdapter.SymptomListEditViewHolder>() {
        inner class SymptomListEditViewHolder(itemSymptomListEditBinding: ItemSymptomListEditBinding) :
            RecyclerView.ViewHolder(itemSymptomListEditBinding.root) {
            val itemSymptomName: TextView
            val itemSymptomTime: TextView
            val itemSymptomImg: ImageView
            val itemSymptomCheck: ImageView

            init {
                itemSymptomName = itemSymptomListEditBinding.textViewItemSymptomListEdit
                itemSymptomTime = itemSymptomListEditBinding.textViewItemSymptomListEditTime
                itemSymptomImg = itemSymptomListEditBinding.imageViewItemSymptomListEdit
                itemSymptomCheck = itemSymptomListEditBinding.imageViewItemSymptomListEditCheck

                itemSymptomListEditBinding.root.setOnClickListener {
                    symptomViewModel.updateCheckedStatusMap(symptomViewModel.symptomList.value!![adapterPosition].symptomId)
                }
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int,
        ): SymptomListEditViewHolder {
            val itemSymptomListEditBinding = ItemSymptomListEditBinding.inflate(layoutInflater)
            val allViewHolder = SymptomListEditViewHolder(itemSymptomListEditBinding)

            itemSymptomListEditBinding.root.layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )

            return allViewHolder
        }

        override fun getItemCount(): Int {
            return symptomViewModel.symptomList.value!!.size
        }

        override fun onBindViewHolder(
            holder: SymptomListEditViewHolder,
            position: Int,
        ) {
            holder.itemSymptomName.text = symptomViewModel.symptomList.value!![position].note
            holder.itemSymptomTime.text =
                SymptomUtils.convertDateToTime(symptomViewModel.symptomList.value!![position].dateTime)
            holder.itemSymptomImg.setImageResource(SymptomUtils.getSymptomImg(symptomViewModel.symptomList.value!![position]))

            val isSelected = symptomViewModel.listEditSymptomCheckedStatusMap.value?.get(symptomViewModel.symptomList.value!![position].symptomId)
            if ( isSelected == true) {
                // 이미지뷰를 변경할 조건을 설정
                holder.itemSymptomCheck.setImageResource(R.drawable.all_check_20dp)
            } else {
                holder.itemSymptomCheck.setImageResource(R.drawable.all_un_check_20dp)
            }
        }
    }
}
