package com.project.meongcare.excreta.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentExcretaInfoBinding
import com.project.meongcare.excreta.model.entities.Excreta
import com.project.meongcare.excreta.model.entities.ExcretaDetailGetResponse
import com.project.meongcare.excreta.utils.EXCRETA_DELETE_FAILURE
import com.project.meongcare.excreta.utils.EXCRETA_DELETE_SUCCESS
import com.project.meongcare.excreta.utils.ExcretaDateTimeUtils.convertDateTimeFormat
import com.project.meongcare.excreta.utils.ExcretaDateTimeUtils.convertToTimeFormat
import com.project.meongcare.excreta.utils.ExcretaInfoUtils.showFailureSnackBar
import com.project.meongcare.excreta.utils.ExcretaInfoUtils.showSuccessSnackBar
import com.project.meongcare.excreta.utils.SUCCESS
import com.project.meongcare.excreta.viewmodel.ExcretaDeleteViewModel
import com.project.meongcare.excreta.viewmodel.ExcretaDetailViewModel
import com.project.meongcare.feed.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExcretaInfoFragment : Fragment() {
    private var _binding: FragmentExcretaInfoBinding? = null
    val binding get() = _binding!!

    private val excretaDetailViewModel: ExcretaDetailViewModel by viewModels()
    private val excretaDeleteViewModel: ExcretaDeleteViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    private lateinit var excretaInfo: ExcretaDetailGetResponse
    private var excretaImageURL: String? = ""
    private var excretaDateTime = ""
    private var excretaType = ""
    private var accessToken = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentExcretaInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel.fetchAccessToken()
        userViewModel.accessToken.observe(viewLifecycleOwner) { response ->
            accessToken = response
            fetchExcretaInfo()
        }
        initToolbar()
    }

    private fun initToolbar() {
        binding.toolbarExcretainfo.apply {
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            val excretaId = getExcretaId()
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_info_edit -> {
                        val bundle =
                            bundleOf(
                                "excretaId" to excretaId,
                                "excretaInfo" to excretaInfo,
                            )
                        findNavController().navigate(R.id.action_excretaInfoFragment_to_excretaEditFragment, bundle)
                    }
                    R.id.menu_info_delete -> {
                        excretaDeleteViewModel.apply {
                            deleteExcreta(accessToken, intArrayOf(excretaId.toInt()))
                            excretaDeleted.observe(viewLifecycleOwner) { response ->
                                if (response == SUCCESS) {
                                    showSuccessSnackBar(
                                        requireView(),
                                        EXCRETA_DELETE_SUCCESS,
                                    )
                                    findNavController().popBackStack()
                                } else {
                                    showFailureSnackBar(
                                        requireView(),
                                        EXCRETA_DELETE_FAILURE,
                                    )
                                }
                            }
                        }
                    }
                }
                false
            }
        }
    }

    private fun fetchExcretaInfo() {
        excretaDetailViewModel.apply {
            getExcretaDetail(accessToken, getExcretaId())
            excretaDetailGet.observe(viewLifecycleOwner) { response ->
                excretaImageURL = response.excretaImageURL
                excretaDateTime = response.dateTime
                excretaType = response.excretaType
                excretaInfo =
                    ExcretaDetailGetResponse(
                        excretaImageURL,
                        excretaDateTime,
                        excretaType,
                    )
                initExcretaImage(excretaImageURL)
                binding.textviewExcretainfoDate.text = convertDateTimeFormat(excretaDateTime)
                initExcretaCheckBox(excretaType)
                binding.textviewExcretainfoTime.text = convertToTimeFormat(response.dateTime)
            }
        }
    }

    private fun initExcretaImage(excretaImageURL: String?) {
        binding.apply {
            if (!excretaImageURL.isNullOrEmpty()) {
                cardviewExcretaInfoVisibilityOff.visibility = View.VISIBLE
                imageviewExcretainfoFecesIllustration.visibility = View.INVISIBLE
                Glide.with(this@ExcretaInfoFragment)
                    .load(excretaImageURL)
                    .into(imageviewExcretainfoPicture)
                cardviewExcretaInfoVisibilityOff.setOnClickListener { cardView ->
                    cardView.visibility = View.GONE
                }
                imageviewExcretainfoPicture.setOnClickListener {
                    cardviewExcretaInfoVisibilityOff.visibility = View.VISIBLE
                }
            } else {
                cardviewExcretaInfoVisibilityOff.visibility = View.GONE
                imageviewExcretainfoFecesIllustration.visibility = View.VISIBLE
            }
        }
    }

    private fun initExcretaCheckBox(excretaType: String) {
        binding.apply {
            if (excretaType == Excreta.FECES.toString()) {
                checkboxExcretainfoFeces.isChecked = true
                checkboxExcretainfoUrine.isChecked = false
            } else {
                checkboxExcretainfoUrine.isChecked = true
                checkboxExcretainfoFeces.isChecked = false
            }
        }
    }

    private fun getExcretaId() = arguments?.getLong("excretaId")!!

    override fun onDestroyView() {
        super.onDestroyView()
        binding.apply {
            textviewExcretainfoDate.text = ""
            textviewExcretainfoTime.text = ""
            Glide.with(this@ExcretaInfoFragment)
                .clear(imageviewExcretainfoPicture)
        }
        _binding = null
    }
}
