package com.project.meongcare.excreta.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentExcretaBinding
import com.project.meongcare.excreta.model.entities.Excreta
import com.project.meongcare.excreta.utils.TIME
import com.project.meongcare.excreta.viewmodel.ExcretaRecordViewModel
import com.project.meongcare.feed.viewmodel.DogViewModel
import com.project.meongcare.feed.viewmodel.UserViewModel
import com.project.meongcare.toolbar.viewmodel.ToolbarViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class ExcretaFragment : Fragment() {
    private var _binding: FragmentExcretaBinding? = null
    val binding get() = _binding!!

    private val excretaRecordViewModel: ExcretaRecordViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val dogViewModel: DogViewModel by viewModels()
    lateinit var toolbarViewModel: ToolbarViewModel

    private lateinit var excretaAdapter: ExcretaAdapter
    private var accessToken = ""
    private var dogId = 0L
    private var dateTime = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentExcretaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initDogId()
        initDogName()
        toolbarViewModel = ViewModelProvider(requireActivity())[ToolbarViewModel::class.java]
        toolbarViewModel.selectedDate.observe(viewLifecycleOwner) { date ->
            dateTime = convertSelectedDate(date)
            userViewModel.fetchAccessToken()
        }
        userViewModel.accessToken.observe(viewLifecycleOwner) { response ->
            accessToken = response
            excretaRecordViewModel.getExcretaRecord(dogId, accessToken, dateTime)
            initExcretaRecordRecyclerView()
        }
        fetchExcretaRecord()
        excretaAdapter = ExcretaAdapter()
        initExcretaAddButton()
        initExcretaEditButton()
    }

    private fun initDogId() {
        dogViewModel.fetchDogId()
        dogViewModel.dogId.observe(viewLifecycleOwner) { response ->
            dogId = response
        }
    }

    private fun initDogName() {
        dogViewModel.fetchDogName()
        dogViewModel.dogName.observe(viewLifecycleOwner) { response ->
            binding.textviewExcretaTitleName.text = response
        }
    }

    private fun initExcretaAddButton() {
        binding.textviewExcretaAddbutton.setOnClickListener {
            findNavController().navigate(R.id.action_excretaFragment_to_excretaAddFragment)
        }
    }

    private fun initExcretaEditButton() {
        binding.textviewExcretaEditbutton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("selectedDateTime", dateTime)
            findNavController().navigate(R.id.action_excretaFragment_to_excretaRecordEditFragment, bundle)
        }
    }

    private fun initExcretaRecordRecyclerView() {
        binding.recyclerviewExcretaRecord.run {
            adapter = excretaAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun fetchExcretaRecord() {
        excretaRecordViewModel.excretaRecordGet.observe(viewLifecycleOwner) { response ->
            binding.apply {
                if (response.excretaRecords.isNullOrEmpty()) {
                    textviewExcretaEditbutton.visibility = View.GONE
                } else {
                    textviewExcretaEditbutton.visibility = View.VISIBLE
                }
                textviewExcretaNumberfeces.text = formatExcretaCount(Excreta.FECES.type, response.fecesCount)
                textviewExcretaNumberurine.text = formatExcretaCount(Excreta.URINE.type, response.urineCount)
                excretaAdapter.submitList(response.excretaRecords)
            }
        }
    }

    private fun convertSelectedDate(selectedDate: Date): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        return dateFormat.format(selectedDate.time)
    }

    private fun formatExcretaCount(
        type: String,
        count: Int,
    ) = "$type $count$TIME"

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
