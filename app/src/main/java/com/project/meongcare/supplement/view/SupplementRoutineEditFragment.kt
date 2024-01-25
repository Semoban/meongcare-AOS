package com.project.meongcare.supplement.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.meongcare.MainActivity
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentSupplementRoutineEditBinding
import com.project.meongcare.databinding.ItemSupplementRoutineEditBinding
import com.project.meongcare.snackbar.view.CustomSnackBar
import com.project.meongcare.supplement.model.data.repository.SupplementRepository
import com.project.meongcare.supplement.viewmodel.SupplementViewModel
import com.project.meongcare.supplement.viewmodel.SupplementViewModelFactory

class SupplementRoutineEditFragment : Fragment() {
    lateinit var fragmentSupplementRoutineEditBinding: FragmentSupplementRoutineEditBinding
    lateinit var mainActivity: MainActivity
    lateinit var navController: NavController
    lateinit var supplementViewModel: SupplementViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mainActivity = activity as MainActivity
        fragmentSupplementRoutineEditBinding =
            FragmentSupplementRoutineEditBinding.inflate(layoutInflater)

        navController = findNavController()

        val factory = SupplementViewModelFactory(SupplementRepository())
        supplementViewModel = ViewModelProvider(this, factory)[SupplementViewModel::class.java]

        supplementViewModel.run {
            getSupplementDogs()
            supplementIdList.observe(viewLifecycleOwner) {
                Log.d("영양제 리스트", supplementIdList.value.toString())
                fragmentSupplementRoutineEditBinding.run {
                    imageViewSupplementRoutineEditDeleteAllCheck.isSelected = it.isNotEmpty() && it.size == supplementDogList.value!!.size
                }
            }
            supplementDogList.observe(viewLifecycleOwner) {
                Log.d("영양제 리스트 확인", it.toString())
                fragmentSupplementRoutineEditBinding.run {
                    val temp =
                        supplementViewModel.supplementDogList.value!!.map { it.supplementsId }.toMutableList()
                    Log.d("영양제 리스트 확인2", temp.toString())
                    imageViewSupplementRoutineEditDeleteAllCheck.run {
                        setOnClickListener {
                            it.isSelected = !it.isSelected
                            Log.d("영양제 리스트1", it.isSelected.toString())
                            if (it.isSelected) {
                                supplementViewModel.supplementIdList.value = temp
                                Log.d("영양제 리스트2", supplementViewModel.supplementIdList.value.toString())
                            } else {
                                supplementViewModel.supplementIdList.value = mutableListOf()
                                Log.d("영양제 리스트3", supplementViewModel.supplementIdList.value.toString())
                            }
                        }
                    }
                    recyclerViewSupplementRoutineEdit.run {
                        adapter = SupplementRoutineEditRecyclerViewAdapter()
                        layoutManager = LinearLayoutManager(context)
                    }
                }
            }
            supplementDeleteCode.observe(viewLifecycleOwner) {
                if (it == 200) {
                    showSuccessSnackbar()
                    findNavController().popBackStack()
                } else {
                    showFailSnackbar()
                }
            }
        }

        fragmentSupplementRoutineEditBinding.run {
            toolbarSupplementRoutineEdit.setNavigationOnClickListener { navController.popBackStack() }

            val temp =
                supplementViewModel.supplementDogList.value!!.map { it.supplementsId }.toMutableList()
            Log.d("영양제 리스트 확인2", temp.toString())
            imageViewSupplementRoutineEditDeleteAllCheck.run {
                setOnClickListener {
                    it.isSelected = !it.isSelected
                    Log.d("영양제 리스트1", it.isSelected.toString())
                    if (it.isSelected) {
                        supplementViewModel.supplementIdList.value = temp
                        Log.d("영양제 리스트2", supplementViewModel.supplementIdList.value.toString())
                    } else {
                        supplementViewModel.supplementIdList.value = mutableListOf()
                        Log.d("영양제 리스트3", supplementViewModel.supplementIdList.value.toString())
                    }
                }
            }

            buttonSupplementRoutineEditCancel.setOnClickListener {
                navController.popBackStack()
            }

            buttonSupplementRoutineEditComplete.setOnClickListener {
                includeSupplementRoutineEditDeleteDialog.run {
                    root.visibility = View.VISIBLE
                    buttonDeleteDialogCancel.setOnClickListener {
                        includeSupplementRoutineEditDeleteDialog.root.visibility = View.GONE
                    }
                    buttonDeleteDialogDelete.setOnClickListener {
                        supplementViewModel.deleteSupplements(supplementViewModel.supplementIdList.value!!.toIntArray())
                    }
                }
            }
        }
        return fragmentSupplementRoutineEditBinding.root
    }

    private fun showSuccessSnackbar() {
        CustomSnackBar.make(
            requireView(),
            R.drawable.snackbar_success_16dp,
            "삭제가 완료되었습니다.",
        ).show()
    }

    private fun showFailSnackbar() {
        CustomSnackBar.make(
            requireView(),
            R.drawable.snackbar_error_16dp,
            "삭제에 실패하였습니다.\n잠시 후 다시 시도해주세요",
        ).show()
    }

    inner class SupplementRoutineEditRecyclerViewAdapter :
        RecyclerView.Adapter<SupplementRoutineEditRecyclerViewAdapter.SupplementRoutineEditHolder>() {
        inner class SupplementRoutineEditHolder(itemSupplementRoutineEditBinding: ItemSupplementRoutineEditBinding) :
            RecyclerView.ViewHolder(itemSupplementRoutineEditBinding.root) {
            val itemSupplementRoutineEditName: TextView
            val itemSupplementRoutineEditCheckImg: ImageView
            val itemSupplementRoutineEditAlarm: ImageView

            init {
                itemSupplementRoutineEditName =
                    itemSupplementRoutineEditBinding.textViewItemSupplementRoutineEditName
                itemSupplementRoutineEditCheckImg =
                    itemSupplementRoutineEditBinding.imageViewItemSupplementRoutineEditCheck
                itemSupplementRoutineEditAlarm =
                    itemSupplementRoutineEditBinding.imageViewItemSupplementRoutineEditAlarm
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int,
        ): SupplementRoutineEditHolder {
            val itemSupplementRoutineEditBinding =
                ItemSupplementRoutineEditBinding.inflate(layoutInflater)
            val allViewHolder = SupplementRoutineEditHolder(itemSupplementRoutineEditBinding)

            itemSupplementRoutineEditBinding.root.layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )

            return allViewHolder
        }

        override fun getItemCount(): Int {
            return supplementViewModel.supplementDogList.value!!.size
        }

        override fun onBindViewHolder(
            holder: SupplementRoutineEditHolder,
            position: Int,
        ) {
            Log.d("루틴 편집20", supplementViewModel.supplementDogList.value!!.toString())
            holder.itemSupplementRoutineEditName.text =
                supplementViewModel.supplementDogList.value!![position].name

            val supplementsId =
                supplementViewModel.supplementDogList.value!![position].supplementsId

            supplementViewModel.supplementIdList.observe(viewLifecycleOwner) {
                holder.itemSupplementRoutineEditCheckImg.isSelected =
                    supplementViewModel.supplementIdList.value!!.contains(supplementsId)
            }

            holder.itemSupplementRoutineEditCheckImg.setOnClickListener {
                it.isSelected = !it.isSelected
                val temp2 = supplementViewModel.supplementIdList.value!!
                if (temp2.contains(supplementsId)) {
                    temp2.remove(supplementsId)
                    supplementViewModel.supplementIdList.value = temp2
                } else {
                    temp2.add(supplementsId)
                    supplementViewModel.supplementIdList.value = temp2
                }
            }

            holder.itemSupplementRoutineEditName.setOnClickListener {
                val bundle = Bundle()
                bundle.putInt("supplementsId", supplementsId)
                navController.navigate(R.id.action_supplementRoutineEdit_to_supplementInfo, bundle)
            }

            holder.itemSupplementRoutineEditAlarm.isSelected =
                !supplementViewModel.supplementDogList.value!![position].pushAgreement

            holder.itemSupplementRoutineEditAlarm.setOnClickListener {
                val supplementsId =
                    supplementViewModel.supplementDogList.value!![position].supplementsId
                it.isSelected = !it.isSelected
                val isAlarmTrue = it.isSelected
                supplementViewModel.patchSupplementAlarm(supplementsId, !isAlarmTrue)
            }
        }
    }
}
