package com.project.meongcare.supplement.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.project.meongcare.MainActivity
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentSupplementInfoBinding
import com.project.meongcare.supplement.model.data.repository.SupplementRepository
import com.project.meongcare.supplement.model.entities.DetailSupplement
import com.project.meongcare.supplement.view.adapter.SupplementInfoTimeRecyclerViewAdapter
import com.project.meongcare.supplement.viewmodel.SupplementViewModel
import com.project.meongcare.supplement.viewmodel.SupplementViewModelFactory

class SupplementInfoFragment : Fragment() {
    lateinit var fragmentSupplementInfoBinding: FragmentSupplementInfoBinding
    lateinit var mainActivity: MainActivity
    lateinit var supplementViewModel: SupplementViewModel
    lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentSupplementInfoBinding = FragmentSupplementInfoBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        navController = findNavController()
        val supplementId = arguments?.getInt("supplementsId")

        val factory = SupplementViewModelFactory(SupplementRepository())
        supplementViewModel = ViewModelProvider(this, factory)[SupplementViewModel::class.java]

        supplementViewModel.run {
            getSupplementDetail(supplementId!!)
            supplementDetail.observe(viewLifecycleOwner) {
                fragmentSupplementInfoBinding.run {
                    if (!it.imageUrl.isNullOrBlank()) {
                        layoutSupplementInfoDefault.visibility = View.GONE
                        imageViewSupplementInfo.visibility = View.VISIBLE
                        Glide.with(this@SupplementInfoFragment)
                            .load(it.imageUrl)
                            .into(imageViewSupplementInfo)
                        textViewSupplementInfoName.text = it.name
                    }

                    textViewSupplementInfoBrandName.text = it.brand
                    textViewSupplementInfoCycleCount.text = it.intakeCycle.toString()
                    textViewSupplementInfoTimeListCount.text = "${it.intakeInfos.size}회"

                    setButtonSelected(it)

                    recyclerViewSupplementInfoTimeList.run {
                        adapter = SupplementInfoTimeRecyclerViewAdapter(supplementViewModel)
                        layoutManager = LinearLayoutManager(context)
                    }
                }
            }
        }

        fragmentSupplementInfoBinding.run {
            toolbarSupplementInfo.run {
                setNavigationOnClickListener {
                    navController.popBackStack()
                }

                setOnMenuItemClickListener {
                    includeSupplementInfoDeleteDialog.root.visibility = View.VISIBLE
                    includeSupplementInfoDeleteDialog.run {
                        buttonDeleteDialogCancel.setOnClickListener {
                            includeSupplementInfoDeleteDialog.root.visibility = View.GONE
                        }
                        buttonDeleteDialogDelete.setOnClickListener {
                            supplementViewModel.deleteSupplement(supplementId!!, navController)
                        }
                    }

                    false
                }
            }

            // Todo : supplementViewModel.supplementDetail.value.isActive 로 교체
            var temp = true
            buttonSupplementInfoRoutine.run {
                this.isSelected = temp
                val selected = ContextCompat.getColor(context, R.color.white)
                val unselected = ContextCompat.getColor(context, R.color.gray4)

                textViewButtonSupplementInfoRoutine.run {
                    if (isSelected) {
                        setTextColor(unselected)
                        text = "루틴 중단"
                    } else {
                        setTextColor(selected)
                        text = "루틴 시작하기"
                    }
                }
            }

            buttonSupplementInfoRoutine.setOnClickListener {
                temp = !temp
                it.isSelected = !it.isSelected
                val activeStatus = it.isSelected
                supplementViewModel.patchSupplementActive(
                    supplementId!!,
                    activeStatus,
                    mainActivity,
                    textViewButtonSupplementInfoRoutine,
                )
            }
        }

        return fragmentSupplementInfoBinding.root
    }

    private fun FragmentSupplementInfoBinding.setButtonSelected(it: DetailSupplement) {
        val selected = R.drawable.all_rect_main1_r5_outline_main3
        val selectedTextColor = ContextCompat.getColor(mainActivity, R.color.main4)

        when (it.intakeUnit) {
            "mg" -> {
                buttonSupplementInfoUnitMg.setBackgroundResource(selected)
                textViewButtonSupplementInfoUnitMg.setTextColor(selectedTextColor)
            }

            "스쿱" -> {
                buttonSupplementInfoUnitScoop.setBackgroundResource(selected)
                textViewButtonSupplementInfoUnitScoop.setTextColor(selectedTextColor)
            }

            "정" -> {
                buttonSupplementInfoUnitJung.setBackgroundResource(selected)
                textViewButtonSupplementInfoUnitJung.setTextColor(selectedTextColor)
            }
        }
    }
}
