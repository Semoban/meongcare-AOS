package com.project.meongcare.feed.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentFeedBinding
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.excreta.utils.SUCCESS
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

@AndroidEntryPoint
class FeedFragment : Fragment() {
    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private val feedGetViewModel: FeedGetViewModel by viewModels()
    private val feedPartGetViewModel: FeedPartGetViewModel by viewModels()
    private val feedStopViewModel: FeedStopViewModel by viewModels()

    private val dogViewModel: DogViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    private var dogId = 0L
    private var accessToken = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initComposeView()
        fetchFeed()
        observeFeedStopped()
    }

    private fun initComposeView() {
        binding.composeViewFeed.run {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SemobanTheme {
                    val feed by feedGetViewModel.feedGet.observeAsState()
                    val feedParts by feedPartGetViewModel.feedPartGet.observeAsState()
                    val previousFeeds = feedParts?.feedPartRecords.orEmpty()

                    var showStopDialog by rememberSaveable { mutableStateOf(false) }

                    FeedScreen(
                        feed = feed,
                        previousFeeds = previousFeeds,
                        onInputGuideClick = {
                            if (previousFeeds.isEmpty()) {
                                findNavController().navigate(R.id.action_feedFragment_to_feedAddFragment)
                            } else {
                                findNavController().navigate(R.id.action_feedFragment_to_searchFeedFragment)
                            }
                        },
                        onStopClick = { showStopDialog = true },
                        onSeeMoreClick = { navigateToOldFeed() },
                        onChangeClick = {
                            findNavController().navigate(R.id.action_feedFragment_to_searchFeedFragment)
                        },
                    )

                    if (showStopDialog) {
                        FeedStopDialog(
                            onCancel = { showStopDialog = false },
                            onConfirm = {
                                showStopDialog = false
                                stopFeed()
                            },
                        )
                    }
                }
            }
        }
    }

    private fun fetchFeed() {
        dogViewModel.fetchDogId()
        dogViewModel.dogId.observe(viewLifecycleOwner) { response ->
            dogId = response
        }
        userViewModel.fetchAccessToken()
        userViewModel.accessToken.observe(viewLifecycleOwner) { response ->
            accessToken = response
            feedGetViewModel.getFeed(accessToken, dogId)
        }
        feedGetViewModel.feedGet.observe(viewLifecycleOwner) { response ->
            feedPartGetViewModel.getFeedPart(accessToken, dogId, response.feedRecordId)
        }
    }

    private fun stopFeed() {
        val feedRecordId = feedGetViewModel.feedGet.value?.feedRecordId ?: return
        feedStopViewModel.stopFeed(accessToken, feedRecordId)
    }

    private fun observeFeedStopped() {
        feedStopViewModel.feedStopped.observe(viewLifecycleOwner) { code ->
            if (code == null) return@observe
            if (code == SUCCESS) {
                showSuccessSnackBar(requireView(), FEED_STOP_SUCCESS)
                feedGetViewModel.getFeed(accessToken, dogId)
            } else {
                showFailureSnackBar(requireView(), FEED_STOP_FAILURE)
            }
        }
    }

    private fun navigateToOldFeed() {
        val feedRecordId = feedGetViewModel.feedGet.value?.feedRecordId ?: return
        val bundle = Bundle()
        bundle.putLong("feedRecordId", feedRecordId)
        findNavController().navigate(R.id.action_feedFragment_to_oldFeedFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
