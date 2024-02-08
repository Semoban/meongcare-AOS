package com.project.meongcare.feed.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentSearchFeedBinding
import com.project.meongcare.excreta.utils.SUCCESS
import com.project.meongcare.feed.model.data.local.FeedItemSelectionListener
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
    val binding get() = _binding!!

    private val feedsGetViewModel: FeedsGetViewModel by viewModels()
    private val feedPatchViewModel: FeedPatchViewModel by viewModels()
    private val dogViewModel: DogViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var feedsAdapter: FeedsAdapter

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
        feedsAdapter =
            FeedsAdapter(
                object : FeedItemSelectionListener {
                    override fun onItemSelection(feedId: Long) {
                        patchFeed(feedId)
                    }
                },
            )

        initToolbar()
        initFeedsRecyclerView()
        initDirectInputButton()
        updateSearchResult()
        feedsGetViewModel.feedsGet.observe(viewLifecycleOwner) { response ->
            feedsAdapter.submitList(response.feeds)
        }
    }

    private fun initToolbar() {
        binding.toolbarSearchfeed.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun updateSearchResult() {
        binding.edittextSearchfeedSearch.doAfterTextChanged {
            val searchText = it.toString()
            feedsGetViewModel.filterFeeds(searchText)
        }
    }

    private fun initFeedsRecyclerView() {
        binding.recyclerviewSearchfeedResult.apply {
            adapter = feedsAdapter
            layoutManager = LinearLayoutManager(context)
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
        feedPatchViewModel.feedPatched.observe(viewLifecycleOwner) { response ->
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

    private fun initDirectInputButton() {
        binding.extendedfloatingbuttonSearchfeedDirectInput.setOnClickListener {
            findNavController().navigate(R.id.action_searchFeedFragment_to_feedAddFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
