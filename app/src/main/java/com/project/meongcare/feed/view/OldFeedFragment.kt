package com.project.meongcare.feed.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.meongcare.databinding.FragmentOldFeedBinding
import com.project.meongcare.feed.viewmodel.DogViewModel
import com.project.meongcare.feed.viewmodel.PreviousFeedGetViewModel
import com.project.meongcare.feed.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OldFeedFragment : Fragment() {
    private var _binding: FragmentOldFeedBinding? = null
    val binding get() = _binding!!

    private val previousFeedGetViewModel: PreviousFeedGetViewModel by viewModels()
    private val dogViewModel: DogViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var previousFeedAdapter: PreviousFeedAdapter

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
        previousFeedAdapter = PreviousFeedAdapter()
        previousFeedGetViewModel.previousFeedGet.observe(viewLifecycleOwner) { response ->
            if (response.feedRecords.isEmpty()) {
                binding.apply {
                    imageviewOldfeedBowlIllustration.visibility = View.VISIBLE
                    textviewOldfeedMessage.visibility = View.VISIBLE
                }
            }
            previousFeedAdapter.submitList(response.feedRecords)
        }
        initToolbar()
        initPreviousFeedRecyclerView()
    }

    private fun initToolbar() {
        binding.toolbarOldfeed.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initPreviousFeedRecyclerView() {
        binding.recyclerviewOldfeed.run {
            adapter = previousFeedAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun getFeedRecordId() = arguments?.getLong("feedRecordId")!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
