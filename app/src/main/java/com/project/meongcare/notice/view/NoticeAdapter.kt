package com.project.meongcare.notice.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.project.meongcare.R
import com.project.meongcare.databinding.ItemNoticeBinding
import com.project.meongcare.notice.model.entities.Notice
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class NoticeAdapter(
    private val layoutInflater: LayoutInflater,
) : RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder>() {
    private var noticeList: List<Notice> = emptyList()

    fun updateNoticeList(newList: List<Notice>) {
        this.noticeList = newList
        notifyDataSetChanged()
    }

    inner class NoticeViewHolder(itemNoticeBinding: ItemNoticeBinding) :
        RecyclerView.ViewHolder(itemNoticeBinding.root) {
        var expandableLayout: FrameLayout
        var noticeTitle: TextView
        var noticeContent: TextView
        var noticeNewBadge: ConstraintLayout
        var noticeToggle: ImageView
        var noticeTime: TextView

        init {
            expandableLayout = itemNoticeBinding.noticeItem
            noticeTitle = expandableLayout.findViewById(R.id.textview_notice_title)
            noticeContent = itemNoticeBinding.noticeItem.secondLayout.findViewById(R.id.textview_notice_content) //expandableLayout.findViewById(R.id.textview_notice_content)
            noticeNewBadge = expandableLayout.findViewById(R.id.constraintlayout_notice_badge)
            noticeToggle = expandableLayout.findViewById(R.id.imageview_notice_toggle)
            noticeTime = expandableLayout.findViewById(R.id.textview_notice_time)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): NoticeViewHolder {
        val itemNoticeBinding = ItemNoticeBinding.inflate(layoutInflater)
        var isExpanded = false

        itemNoticeBinding.root.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )

        itemNoticeBinding.noticeItem.parentLayout.setOnClickListener { noticeHeader ->
            val expandableLayout = itemNoticeBinding.noticeItem
            val noticeToggle = noticeHeader.findViewById<ImageView>(R.id.imageview_notice_toggle)

            if (isExpanded) {
                expandableLayout.collapse()
                noticeToggle.setImageResource(R.drawable.notice_toggle_unchecked)
                isExpanded = !isExpanded
            } else {
                expandableLayout.expand()
                noticeToggle.setImageResource(R.drawable.notice_toggle_checked)
                isExpanded = !isExpanded
            }
        }

        return NoticeViewHolder(itemNoticeBinding)
    }

    override fun getItemCount(): Int {
        return noticeList.size
    }

    override fun onBindViewHolder(
        holder: NoticeViewHolder,
        position: Int,
    ) {
        holder.noticeTitle.text = noticeList[position].title
        holder.noticeContent.text = noticeList[position].text

        val dateTime = parseLocalDateTime(noticeList[position].lastUpdateTime)
        val hoursDiff = getHoursDifference(dateTime)
        holder.noticeTime.text = formatLastUpdateTime(dateTime, hoursDiff)
        holder.noticeNewBadge.visibility = setBadgeVisibility(hoursDiff)
    }
}

fun parseLocalDateTime(str: String): LocalDateTime {
    val dateTime = LocalDateTime.parse(str, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    return dateTime
}

fun getHoursDifference(dateTime: LocalDateTime): Long {
    val currentDateTime = LocalDateTime.now()
    val hoursDiff = ChronoUnit.HOURS.between(dateTime, currentDateTime)
    return hoursDiff
}

fun formatLastUpdateTime(
    dateTime: LocalDateTime,
    hoursDiff: Long,
): String {
    val formattedTime = dateTime.format(DateTimeFormatter.ofPattern("a hh:mm"))
    val formattedMonth = dateTime.format(DateTimeFormatter.ofPattern("MM월 dd일"))
    val formattedYear = dateTime.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))

    if (hoursDiff < 24) {
        return "오늘 $formattedTime"
    } else if (hoursDiff in 24 until 48) {
        return "어제 $formattedTime"
    } else {
        if (dateTime.year == LocalDateTime.now().year) {
            return formattedMonth
        }
        return formattedYear
    }
}

fun setBadgeVisibility(hoursDiff: Long): Int {
    if (hoursDiff < 24) {
        return View.VISIBLE
    } else {
        return View.INVISIBLE
    }
}
