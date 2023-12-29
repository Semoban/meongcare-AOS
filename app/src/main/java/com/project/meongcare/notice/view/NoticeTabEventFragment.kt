package com.project.meongcare.notice.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.project.meongcare.databinding.FragmentNoticeTabEventBinding

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
                adapter = EventAdapter(layoutInflater)
                layoutManager = LinearLayoutManager(mainActivity)

                val divider = MaterialDividerItemDecoration(mainActivity, LinearLayoutManager.VERTICAL)
                divider.setDividerColorResource(mainActivity, R.color.gray2)
                addItemDecoration(divider)
            }
        }

        return fragmentNoticeTabEventBinding.root
    }
}
