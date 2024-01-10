package com.project.meongcare.feed.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentFeedInfoBinding
import com.project.meongcare.feed.viewmodel.FeedDetailGetViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedInfoFragment : Fragment() {
    private var _binding: FragmentFeedInfoBinding? = null
    private val binding get() = _binding!!

    private val feedInfoFeedDetailGetViewModel: FeedDetailGetViewModel by viewModels()

    private var feedId = 0L
    private var feedRecordId = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFeedInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        feedId = getFeedId()
        feedRecordId = getFeedRecordId()
        feedInfoFeedDetailGetViewModel.getFeedDetail(
            feedId,
            feedRecordId
        )
        initToolbar()
        fetchFeedInfo()
    }

    private fun fetchFeedInfo() {
        feedInfoFeedDetailGetViewModel.feedDetailGet.observe(viewLifecycleOwner) { response ->
            binding.apply {
                if (response.imageURL.isNotEmpty()) {
                    Glide.with(this@FeedInfoFragment)
                        .load(response.imageURL)
                        .into(imageviewFeedinfo)
                }
                textviewFeedinfoBrandContent.text = response.brand
                textviewFeedinfoNameContent.text = response.feedName
                textviewFeedinfoCrudeProteinRatio.text = response.protein.toString()
                textviewFeedinfoCrudeFatRatio.text = response.fat.toString()
                textviewFeedinfoMoistureRatio.text = response.moisture.toString()
                textviewFeedinfoCrudeAshRatio.text = response.crudeAsh.toString()
                textviewFeedinfoKcalContent.text = response.kcal.toString()
                textviewFeedinfoDailyIntakeContent.text = response.recommendIntake.toString()
                // 형식 변환 필요 2024-01-10 -> 2024년 01월 10일
                textviewFeedinfoIntakePeriodStart.text = response.startDate
                textviewFeedinfoIntakePeriodEnd.text = response.endDate
            }
        }
    }

    private fun initToolbar() {
        binding.toolbarFeedinfo.apply {
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            setOnMenuItemClickListener { menu ->
                when (menu.itemId) {
                    R.id.menu_info_edit -> findNavController().navigate(R.id.action_feedInfoFragment_to_feedAddFragment)
                    R.id.menu_info_delete -> Log.d("사료 정보 삭제", "사료 정보 삭제")
                }
                false
            }
        }
    }

    private fun getFeedId() = arguments?.getLong("feedId")!!

    private fun getFeedRecordId() = arguments?.getLong("feedRecordId")!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
