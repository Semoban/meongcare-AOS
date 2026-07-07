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
import com.project.meongcare.databinding.FragmentOldFeedBinding
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.feed.viewmodel.DogViewModel
import com.project.meongcare.feed.viewmodel.PreviousFeedGetViewModel
import com.project.meongcare.feed.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OldFeedFragment : Fragment() {
    private var _binding: FragmentOldFeedBinding? = null
    private val binding get() = _binding!!

    private val previousFeedGetViewModel: PreviousFeedGetViewModel by viewModels()
    private val dogViewModel: DogViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    private var dogId = 0L
    private var accessToken = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentOldFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initComposeView()
        fetchPreviousFeed()
    }

    private fun initComposeView() {
        binding.composeViewOldFeed.run {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SemobanTheme {
                    val previousFeeds by previousFeedGetViewModel.previousFeedGet.observeAsState()

                    OldFeedScreen(
                        feedRecords = previousFeeds?.feedRecords.orEmpty(),
                        onBackClick = { findNavController().popBackStack() },
                        onItemClick = ::navigateToFeedInfo,
                    )
                }
            }
        }
    }

    private fun fetchPreviousFeed() {
        val feedRecordId = getFeedRecordId()
        dogViewModel.fetchDogId()
        dogViewModel.dogId.observe(viewLifecycleOwner) { response ->
            dogId = response
        }
        userViewModel.fetchAccessToken()
        userViewModel.accessToken.observe(viewLifecycleOwner) { response ->
            accessToken = response
            previousFeedGetViewModel.getPreviousFeed(
                accessToken,
                dogId,
                feedRecordId,
            )
        }
    }

    private fun navigateToFeedInfo(
        feedId: Long,
        feedRecordId: Long,
    ) {
        val bundle = Bundle()
        bundle.putLong("feedId", feedId)
        bundle.putLong("feedRecordId", feedRecordId)
        findNavController().navigate(R.id.action_oldFeedFragment_to_feedInfoFragment, bundle)
    }

    private fun getFeedRecordId() = arguments?.getLong("feedRecordId")!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
