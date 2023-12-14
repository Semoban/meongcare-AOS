package com.project.meongcare.toolbar.view

import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.project.meongcare.MainActivity
import com.project.meongcare.R
import com.project.meongcare.databinding.ItemToolbarCalendarWeekBinding
import com.project.meongcare.toolbar.viewmodel.ToolbarViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ToolbarDateRecyclerViewAdapter(val mainActivity: MainActivity) :
    RecyclerView.Adapter<ToolbarDateRecyclerViewAdapter.SymptomDateViewHolder>() {
    private val toolbarViewModel = ViewModelProvider(mainActivity)[ToolbarViewModel::class.java]

    inner class SymptomDateViewHolder(itemSymptomDateBinding: ItemToolbarCalendarWeekBinding) :
        RecyclerView.ViewHolder(itemSymptomDateBinding.root) {
        val itemSymptomDate: TextView
        val itemSymptomDay: TextView
        val itemLayout: LinearLayout

        init {
            itemSymptomDate = itemSymptomDateBinding.tvDateCalendarItem
            itemSymptomDay = itemSymptomDateBinding.tvDayCalendarItem
            itemLayout = itemSymptomDateBinding.clCalendarItem
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SymptomDateViewHolder {
        val itemSymptomDateBinding = ItemToolbarCalendarWeekBinding.inflate(mainActivity.layoutInflater)
        val symptomDateHolder = SymptomDateViewHolder(itemSymptomDateBinding)

        itemSymptomDateBinding.root.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )

        return symptomDateHolder
    }

    override fun getItemCount(): Int {
        return toolbarViewModel.dateList.value!!.size
    }

    override fun onBindViewHolder(
        holder: SymptomDateViewHolder,
        position: Int,
    ) {
        holder.itemSymptomDate.text = getDate(getItem(position))
        holder.itemSymptomDay.text = getDay(getItem(position))

        toolbarViewModel.selectDatePosition.observe(mainActivity) {
            // 클릭한 아이템에 대한 레이아웃을 적용
            if (position == it) {
                holder.itemLayout.setBackgroundResource(R.drawable.toolbar_rect_main1_r10)
                holder.itemSymptomDate.setTextColor(
                    ContextCompat.getColor(
                        mainActivity,
                        R.color.main4,
                    ),
                )
                holder.itemSymptomDay.setTextColor(
                    ContextCompat.getColor(
                        mainActivity,
                        R.color.main4,
                    ),
                )
            } else {
                // 클릭하지 않은 아이템에 대한 레이아웃을 적용
                holder.itemLayout.setBackgroundResource(R.drawable.toolbar_rect_white_r10)
                holder.itemSymptomDate.setTextColor(
                    ContextCompat.getColor(
                        mainActivity,
                        R.color.black,
                    ),
                )
                holder.itemSymptomDay.setTextColor(
                    ContextCompat.getColor(
                        mainActivity,
                        R.color.black,
                    ),
                )
            }
        }

        holder.itemLayout.setOnClickListener {
            toolbarViewModel.selectDatePosition.value = position
            toolbarViewModel.selectedDate.value = getItem(position)
        }
    }

    // getItem 함수는 데이터 리스트에서 특정 위치(position)의 아이템을 가져옵니다.
    private fun getItem(position: Int): Date {
//        selectDatePosition = position
        return toolbarViewModel.dateList.value!![position]
    }
}

fun getDay(date: Date): String = SimpleDateFormat("EE", Locale.getDefault()).format(date)

fun getDate(date: Date): String = SimpleDateFormat("d", Locale.getDefault()).format(date)
