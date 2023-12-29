package com.project.meongcare.notice.view

import android.content.ClipData.Item
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.project.meongcare.R
import com.project.meongcare.databinding.ItemNoticeBinding
import com.project.meongcare.notice.model.entities.Notice

class EventAdapter(
    private val layoutInflater: LayoutInflater,
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {
    private var eventList: List<Notice> = emptyList()

    fun updateEventList(newList: List<Notice>) {
        this.eventList = newList
        notifyDataSetChanged()
    }

    inner class EventViewHolder(itemNoticeBinding: ItemNoticeBinding) :
        RecyclerView.ViewHolder(itemNoticeBinding.root) {
        var expandableLayout: FrameLayout
        var eventTitle: TextView
        var eventContent: TextView
        var eventNewBadge: ConstraintLayout
        var eventToggle: ImageView
        var eventTime: TextView

        init {
            expandableLayout = itemNoticeBinding.noticeItem
            eventTitle = expandableLayout.findViewById(R.id.textview_notice_title)
            eventContent = itemNoticeBinding.noticeItem.secondLayout.findViewById(R.id.textview_notice_content) //expandableLayout.findViewById(R.id.textview_notice_content)
            eventNewBadge = expandableLayout.findViewById(R.id.constraintlayout_notice_badge)
            eventToggle = expandableLayout.findViewById(R.id.imageview_notice_toggle)
            eventTime = expandableLayout.findViewById(R.id.textview_notice_time)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val itemNoticeBinding = ItemNoticeBinding.inflate(layoutInflater)
        var isExpanded = false

        itemNoticeBinding.root.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )

        itemNoticeBinding.noticeItem.parentLayout.setOnClickListener { noticeHeader ->
            val expandableLayout = itemNoticeBinding.noticeItem
            val eventToggle = noticeHeader.findViewById<ImageView>(R.id.imageview_notice_toggle)
            if (isExpanded) {
                eventToggle.setImageResource(R.drawable.notice_toggle_unchecked)
                expandableLayout.collapse()
                isExpanded = !isExpanded
            } else {
                eventToggle.setImageResource(R.drawable.notice_toggle_checked)
                expandableLayout.expand()
                isExpanded = !isExpanded
            }
        }
        return EventViewHolder(itemNoticeBinding)
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.eventTitle.text = eventList[position].title
        holder.eventContent.text = eventList[position].text

        val dateTime = parseLocalDateTime(eventList[position].lastUpdateTime)
        val hoursDiff = getHoursDifference(dateTime)
        holder.eventTime.text = formatLastUpdateTime(dateTime, hoursDiff)
        holder.eventNewBadge.visibility = setBadgeVisibility(hoursDiff)
    }
}
