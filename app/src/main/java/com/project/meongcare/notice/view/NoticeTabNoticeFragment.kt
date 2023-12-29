package com.project.meongcare.notice.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.project.meongcare.databinding.FragmentNoticeTabNoticeBinding

class NoticeTabNoticeFragment : Fragment() {
    lateinit var fragmentNoticeTabNoticeBinding: FragmentNoticeTabNoticeBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        fragmentNoticeTabNoticeBinding = FragmentNoticeTabNoticeBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        fragmentNoticeTabNoticeBinding.run {
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
