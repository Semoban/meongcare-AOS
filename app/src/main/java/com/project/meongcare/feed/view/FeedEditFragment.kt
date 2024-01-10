package com.project.meongcare.feed.view

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentFeedAddEditBinding
import com.project.meongcare.feed.model.data.local.FeedPhotoListener
import com.project.meongcare.feed.model.entities.FeedDetailGetResponse
import com.project.meongcare.feed.model.utils.FeedDateUtils.convertDateFormat
import com.project.meongcare.feed.viewmodel.FeedPutViewModel

class FeedEditFragment: Fragment(), FeedPhotoListener {
    private var _binding: FragmentFeedAddEditBinding? = null
    private val binding
        get() = _binding!!

    private var feedId = 0L
    private var feedRecordId = 0L
    private lateinit var feedInfo: FeedDetailGetResponse

    private val feedPutViewModel: FeedPutViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFeedAddEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        feedId = getFeedId()
        feedRecordId = getFeedRecordId()
        feedInfo = getFeedInfo()
        initToolbar()
        fetchFeedInfo()
        initPhotoAttachModalBottomSheet()
        updateCalendarVisibility()
    }

    private fun fetchFeedInfo() {
        binding.apply {
            if (feedInfo.imageURL.isNotEmpty()) {
                Glide.with(this@FeedEditFragment)
                    .load(feedInfo.imageURL)
                    .into(imageviewFeedaddeditPicture)
                layoutFeedaddeditImage.root.visibility = View.INVISIBLE
            }
            edittextFeedaddeditBrand.setText(feedInfo.brand)
            edittextFeedaddeditName.setText(feedInfo.feedName)
            edittextFeedaddeditCrudeProteinPercentage.setText(feedInfo.protein.toString())
            edittextFeedaddeditCrudeFatPercent.setText(feedInfo.fat.toString())
            edittextFeedaddeditCrudeAshPercent.setText(feedInfo.crudeAsh.toString())
            edittextFeedaddeditMoisturePercent.setText(feedInfo.moisture.toString())
            edittextFeedaddeditKcalContent.setText(feedInfo.kcal.toString())
            textviewFeedaddeditDailyIntakeContent.text = "${feedInfo.recommendIntake}g"
            textviewFeedaddeditIntakePeriodStart.apply {
                text = convertDateFormat(feedInfo.startDate)
                setTextColor(resources.getColor(R.color.black, null))
            }
            textviewFeedaddeditIntakePeriodEnd.apply {
                text = if (feedInfo.endDate != null) {
                    convertDateFormat(feedInfo.endDate)
                } else {
                    "모름"
                }
                setTextColor(resources.getColor(R.color.black, null))
            }
        }
    }

    private fun initToolbar() {
        binding.toolbarFeedaddedit.apply {
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun initPhotoAttachModalBottomSheet() {
        binding.cardviewFeedaddeditImage.setOnClickListener {
            val photoAttachModalBottomSheet = FeedPhotoAttachModalBottomSheetFragment()
            photoAttachModalBottomSheet.setPhotoListener(this@FeedEditFragment)
            photoAttachModalBottomSheet.show(
                requireActivity().supportFragmentManager,
                FeedPhotoAttachModalBottomSheetFragment.TAG,
            )
        }
    }

    private fun updateCalendarVisibility() {
        binding.apply {
            textviewFeedaddeditIntakePeriodStart.apply {
                setOnClickListener {
                    setTextColor(resources.getColor(R.color.black, null))
                    calendarviewFeedaddeditStartDate.visibility = View.VISIBLE
                    calendarviewFeedaddeditEndDate.visibility = View.GONE
                    checkboxFeedaddeditDoNotKnowEndDate.visibility = View.GONE
                    textviewFeedaddeditDoNotKnowEndDate.visibility = View.GONE
                    textviewFeedaddeditIntakePeriodEnd.setTextColor(resources.getColor(R.color.gray4, null))
                }
            }
            textviewFeedaddeditIntakePeriodEnd.apply {
                setOnClickListener {
                    setTextColor(resources.getColor(R.color.black, null))
                    calendarviewFeedaddeditEndDate.visibility = View.VISIBLE
                    calendarviewFeedaddeditStartDate.visibility = View.INVISIBLE
                    checkboxFeedaddeditDoNotKnowEndDate.visibility = View.VISIBLE
                    textviewFeedaddeditDoNotKnowEndDate.visibility = View.VISIBLE
                    textviewFeedaddeditIntakePeriodStart.setTextColor(resources.getColor(R.color.gray4, null))
                }
            }
        }
    }

    private fun getFeedId() = arguments?.getLong("feedId")!!

    private fun getFeedRecordId() = arguments?.getLong("feedRecordId")!!

    private fun getFeedInfo() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("feedInfo", FeedDetailGetResponse::class.java)!!
        } else {
            arguments?.getParcelable("feedInfo")!!
        }

    override fun onUriPassed(uri: Uri) {
        binding.apply {
            Glide.with(this@FeedEditFragment)
                .load(uri)
                .into(imageviewFeedaddeditPicture)
            layoutFeedaddeditImage.root.visibility = View.INVISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
