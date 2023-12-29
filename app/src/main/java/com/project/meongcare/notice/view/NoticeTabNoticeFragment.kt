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
import com.project.meongcare.databinding.FragmentNoticeTabNoticeBinding
import com.project.meongcare.notice.viewmodel.NoticeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoticeTabNoticeFragment : Fragment() {
    lateinit var fragmentNoticeTabNoticeBinding: FragmentNoticeTabNoticeBinding
    lateinit var mainActivity: MainActivity

    private val noticeViewModel: NoticeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        fragmentNoticeTabNoticeBinding = FragmentNoticeTabNoticeBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        noticeViewModel.noticeList.observe(viewLifecycleOwner) { noticeResponse ->
            if (noticeResponse != null) {
                val adapter = fragmentNoticeTabNoticeBinding.recyclerviewNoticeTabNotice.adapter as NoticeAdapter
                adapter.updateNoticeList(noticeResponse.records)
            }
            if (noticeResponse.records.size == 0) {
                fragmentNoticeTabNoticeBinding.recyclerviewNoticeTabNotice.visibility = View.GONE
                fragmentNoticeTabNoticeBinding.linearlayoutNoticeNoList.visibility = View.VISIBLE
            } else {
                fragmentNoticeTabNoticeBinding.recyclerviewNoticeTabNotice.visibility = View.VISIBLE
                fragmentNoticeTabNoticeBinding.linearlayoutNoticeNoList.visibility = View.GONE
            }
        }

        fragmentNoticeTabNoticeBinding.run {
            noticeViewModel.getNoticeList("notice")

            recyclerviewNoticeTabNotice.run {
                adapter = NoticeAdapter(layoutInflater)
                layoutManager = LinearLayoutManager(mainActivity)

                val divider = MaterialDividerItemDecoration(mainActivity, LinearLayoutManager.VERTICAL)
                divider.setDividerColorResource(mainActivity, R.color.gray2)
                addItemDecoration(divider)
            }
        }

        return fragmentNoticeTabNoticeBinding.root
    }
}
