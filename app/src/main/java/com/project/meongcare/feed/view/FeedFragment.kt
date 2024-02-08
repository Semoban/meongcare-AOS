package com.project.meongcare.feed.view

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentFeedBinding
import com.project.meongcare.databinding.LayoutFeedNutrientBinding
import com.project.meongcare.databinding.LayoutFeedStopDialogBinding
import com.project.meongcare.excreta.utils.SUCCESS
import com.project.meongcare.feed.model.entities.FeedGetResponse
import com.project.meongcare.feed.model.utils.FEED_STOP_FAILURE
import com.project.meongcare.feed.model.utils.FEED_STOP_SUCCESS
import com.project.meongcare.feed.model.utils.FeedInfoUtils.showFailureSnackBar
import com.project.meongcare.feed.model.utils.FeedInfoUtils.showSuccessSnackBar
import com.project.meongcare.feed.viewmodel.DogViewModel
import com.project.meongcare.feed.viewmodel.FeedGetViewModel
import com.project.meongcare.feed.viewmodel.FeedPartGetViewModel
import com.project.meongcare.feed.viewmodel.FeedStopViewModel
import com.project.meongcare.feed.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class FeedFragment : Fragment() {
    private var _binding: FragmentFeedBinding? = null
    val binding get() = _binding!!

    private lateinit var dialog: Dialog
    private lateinit var dialogBinding: LayoutFeedStopDialogBinding

    private val feedGetViewModel: FeedGetViewModel by viewModels()
    private val feedPartGetViewModel: FeedPartGetViewModel by viewModels()
    private val feedStopViewModel: FeedStopViewModel by viewModels()

    private val dogViewModel: DogViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    private lateinit var feedPartAdapter: FeedPartAdapter
    private lateinit var feedGetResponse: FeedGetResponse

    private var dogId = 0L
    private var accessToken = ""
    private var feedItemCount = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        dialogBinding = LayoutFeedStopDialogBinding.inflate((LayoutInflater.from(requireContext())))
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        dogViewModel.fetchDogId()
        dogViewModel.dogId.observe(viewLifecycleOwner) { response ->
            dogId = response
        }
        userViewModel.fetchAccessToken()
        userViewModel.accessToken.observe(viewLifecycleOwner) { response ->
            accessToken = response
            feedGetViewModel.getFeed(
                accessToken,
                dogId,
            )
        }

        feedPartAdapter = FeedPartAdapter()
        feedGetViewModel.feedGet.observe(viewLifecycleOwner) { response ->
            feedGetResponse = response
            if (feedGetResponse.brand == null) {
                updateVisibilityForEmptyFeed()
            } else {
                updateVisibilityForFeedExist(feedGetResponse.feedId)
                initFeedInfo(feedGetResponse.brand!!, feedGetResponse.feedName!!)
                initNutrientPieChart(
                    feedGetResponse.protein,
                    feedGetResponse.fat,
                    feedGetResponse.crudeAsh,
                    feedGetResponse.moisture,
                    feedGetResponse.etc,
                )
                initIntakePeriod(feedGetResponse.days)
                initDailyRecommendIntake(feedGetResponse.recommendIntake)
            }
            fetchPreviousFeedPart(feedGetResponse.feedRecordId)
            initNutrientTable(feedGetResponse)
            initOldFeedSeeMoreButton(feedGetResponse.feedRecordId)
        }
        initOldFeedPartRecyclerView()
        initChangeButton()
        initFeedStopButton()
        initFeedStopDialog()
    }

    private fun updateVisibilityForEmptyFeed() {
        binding.apply {
            imageviewFeedBowlIllustration.visibility = View.VISIBLE
            buttonFeedInputGuide.visibility = View.VISIBLE
            textviewFeedBrand.visibility = View.GONE
            textviewFeedName.visibility = View.GONE
            piechartFeedNutrient.visibility = View.GONE
            textviewFeedSuspend.visibility = View.GONE
            dividerFeedSuspend.visibility = View.GONE
            buttonFeedChange.visibility = View.GONE
        }
    }

    private fun updateVisibilityForFeedExist(feedId: Long) {
        binding.apply {
            if (feedId != 0L) {
                imageviewFeedBowlIllustration.visibility = View.GONE
                piechartFeedNutrient.visibility = View.VISIBLE
                buttonFeedInputGuide.visibility = View.GONE
                textviewFeedSuspend.visibility = View.VISIBLE
                dividerFeedSuspend.visibility = View.VISIBLE
                buttonFeedChange.visibility = View.VISIBLE
            }
        }
    }

    private fun initFeedStopButton() {
        binding.textviewFeedSuspend.setOnClickListener {
            dialog = Dialog(requireContext(), R.style.CustomDialogTheme)
            val view = dialogBinding.root

            if(view.parent != null){
                ((view.parent) as ViewGroup).removeView(view)
            }

            dialog.setContentView(dialogBinding.root)
            dialog.show()
        }
    }

    private fun initFeedStopDialog() {
        dialogBinding.apply {
            buttonFeedstopdialogCancel.setOnClickListener {
                dialog.dismiss()
            }
            buttonFeedstopdialogCheck.setOnClickListener {
                stopFeed()
            }
        }
    }

    private fun stopFeed() {
        feedStopViewModel.stopFeed(
            accessToken,
            feedGetResponse.feedRecordId,
        )
        feedStopViewModel.feedStopped.observe(viewLifecycleOwner) { code ->
            if (code == SUCCESS) {
                dialog.dismiss()
                showSuccessSnackBar(
                    requireView(),
                    FEED_STOP_SUCCESS,
                )
                feedGetViewModel.getFeed(
                    accessToken,
                    dogId,
                )
                feedGetViewModel.feedGet.observe(viewLifecycleOwner) { response ->
                    feedGetResponse = response
                    updateVisibilityForEmptyFeed()
                    fetchPreviousFeedPart(feedGetResponse.feedRecordId)
                    initIntakePeriod(feedGetResponse.days)
                    initDailyRecommendIntake(feedGetResponse.recommendIntake)
                    initOldFeedPartRecyclerView()
                }
            } else {
                dialog.dismiss()
                showFailureSnackBar(
                    requireView(),
                    FEED_STOP_FAILURE,
                )
            }
        }
    }

    private fun initFeedAddButton() {
        binding.buttonFeedInputGuide.apply {
            if (feedItemCount == 0) {
                setOnClickListener {
                    findNavController().navigate(R.id.action_feedFragment_to_feedAddFragment)
                }
            } else {
                text = "사료를 선택해주세요"
                setOnClickListener {
                    findNavController().navigate(R.id.action_feedFragment_to_searchFeedFragment)
                }
            }
        }
    }

    private fun initFeedInfo(
        brand: String,
        feedName: String,
    ) {
        binding.apply {
            textviewFeedBrand.apply {
                visibility = View.VISIBLE
                text = brand
            }
            textviewFeedName.apply {
                visibility = View.VISIBLE
                text = feedName
            }
        }
    }

    private fun initNutrientPieChart(
        protein: Double,
        fat: Double,
        crudeAsh: Double,
        moisture: Double,
        etc: Double,
    ) {
        val nutrientRatio =
            listOf(
                PieEntry(protein.toFloat()),
                PieEntry(fat.toFloat()),
                PieEntry(crudeAsh.toFloat()),
                PieEntry(moisture.toFloat()),
                PieEntry(etc.toFloat()),
            )

        val pieColors =
            listOf(
                ContextCompat.getColor(requireContext(), R.color.main3),
                ContextCompat.getColor(requireContext(), R.color.sub7),
                ContextCompat.getColor(requireContext(), R.color.sub6),
                ContextCompat.getColor(requireContext(), R.color.sub8),
                ContextCompat.getColor(requireContext(), R.color.sub5),
            )

        val dataSet = PieDataSet(nutrientRatio, "")
        dataSet.colors = pieColors
        dataSet.setDrawValues(false)

        binding.piechartFeedNutrient.apply {
            data = PieData(dataSet)
            description.isEnabled = false
            legend.isEnabled = false
            isRotationEnabled = true
            holeRadius = 60f
            setTouchEnabled(false)
            animateY(1200, Easing.EaseInOutCubic)
            animate()
        }
    }

    private fun initNutrientTable(feedInfo: FeedGetResponse) {
        binding.run {
            initNutrientRow(
                includeFeedNutrientCrudeProtein,
                R.drawable.feed_rect_crude_protein_r5,
                "조단백",
                feedInfo.protein,
            )
            initNutrientRow(
                includeFeedNutrientCrudeFat,
                R.drawable.feed_rect_crude_fat_r5,
                "조지방",
                feedInfo.fat,
            )
            initNutrientRow(
                includeFeedNutrientCrudeAsh,
                R.drawable.feed_rect_crude_ash_r5,
                "조회분",
                feedInfo.crudeAsh,
            )
            initNutrientRow(
                includeFeedNutrientMoisture,
                R.drawable.feed_rect_moisture_r5,
                "수분",
                feedInfo.moisture,
            )
            initNutrientRow(
                includeFeedNutrientEtc,
                R.drawable.feed_rect_etc_r5,
                "기타",
                feedInfo.etc,
            )
        }
    }

    private fun initNutrientRow(
        nutrientRow: LayoutFeedNutrientBinding,
        nutrientColorLabel: Int,
        nutrientType: String,
        nutrientPercentage: Double,
    ) {
        nutrientRow.apply {
            viewFeedNutrientColorLabel.setBackgroundResource(nutrientColorLabel)
            textviewFeedNutrientType.text = nutrientType
            textviewFeedNutrientPercentage.text = convertNutrientPercentage(nutrientPercentage.roundToInt())
        }
    }

    private fun initIntakePeriod(days: Long) {
        binding.textviewFeedIntakePeriodContent.text = convertIntakePeriod(days)
    }

    private fun initDailyRecommendIntake(recommendIntake: Int) {
        binding.textviewFeedDailyIntakeContent.text = convertDailyRecommendIntake(recommendIntake)
    }

    private fun fetchPreviousFeedPart(
        feedRecordId: Long,
    ) {
        feedPartGetViewModel.getFeedPart(
            accessToken,
            dogId,
            feedRecordId,
        )
        feedPartGetViewModel.feedPartGet.observe(viewLifecycleOwner) { response ->
            feedPartAdapter.submitList(response.feedPartRecords)
            feedItemCount = feedPartAdapter.itemCount
            initFeedAddButton()
        }
    }

    private fun initOldFeedSeeMoreButton(feedRecordId: Long) {
        binding.textviewFeedOldFeedSeeMore.setOnClickListener {
            val bundle = Bundle()
            bundle.putLong("feedRecordId", feedRecordId)
            findNavController().navigate(R.id.action_feedFragment_to_oldFeedFragment, bundle)
        }
    }

    private fun initOldFeedPartRecyclerView() {
        binding.recyclerviewFeedOldfeed.run {
            adapter = feedPartAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun initChangeButton() {
        binding.buttonFeedChange.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_searchFeedFragment)
        }
    }

    private fun convertNutrientPercentage(nutrientPercentage: Int) = String.format("%d%%", nutrientPercentage)

    private fun convertIntakePeriod(days: Long) = String.format("%d일", days)

    private fun convertDailyRecommendIntake(recommendIntake: Int) = String.format("%dg", recommendIntake)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
