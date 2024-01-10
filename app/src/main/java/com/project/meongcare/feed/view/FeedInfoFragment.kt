package com.project.meongcare.feed.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentFeedInfoBinding
import com.project.meongcare.feed.model.entities.FeedDetailGetResponse
import com.project.meongcare.feed.viewmodel.FeedDetailGetViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedInfoFragment : Fragment() {
    private var _binding: FragmentFeedInfoBinding? = null
    private val binding get() = _binding!!

    private val feedInfoFeedDetailGetViewModel: FeedDetailGetViewModel by viewModels()

    private var feedId = 0L
    private var feedRecordId = 0L
    private lateinit var feedInfo: FeedDetailGetResponse

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
                feedInfo = FeedDetailGetResponse(
                    response.brand,
                    response.feedName,
                    response.protein,
                    response.fat,
                    response.crudeAsh,
                    response.moisture,
                    response.kcal,
                    response.recommendIntake,
                    response.imageURL,
                    response.startDate,
                    response.endDate,
                )
                if (response.imageURL.isNotEmpty()) {
                    Glide.with(this@FeedInfoFragment)
                        .load(response.imageURL)
                        .into(imageviewFeedinfo)
                }
                textviewFeedinfoBrandContent.text = feedInfo.brand
                textviewFeedinfoNameContent.text = feedInfo.feedName
                textviewFeedinfoCrudeProteinRatio.text = feedInfo.protein.toString()
                textviewFeedinfoCrudeFatRatio.text = feedInfo.fat.toString()
                textviewFeedinfoMoistureRatio.text = feedInfo.moisture.toString()
                textviewFeedinfoCrudeAshRatio.text = feedInfo.crudeAsh.toString()
                textviewFeedinfoKcalContent.text = feedInfo.kcal.toString()
                textviewFeedinfoDailyIntakeContent.text = feedInfo.recommendIntake.toString()
                // 형식 변환 필요 2024-01-10 -> 2024년 01월 10일
                textviewFeedinfoIntakePeriodStart.text = feedInfo.startDate
                textviewFeedinfoIntakePeriodEnd.text = feedInfo.endDate
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
                    R.id.menu_info_edit -> {
                        val bundle =
                            bundleOf(
                                "feedId" to feedId,
                                "feedRecordId" to feedRecordId,
                                "feedInfo" to feedInfo,
                            )
                        findNavController().navigate(R.id.action_feedInfoFragment_to_feedEditFragment, bundle)
                    }
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
