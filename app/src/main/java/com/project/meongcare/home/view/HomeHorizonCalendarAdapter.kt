package com.project.meongcare.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.meongcare.R
import com.project.meongcare.databinding.ItemToolbarCalendarWeekBinding
import com.project.meongcare.toolbar.view.getDate
import com.project.meongcare.toolbar.view.getDay
import java.util.Date

class HomeHorizonCalendarAdapter(
    private val layoutInflater: LayoutInflater,
    private val context: Context,
    private val itemClickListener: HorizonCalendarItemClickListener,
) : RecyclerView.Adapter<HomeHorizonCalendarAdapter.CalendarViewHolder>() {
    private var dateList: List<Date> = emptyList()
    private var selectedPos: Int = RecyclerView.NO_POSITION

    fun updateDateList(newList: List<Date>) {
        this.dateList = newList
        notifyDataSetChanged()
    }

    fun updateSelectedPos(newPos: Int) {
        this.selectedPos = newPos
        notifyDataSetChanged()
    }

    inner class CalendarViewHolder(itemCalendarBinding: ItemToolbarCalendarWeekBinding) :
        RecyclerView.ViewHolder(itemCalendarBinding.root) {
        val itemDate: TextView
        val itemDay: TextView
        val itemLayout: LinearLayout

        init {
            itemDate = itemCalendarBinding.tvDateCalendarItem
            itemDay = itemCalendarBinding.tvDayCalendarItem
            itemLayout = itemCalendarBinding.clCalendarItem
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CalendarViewHolder {
        val itemCalendarBinding = ItemToolbarCalendarWeekBinding.inflate(layoutInflater)
        itemCalendarBinding.root.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
        return CalendarViewHolder(itemCalendarBinding)
    }

    override fun getItemCount(): Int {
        return dateList.size
    }

    override fun onBindViewHolder(
        holder: CalendarViewHolder,
        position: Int,
    ) {
        holder.itemDate.text = getDate(dateList[position])
        holder.itemDay.text = getDay(dateList[position])
        holder.itemLayout.setOnClickListener {
            itemClickListener.onItemClick(position)
        }
        updateDateLayout(holder.itemDate, holder.itemDay, holder.itemLayout, position)
    }

    fun updateDateLayout(
        date: TextView,
        day: TextView,
        layout: LinearLayout,
        currentPos: Int,
    ) {
        if (selectedPos == currentPos) {
            date.setTextColor(context.getColor(R.color.main4))
            day.setTextColor(context.getColor(R.color.main4))
            layout.setBackgroundResource(R.drawable.toolbar_rect_main1_r10)
        } else {
            date.setTextColor(context.getColor(R.color.black))
            day.setTextColor(context.getColor(R.color.black))
            layout.setBackgroundResource(R.drawable.toolbar_rect_white_r10)
        }
    }
}
