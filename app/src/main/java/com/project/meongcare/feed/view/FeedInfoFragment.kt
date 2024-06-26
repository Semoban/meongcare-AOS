package com.project.meongcare.feed.view

import android.os.Bundle
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
import com.project.meongcare.excreta.utils.SUCCESS
import com.project.meongcare.feed.model.entities.FeedDetailGetResponse
import com.project.meongcare.feed.model.utils.FEED_DELETE_FAILURE
import com.project.meongcare.feed.model.utils.FEED_DELETE_SUCCESS
import com.project.meongcare.feed.model.utils.FeedDateUtils.convertDateFormat
import com.project.meongcare.feed.model.utils.FeedInfoUtils.showFailureSnackBar
import com.project.meongcare.feed.model.utils.FeedInfoUtils.showSuccessSnackBar
import com.project.meongcare.feed.viewmodel.FeedDeleteViewModel
import com.project.meongcare.feed.viewmodel.FeedDetailGetViewModel
import com.project.meongcare.feed.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedInfoFragment : Fragment() {
    private var _binding: FragmentFeedInfoBinding? = null
    val binding get() = _binding!!

    private val feedInfoFeedDetailGetViewModel: FeedDetailGetViewModel by viewModels()
    private val feedDeleteViewModel: FeedDeleteViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    private var feedId = 0L
    private var feedRecordId = 0L
    private lateinit var feedInfo: FeedDetailGetResponse
    private var accessToken = ""

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
        userViewModel.fetchAccessToken()
        userViewModel.accessToken.observe(viewLifecycleOwner) { response ->
            accessToken = response
            feedInfoFeedDetailGetViewModel.getFeedDetail(
                accessToken,
                feedId,
                feedRecordId,
            )
        }
        initToolbar()
        fetchFeedInfo()
    }

    private fun fetchFeedInfo() {
        feedInfoFeedDetailGetViewModel.feedDetailGet.observe(viewLifecycleOwner) { response ->
            binding.apply {
                feedInfo =
                    FeedDetailGetResponse(
                        response.brand,
                        response.feedName,
                        response.protein,
                        response.fat,
                        response.crudeAsh,
                        response.moisture,
                        response.etc,
                        response.kcal,
                        response.recommendIntake,
                        response.imageURL,
                        response.startDate,
                        response.endDate,
                    )
                if (!response.imageURL.isNullOrEmpty()) {
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
                textviewFeedinfoEtcRatio.text = feedInfo.etc.toString()
                textviewFeedinfoKcalContent.text = feedInfo.kcal.toString()
                textviewFeedinfoDailyIntakeContent.text = feedInfo.recommendIntake.toString()
                textviewFeedinfoIntakePeriodStart.text = convertDateFormat(feedInfo.startDate)
                if (feedInfo.endDate != null) {
                    textviewFeedinfoIntakePeriodEnd.text = convertDateFormat(feedInfo.endDate)
                } else {
                    textviewFeedinfoIntakePeriodEnd.text = "모름"
                }
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
                    R.id.menu_info_delete -> {
                        binding.apply {
                            includeFeedInfoDeleteDialog.root.visibility = View.VISIBLE
                            includeFeedInfoDeleteDialog.apply {
                                buttonDeleteDialogCancel.setOnClickListener {
                                    includeFeedInfoDeleteDialog.root.visibility = View.GONE
                                }
                                buttonDeleteDialogDelete.setOnClickListener {
                                    deleteFeedInfo()
                                }
                            }
                        }
                    }
                }
                false
            }
        }
    }

    private fun deleteFeedInfo() {
        feedDeleteViewModel.deleteFeed(
            accessToken,
            feedRecordId,
        )
        feedDeleteViewModel.feedDeleted.observe(viewLifecycleOwner) { response ->
            if (response == SUCCESS) {
                findNavController().popBackStack()
                showSuccessSnackBar(
                    requireView(),
                    FEED_DELETE_SUCCESS,
                )
            } else {
                showFailureSnackBar(
                    requireView(),
                    FEED_DELETE_FAILURE,
                )
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
