package com.project.meongcare.notice.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.project.meongcare.databinding.FragmentNoticeBinding

class NoticeFragment : Fragment() {
    lateinit var fragmentNoticeBinding: FragmentNoticeBinding

    val tabName = arrayOf("공지사항", "이벤트")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        fragmentNoticeBinding = FragmentNoticeBinding.inflate(inflater)

        val noticeViewPagerAdapter = NoticeViewPagerAdapter(this@NoticeFragment)

        fragmentNoticeBinding.run {
            viewpagerNotice.adapter = noticeViewPagerAdapter

            TabLayoutMediator(tablayoutNotice, viewpagerNotice) { tab: TabLayout.Tab, i: Int ->
                tab.text = tabName[i]
                viewpagerNotice.currentItem = tab.position
            }.attach()

            tablayoutNotice.addOnTabSelectedListener(
                object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) { }

                    override fun onTabUnselected(tab: TabLayout.Tab?) { }

                    override fun onTabReselected(tab: TabLayout.Tab?) { }
                },
            )

            imageviewNoticeBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }

        return fragmentNoticeBinding.root
    }
}

class NoticeViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> NoticeTabNoticeFragment()
            else -> NoticeTabEventFragment()
        }
    }
}
