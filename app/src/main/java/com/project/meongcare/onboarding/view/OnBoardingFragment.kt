package com.project.meongcare.onboarding.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.Tab
import com.google.android.material.tabs.TabLayoutMediator
import com.project.meongcare.MainActivity
import com.project.meongcare.databinding.FragmentOnBoardingBinding

class OnBoardingFragment : Fragment() {
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val fragmentOnBoardingBinding = FragmentOnBoardingBinding.inflate(inflater)

        viewPagerAdapter = ViewPagerAdapter(this)

        fragmentOnBoardingBinding.run {
            viewpagerOnboarding.adapter = viewPagerAdapter

            TabLayoutMediator(tablayoutOnboarding, viewpagerOnboarding) { tab: Tab, _ ->
                viewpagerOnboarding.currentItem = tab.position
            }.attach()

            tablayoutOnboarding.addOnTabSelectedListener(
                object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: Tab?) {
                        buttonOnboardingStart.visibility =
                            when (tab!!.position) {
                                2 -> View.VISIBLE
                                else -> View.INVISIBLE
                            }
                    }

                    override fun onTabUnselected(tab: Tab?) {}

                    override fun onTabReselected(tab: Tab?) {}
                },
            )

            textviewOnboardingSkip.setOnClickListener {
                moveToLogin()
            }

            buttonOnboardingStart.setOnClickListener {
                moveToLogin()
            }
        }

        return fragmentOnBoardingBinding.root
    }

    private fun moveToLogin() {
        // LoginFragment로 이동
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
