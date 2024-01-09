package com.project.meongcare.supplement.view

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.meongcare.MainActivity
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentSupplementAddBinding
import com.project.meongcare.databinding.ItemSupplementAddTimeBinding
import com.project.meongcare.supplement.model.data.local.OnPictureChangedListener
import com.project.meongcare.supplement.model.data.repository.SupplementRepository
import com.project.meongcare.supplement.model.entities.SupplementDto
import com.project.meongcare.supplement.utils.SupplementUtils.Companion.convertDateToTime
import com.project.meongcare.supplement.utils.SupplementUtils.Companion.hideKeyboard
import com.project.meongcare.supplement.utils.SupplementUtils.Companion.showCycleBottomSheet
import com.project.meongcare.supplement.utils.SupplementUtils.Companion.showTimeBottomSheet
import com.project.meongcare.supplement.view.bottomSheet.SupplementPictureBottomSheetDialogFragment
import com.project.meongcare.supplement.viewmodel.SupplementViewModel
import com.project.meongcare.supplement.viewmodel.SupplementViewModelFactory

class SupplementAddFragment : Fragment(), OnPictureChangedListener {
    lateinit var fragmentSupplementAddBinding: FragmentSupplementAddBinding
    lateinit var mainActivity: MainActivity
    lateinit var supplementViewModel: SupplementViewModel
    lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentSupplementAddBinding = FragmentSupplementAddBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        navController = findNavController()

        val factory = SupplementViewModelFactory(SupplementRepository())
        supplementViewModel = ViewModelProvider(this, factory)[SupplementViewModel::class.java]

        supplementViewModel.run {
            val selected = R.drawable.all_rect_main1_r5_outline_main3
            val unSelected = R.drawable.all_rect_gray3_r5_outline
            val selectedTextColor = getColor(mainActivity, R.color.main4)
            val unSelectedTextColor = getColor(mainActivity, R.color.gray4)

            onMgButtonClick()

            supplementCycle.observe(viewLifecycleOwner) {
                fragmentSupplementAddBinding.run {
                    if (it > 0) {
                        textViewSupplementAddCycleError.visibility = View.GONE
                    }
                    layoutSupplementAddCycleText.visibility = View.VISIBLE
                    textViewSupplementAddCycleCount.text = it.toString()
                }
            }

            intakeTimeList.observe(viewLifecycleOwner) {
                fragmentSupplementAddBinding.run {
                    if (it.isNotEmpty()) {
                        textViewSupplementAddTimeError.visibility = View.GONE
                        textViewSupplementAddTimeListEdit.visibility = View.VISIBLE
                        imageViewSupplementAddTimeListEssential.visibility = View.GONE
                        textViewSupplementAddTimeListCount.text = "${it.size}회"
                        layoutSupplementAddTimeListCount.visibility = View.VISIBLE
                    } else {
                        textViewSupplementAddTimeListEdit.visibility = View.GONE
                        imageViewSupplementAddTimeListEssential.visibility = View.VISIBLE
                        layoutSupplementAddTimeListCount.visibility = View.GONE
                    }

                    recyclerViewSupplementAddTimeList.run {
                        adapter = SupplementAddTimeRecyclerViewAdapter()
                        layoutManager = LinearLayoutManager(context)
                    }
                }
            }

            intakeTimeUnit.observe(viewLifecycleOwner) {
                Log.d("영양제 추가 단위", it)
                fragmentSupplementAddBinding.run {
                    recyclerViewSupplementAddTimeList.run {
                        adapter = SupplementAddTimeRecyclerViewAdapter()
                        layoutManager = LinearLayoutManager(context)
                    }
                }
            }

            mgButtonSelected.observe(viewLifecycleOwner) { isSelected ->
                fragmentSupplementAddBinding.run {
                    buttonSupplementAddUnitMg.setBackgroundResource(
                        if (isSelected) selected else unSelected,
                    )
                    textViewButtonSupplementAddUnitMg.setTextColor(
                        if (isSelected) selectedTextColor else unSelectedTextColor,
                    )
                }
            }

            scoopButtonSelected.observe(viewLifecycleOwner) { isSelected ->
                fragmentSupplementAddBinding.run {
                    buttonSupplementAddUnitScoop.setBackgroundResource(
                        if (isSelected) selected else unSelected,
                    )
                    textViewButtonSupplementAddUnitScoop.setTextColor(
                        if (scoopButtonSelected.value == true) selectedTextColor else unSelectedTextColor,
                    )
                }
            }

            jungButtonSelected.observe(viewLifecycleOwner) { isSelected ->
                fragmentSupplementAddBinding.run {
                    buttonSupplementAddUnitJung.setBackgroundResource(
                        if (isSelected) selected else unSelected,
                    )
                    textViewButtonSupplementAddUnitJung.setTextColor(
                        if (jungButtonSelected.value == true) selectedTextColor else unSelectedTextColor,
                    )
                }
            }
        }

        fragmentSupplementAddBinding.run {
            toolbarSupplementAdd.setNavigationOnClickListener {
                navController.popBackStack()
            }

            cardViewSupplementAdd.setOnClickListener {
                showPictureBottomSheet()
            }

            controlEditText(editTextSupplementAddBrandName, "브랜드를 입력해주세요")
            controlEditText(editTextSupplementAddName, "제품명을 입력해주세요")

            layoutSupplementAddCycle.setOnClickListener {
                showCycleBottomSheet(parentFragmentManager, supplementViewModel)
            }

            textViewSupplementAddTimeListAdd.setOnClickListener {
                showTimeBottomSheet(parentFragmentManager, supplementViewModel)
            }

            textViewSupplementAddTimeListEdit.setOnClickListener {
                onEditButtonClicked()
            }

            buttonSupplementAddUnitMg.setOnClickListener {
                supplementViewModel.onMgButtonClick()
            }

            buttonSupplementAddUnitScoop.setOnClickListener {
                supplementViewModel.onScoopButtonClick()
            }

            buttonSupplementAddUnitJung.setOnClickListener {
                supplementViewModel.onJungButtonClick()
            }

            buttonSupplementAddComplete.setOnClickListener {
                checkInput()
                if (checkInput()) {
                    val brandName = editTextSupplementAddBrandName.text.toString()
                    val name = editTextSupplementAddName.text.toString()
                    val cycle = supplementViewModel.supplementCycle.value!!
                    val intakeUnit = supplementViewModel.intakeTimeUnit.value!!
                    val intakeInfo = supplementViewModel.intakeTimeList.value!!
                    val imgUri = supplementViewModel.supplementAddImg.value
                    val supplementDto =
                        SupplementDto(1, brandName, name, cycle, intakeUnit, intakeInfo)
                    supplementViewModel.addSupplement(
                        supplementDto,
                        requireContext(),
                        imgUri ?: Uri.EMPTY,
                    )

                    supplementViewModel.supplementCode.observe(viewLifecycleOwner) {
                        if (it == 200) {
                            navController.popBackStack()
                        }
                    }
                }
            }
        }
        return fragmentSupplementAddBinding.root
    }

    inner class SupplementAddTimeRecyclerViewAdapter :
        RecyclerView.Adapter<SupplementAddTimeRecyclerViewAdapter.SupplementAddTimeViewHolder>() {
        private val itemEditModeStates =
            MutableList(supplementViewModel.intakeTimeList.value!!.size) { false }

        inner class SupplementAddTimeViewHolder(itemSupplementAddTimeBinding: ItemSupplementAddTimeBinding) :
            RecyclerView.ViewHolder(itemSupplementAddTimeBinding.root) {
            val itemSupplementAddTimeTime: TextView
            val itemSupplementAddTimeAmount: TextView
            val itemSupplementAddTimeMinus: ImageView

            init {
                itemSupplementAddTimeTime =
                    itemSupplementAddTimeBinding.textViewItemSupplementAddTime
                itemSupplementAddTimeAmount =
                    itemSupplementAddTimeBinding.textViewItemSupplementAddTimeAmount
                itemSupplementAddTimeMinus =
                    itemSupplementAddTimeBinding.imageViewItemSupplementAddTimeMinus
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int,
        ): SupplementAddTimeViewHolder {
            val itemSupplementAddTimeBinding = ItemSupplementAddTimeBinding.inflate(layoutInflater)
            val allViewHolder = SupplementAddTimeViewHolder(itemSupplementAddTimeBinding)

            itemSupplementAddTimeBinding.root.layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )
            return allViewHolder
        }

        override fun getItemCount(): Int {
            return supplementViewModel.intakeTimeList.value!!.size
        }

        override fun onBindViewHolder(
            holder: SupplementAddTimeViewHolder,
            position: Int,
        ) {
            val intakeCountString =
                supplementViewModel.intakeTimeList.value!![position].intakeCount
            val intakeUnitString = supplementViewModel.intakeTimeUnit.value
            holder.itemSupplementAddTimeTime.text =
                convertDateToTime(supplementViewModel.intakeTimeList.value!![position].intakeTime)
            holder.itemSupplementAddTimeAmount.text = "$intakeCountString$intakeUnitString"

            if (itemEditModeStates[position]) {
                holder.itemSupplementAddTimeMinus.visibility = View.VISIBLE
            } else {
                holder.itemSupplementAddTimeMinus.visibility = View.GONE
            }

            holder.itemSupplementAddTimeMinus.setOnClickListener {
                supplementViewModel.removeIntakeTimeListItem(position)
            }
        }

        fun setAllItemsToEditMode() {
            for (i in 0 until itemEditModeStates.size) {
                itemEditModeStates[i] = true
            }
            notifyDataSetChanged()
        }
    }

    fun onEditButtonClicked() {
        (
            fragmentSupplementAddBinding.recyclerViewSupplementAddTimeList.adapter
                as SupplementAddTimeRecyclerViewAdapter
        ).setAllItemsToEditMode()
    }

    fun isEditTextNullOrEmpty(editText: EditText) {
        editText.run {
            setBackgroundResource(R.drawable.all_rect_gray1_r5_outline_sub1)
            setHintTextColor(getColor(mainActivity, R.color.sub1))
            hint = "필수 입력 값입니다"
            isCursorVisible = false
            isFocusableInTouchMode = false
        }
        hideKeyboard(editText)
    }

    fun setEditTextOriginal(
        editText: EditText,
        hintText: String,
    ) {
        editText.run {
            setBackgroundResource(R.drawable.all_rect_gray2_r5)
            setHintTextColor(
                getColor(
                    mainActivity,
                    R.color.gray4,
                ),
            )
            hint = "$hintText"
            isCursorVisible = true
            isFocusableInTouchMode = true
            isFocusable = true
        }
    }

    fun controlEditText(
        editText: EditText,
        hintText: String,
    ) {
        editText.run {
            setOnEditorActionListener { view, actionId, keyEvent ->
                if (
                    (
                        actionId == EditorInfo.IME_ACTION_DONE ||
                            (
                                keyEvent != null && keyEvent.action == KeyEvent.ACTION_DOWN &&
                                    keyEvent.keyCode == KeyEvent.KEYCODE_ENTER
                            )
                    ) && this.text.trim().isNotEmpty()
                ) {
                    isCursorVisible = false
                    hideKeyboard(view)
                }
                false
            }

            setOnFocusChangeListener { view, b ->
                if (b) {
                    setEditTextOriginal(this, hintText)
                } else {
                    return@setOnFocusChangeListener
                }
            }

            setOnClickListener {
                setEditTextOriginal(this, hintText)
            }
        }
    }

    private fun showPictureBottomSheet() {
        val bottomSheetFragment = SupplementPictureBottomSheetDialogFragment()

        bottomSheetFragment.setOnPictureChangedListener(this)

        bottomSheetFragment.show(
            parentFragmentManager,
            "SupplementPictureBottomSheetDialogFragment",
        )
    }

    private fun checkInput(): Boolean {
        fragmentSupplementAddBinding.run {
            if (editTextSupplementAddBrandName.text.isNullOrEmpty() ||
                editTextSupplementAddName.text.isNullOrEmpty() ||
                supplementViewModel.supplementCycle.value == null ||
                supplementViewModel.intakeTimeList.value.isNullOrEmpty()
            ) {
                if (editTextSupplementAddBrandName.text.isNullOrEmpty()) {
                    isEditTextNullOrEmpty(editTextSupplementAddBrandName)
                }
                if (editTextSupplementAddName.text.isNullOrEmpty()) {
                    isEditTextNullOrEmpty(editTextSupplementAddName)
                }
                if (supplementViewModel.supplementCycle.value == null) {
                    textViewSupplementAddCycleError.visibility = View.VISIBLE
                }
                if (supplementViewModel.intakeTimeList.value.isNullOrEmpty()) {
                    textViewSupplementAddTimeError.visibility = View.VISIBLE
                }
                return false
            }
        }
        return true
    }

    override fun onPictureChanged(uri: Uri) {
        supplementViewModel.supplementAddImg.value = uri
        Glide.with(this@SupplementAddFragment)
            .load(uri)
            .into(fragmentSupplementAddBinding.imageViewSupplementAdd)
        fragmentSupplementAddBinding.layoutSupplementAddImage.visibility = View.GONE
        fragmentSupplementAddBinding.imageViewSupplementAdd.visibility = View.VISIBLE
    }
}
