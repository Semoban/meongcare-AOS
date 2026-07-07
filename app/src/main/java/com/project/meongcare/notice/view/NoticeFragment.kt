package com.project.meongcare.notice.view

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
import com.project.meongcare.databinding.FragmentNoticeBinding
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.notice.viewmodel.NoticeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoticeFragment : Fragment() {
    private var _binding: FragmentNoticeBinding? = null
    private val binding get() = _binding!!

    private val noticeViewModel: NoticeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentNoticeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initComposeView()
        fetchNoticeList()
    }

    private fun initComposeView() {
        binding.composeViewNotice.run {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SemobanTheme {
                    val noticeResponse by noticeViewModel.noticeList.observeAsState()
                    val eventResponse by noticeViewModel.eventList.observeAsState()

                    NoticeScreen(
                        notices = noticeResponse?.records.orEmpty(),
                        events = eventResponse?.records.orEmpty(),
                        onBackClick = { findNavController().popBackStack() },
                    )
                }
            }
        }
    }

    private fun fetchNoticeList() {
        noticeViewModel.getNoticeList()
        noticeViewModel.getEventList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
