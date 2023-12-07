package com.project.meongcare

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.project.meongcare.databinding.FragmentNoticeTabEventBinding
import com.project.meongcare.databinding.ItemNoticeBinding

class NoticeTabEventFragment : Fragment() {
    lateinit var fragmentNoticeTabEventBinding: FragmentNoticeTabEventBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        fragmentNoticeTabEventBinding = FragmentNoticeTabEventBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        fragmentNoticeTabEventBinding.run {
            recyclerviewNoticeTabEvent.run {
                adapter = RecyclerAdapterClass()
                layoutManager = LinearLayoutManager(mainActivity)

                val divider = MaterialDividerItemDecoration(mainActivity, LinearLayoutManager.VERTICAL)
                divider.setDividerColorResource(mainActivity, R.color.gray2)
                addItemDecoration(divider)
            }
        }

        return fragmentNoticeTabEventBinding.root
    }

    inner class RecyclerAdapterClass: RecyclerView.Adapter<RecyclerAdapterClass.ViewHolderClass>(){
        inner class ViewHolderClass (itemNoticeBinding: ItemNoticeBinding) : RecyclerView.ViewHolder(itemNoticeBinding.root){
            var expandableLayout : FrameLayout

            init {
                expandableLayout = itemNoticeBinding.noticeItem
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
            val itemNoticeBinding = ItemNoticeBinding.inflate(layoutInflater)
            itemNoticeBinding.root.layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

            var isExpanded = false

            itemNoticeBinding.noticeItem.parentLayout.setOnClickListener { noticeHeader ->
                val expandableLayout = itemNoticeBinding.noticeItem
                if (isExpanded) {
                    noticeHeader.findViewById<ImageView>(R.id.imageViewNoticeContentToggle).setImageResource(R.drawable.notice_toggle_unchecked)
                    noticeHeader.findViewById<TextView>(R.id.textViewNoticeItemTitle).setTextAppearance(R.style.Typography_Body1_Medium)
                    expandableLayout.collapse()
                    isExpanded = !isExpanded
                } else {
                    noticeHeader.findViewById<ImageView>(R.id.imageViewNoticeContentToggle).setImageResource(R.drawable.notice_toggle_checked)
                    noticeHeader.findViewById<TextView>(R.id.textViewNoticeItemTitle).setTextAppearance(R.style.Typography_Title3_SemiBold)
                    expandableLayout.expand()
                    isExpanded = !isExpanded
                }
            }

            return ViewHolderClass(itemNoticeBinding)
        }

        override fun getItemCount(): Int {
            return 2
        }

        override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
            Log.d("position", position.toString())
        }
    }
}
