package com.project.meongcare.onboarding.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentDogVarietySearchBinding
import com.project.meongcare.databinding.LayoutInputDogTypeDialogBinding
import com.project.meongcare.onboarding.model.data.local.DogTypeSelectListener
import com.project.meongcare.onboarding.viewmodel.DogTypeSharedViewModel
import com.project.meongcare.onboarding.viewmodel.DogTypeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DogVarietySearchFragment : Fragment(), DogTypeSelectListener {
    private lateinit var binding: FragmentDogVarietySearchBinding

    private val dogTypeViewModel: DogTypeViewModel by viewModels()
    private val dogTypeSharedViewModel: DogTypeSharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentDogVarietySearchBinding.inflate(inflater)

        dogTypeViewModel.dogTypeList.observe(viewLifecycleOwner) { dogTypes ->
            if (dogTypes != null) {
                val adapter = binding.recyclerViewDogVariety.adapter as DogTypeAdapter
                adapter.updateDogTypeList(dogTypes)
            }
        }

        binding.run {
            imageViewBack.setOnClickListener {
                findNavController().popBackStack()
            }

            imageviewClearText.setOnClickListener {
                editTextDogVariety.setText("")
                clearFocus(editTextDogVariety)
            }

            recyclerViewDogVariety.run {
                adapter = DogTypeAdapter(inflater, this@DogVarietySearchFragment)
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }

            editTextDogVariety.doOnTextChanged { text, start, before, count ->
                val query = text.toString()
                dogTypeViewModel.searchDogType(query)
                imageviewClearText.visibility =
                    if (count > 0) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
            }

            buttonInputDogType.setOnClickListener {
                showInputDogTypeDialog()
            }
        }

        return binding.root
    }

    fun clearFocus(view: View) {
        view.clearFocus()
        val inputMethodManager =
            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDogTypeSelected(dogType: String) {
        dogTypeSharedViewModel.setDogType(dogType)
        findNavController().popBackStack()
    }

    private fun showInputDogTypeDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val dialogBinding = LayoutInputDogTypeDialogBinding.inflate(layoutInflater)
        builder.setView(dialogBinding.root)

        val dialog = builder.create()

        dialogBinding.run {
            editTextDogType.doOnTextChanged { text, start, before, count ->
                imageviewClearDogType.visibility =
                    if (count > 0) {
                        constraintlayoutInput.setBackgroundResource(R.drawable.all_rect_r5)
                        editTextDogType.hint = "강아지 품종을 입력해주세요"
                        editTextDogType.setHintTextColor(requireContext().getColor(R.color.gray4))
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
            }
            imageviewClearDogType.setOnClickListener {
                editTextDogType.setText("")
            }
            buttonDogTypeCancel.setOnClickListener {
                dialog.dismiss()
            }
            buttonDogTypeSubmit.setOnClickListener {
                val dogType = editTextDogType.text.toString()

                if (dogType.isEmpty()) {
                    constraintlayoutInput.setBackgroundResource(R.drawable.all_rect_gray1_r5_outline_sub1)
                    editTextDogType.hint = "강아지 품종을 입력해주세요 !"
                    editTextDogType.setHintTextColor(requireContext().getColor(R.color.sub1))
                    editTextDogType.requestFocus()
                    return@setOnClickListener
                }

                dogTypeSharedViewModel.setDogType(dogType)
                dialog.dismiss()
                findNavController().popBackStack()
            }
        }

        dialog.show()
    }
}
