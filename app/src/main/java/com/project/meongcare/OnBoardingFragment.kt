package com.project.meongcare

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.Tab
import com.google.android.material.tabs.TabLayoutMediator
import com.project.meongcare.databinding.FragmentOnBoardingBinding

class OnBoardingFragment : Fragment() {
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val fragmentOnBoardingBinding = FragmentOnBoardingBinding.inflate(inflater)
        viewPagerAdapter = ViewPagerAdapter(this)

        fragmentOnBoardingBinding.run {
            viewPagerOnBoarding.adapter = viewPagerAdapter

            TabLayoutMediator(tabLayoutOnBoarding, viewPagerOnBoarding) { tab: Tab, i: Int ->
                viewPagerOnBoarding.currentItem = tab.position
            }.attach()

            tabLayoutOnBoarding.addOnTabSelectedListener(
                object : TabLayout.OnTabSelectedListener
                {
                    override fun onTabSelected(tab: Tab?) {
                        buttonOnBoardingStart.visibility = when (tab!!.position) {
                            2 -> View.VISIBLE
                            else -> View.INVISIBLE
                        }
                    }

                    override fun onTabUnselected(tab: Tab?) {}
                    override fun onTabReselected(tab: Tab?) {}
                }
            )
        }

        return fragmentOnBoardingBinding.root
    }
}

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FirstOnBoardingFragment()
            1 -> SecondOnBoardingFragment()
            else -> ThirdOnBoardingFragment()
        }
    }
}
