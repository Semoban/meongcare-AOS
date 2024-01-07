package com.project.meongcare.feed.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentSearchFeedBinding
import com.project.meongcare.feed.viewmodel.FeedsGetViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFeedFragment : Fragment() {
    private var _binding: FragmentSearchFeedBinding? = null
    private val binding get() = _binding!!

    private val feedsGetViewModel: FeedsGetViewModel by viewModels()
    private lateinit var feedsAdapter: FeedsAdapter

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
        feedsAdapter = FeedsAdapter()
        initToolbar()
        initFeedsRecyclerView()
        initDirectInputButton()
        feedsGetViewModel.getFeeds()
        feedsGetViewModel.feedsGet.observe(viewLifecycleOwner) { response ->
            feedsAdapter.submitList(response.feeds)
        }
    }

    private fun initToolbar() {
        binding.toolbarSearchfeed.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initFeedsRecyclerView() {
        binding.recyclerviewSearchfeedResult.apply {
            adapter = feedsAdapter
            layoutManager = LinearLayoutManager(context)
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
