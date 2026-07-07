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
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentFeedInfoBinding
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.excreta.utils.SUCCESS
import com.project.meongcare.feed.model.utils.FEED_DELETE_FAILURE
import com.project.meongcare.feed.model.utils.FEED_DELETE_SUCCESS
import com.project.meongcare.feed.model.utils.FeedInfoUtils.showFailureSnackBar
import com.project.meongcare.feed.model.utils.FeedInfoUtils.showSuccessSnackBar
import com.project.meongcare.feed.viewmodel.FeedDeleteViewModel
import com.project.meongcare.feed.viewmodel.FeedDetailGetViewModel
import com.project.meongcare.feed.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedInfoFragment : Fragment() {
    private var _binding: FragmentFeedInfoBinding? = null
    private val binding get() = _binding!!

    private val feedDetailGetViewModel: FeedDetailGetViewModel by viewModels()
    private val feedDeleteViewModel: FeedDeleteViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    private var feedId = 0L
    private var feedRecordId = 0L
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
        initComposeView()
        fetchFeedInfo()
        observeFeedDeleted()
    }

    private fun initComposeView() {
        binding.composeViewFeedInfo.run {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SemobanTheme {
                    val feedInfo by feedDetailGetViewModel.feedDetailGet.observeAsState()

                    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

                    FeedInfoScreen(
                        feedInfo = feedInfo,
                        onBackClick = { findNavController().popBackStack() },
                        onEditClick = { navigateToFeedEdit() },
                        onDeleteClick = { showDeleteDialog = true },
                    )

                    if (showDeleteDialog) {
                        FeedConfirmDialog(
                            title = "삭제하시겠습니까?",
                            description = "삭제를 누르면 복구할 수 없습니다.",
                            confirmText = "삭제",
                            onCancel = { showDeleteDialog = false },
                            onConfirm = {
                                showDeleteDialog = false
                                deleteFeedInfo()
                            },
                        )
                    }
                }
            }
        }
    }

    private fun fetchFeedInfo() {
        userViewModel.fetchAccessToken()
        userViewModel.accessToken.observe(viewLifecycleOwner) { response ->
            accessToken = response
            feedDetailGetViewModel.getFeedDetail(
                accessToken,
                feedId,
                feedRecordId,
            )
        }
    }

    private fun navigateToFeedEdit() {
        val feedInfo = feedDetailGetViewModel.feedDetailGet.value ?: return
        val bundle =
            bundleOf(
                "feedId" to feedId,
                "feedRecordId" to feedRecordId,
                "feedInfo" to feedInfo,
            )
        findNavController().navigate(R.id.action_feedInfoFragment_to_feedEditFragment, bundle)
    }

    private fun deleteFeedInfo() {
        feedDeleteViewModel.deleteFeed(
            accessToken,
            feedRecordId,
        )
    }

    private fun observeFeedDeleted() {
        feedDeleteViewModel.feedDeleted.observe(viewLifecycleOwner) { response ->
            if (response == null) return@observe
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
