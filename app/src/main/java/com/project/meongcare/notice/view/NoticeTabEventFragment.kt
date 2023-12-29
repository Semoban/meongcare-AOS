package com.project.meongcare.notice.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.project.meongcare.MainActivity
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentNoticeTabEventBinding
import com.project.meongcare.notice.viewmodel.NoticeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoticeTabEventFragment : Fragment() {
    lateinit var fragmentNoticeTabEventBinding: FragmentNoticeTabEventBinding
    lateinit var mainActivity: MainActivity

    private val noticeViewModel: NoticeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        fragmentNoticeTabEventBinding = FragmentNoticeTabEventBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        noticeViewModel.noticeList.observe(viewLifecycleOwner) { noticeResponse ->
            if (noticeResponse != null) {
                val adapter = fragmentNoticeTabEventBinding.recyclerviewNoticeTabEvent.adapter as EventAdapter
                adapter.updateEventList(noticeResponse.records)
            }
        }

        fragmentNoticeTabEventBinding.run {
            noticeViewModel.getNoticeList("event")

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
