package com.project.meongcare.feed.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentSearchFeedBinding
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.excreta.utils.SUCCESS
import com.project.meongcare.feed.model.entities.FeedPatchRequest
import com.project.meongcare.feed.model.utils.FEED_CHANGE_FAILURE
import com.project.meongcare.feed.model.utils.FEED_CHANGE_SUCCESS
import com.project.meongcare.feed.model.utils.FeedInfoUtils.showFailureSnackBar
import com.project.meongcare.feed.model.utils.FeedInfoUtils.showSuccessSnackBar
import com.project.meongcare.feed.viewmodel.DogViewModel
import com.project.meongcare.feed.viewmodel.FeedPatchViewModel
import com.project.meongcare.feed.viewmodel.FeedsGetViewModel
import com.project.meongcare.feed.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFeedFragment : Fragment() {
    private var _binding: FragmentSearchFeedBinding? = null
    private val binding get() = _binding!!

    private val feedsGetViewModel: FeedsGetViewModel by viewModels()
    private val feedPatchViewModel: FeedPatchViewModel by viewModels()
    private val dogViewModel: DogViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    private var dogId = 0L
    private var accessToken = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSearchFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initComposeView()
        fetchFeeds()
        observeFeedPatched()
    }

    private fun initComposeView() {
        binding.composeViewSearchFeed.run {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SemobanTheme {
                    val feeds by feedsGetViewModel.feedsGet.observeAsState()

                    SearchFeedScreen(
                        feeds = feeds?.feeds.orEmpty(),
                        onBackClick = { findNavController().popBackStack() },
                        onSearchChange = { searchText -> feedsGetViewModel.filterFeeds(searchText) },
                        onFeedClick = ::patchFeed,
                        onDirectInputClick = {
                            findNavController().navigate(R.id.action_searchFeedFragment_to_feedAddFragment)
                        },
                    )
                }
            }
        }
    }

    private fun fetchFeeds() {
        dogViewModel.fetchDogId()
        userViewModel.fetchAccessToken()
        dogViewModel.dogId.observe(viewLifecycleOwner) { response ->
            dogId = response
        }
        userViewModel.accessToken.observe(viewLifecycleOwner) { response ->
            accessToken = response
            feedsGetViewModel.getFeeds(
                accessToken,
                dogId,
            )
        }
    }

    private fun patchFeed(newFeedId: Long) {
        val feedPatchRequest =
            FeedPatchRequest(
                dogId,
                newFeedId,
            )
        feedPatchViewModel.patchFeed(
            accessToken,
            feedPatchRequest,
        )
    }

    private fun observeFeedPatched() {
        feedPatchViewModel.feedPatched.observe(viewLifecycleOwner) { response ->
            if (response == null) return@observe
            if (response == SUCCESS) {
                findNavController().popBackStack()
                showSuccessSnackBar(
                    requireView(),
                    FEED_CHANGE_SUCCESS,
                )
            } else {
                showFailureSnackBar(
                    requireView(),
                    FEED_CHANGE_FAILURE,
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
