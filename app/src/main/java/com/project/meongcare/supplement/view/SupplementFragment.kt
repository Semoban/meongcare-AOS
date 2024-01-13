package com.project.meongcare.supplement.view

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.meongcare.MainActivity
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentSupplementBinding
import com.project.meongcare.databinding.ItemSupplementBinding
import com.project.meongcare.supplement.model.data.repository.SupplementRepository
import com.project.meongcare.supplement.utils.SupplementUtils
import com.project.meongcare.supplement.viewmodel.SupplementViewModel
import com.project.meongcare.supplement.viewmodel.SupplementViewModelFactory
import com.project.meongcare.toolbar.viewmodel.ToolbarViewModel
import java.util.Calendar

class SupplementFragment : Fragment() {
    lateinit var fragmentSupplementBinding: FragmentSupplementBinding
    lateinit var mainActivity: MainActivity
    lateinit var toolbarViewModel: ToolbarViewModel
    lateinit var supplementViewModel: SupplementViewModel
    lateinit var navController: NavController
    var supplementIdListTemp = mutableMapOf<Int, Boolean>()
    private val regularItem = 0
    private val lastItem = 1
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentSupplementBinding = FragmentSupplementBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        toolbarViewModel = mainActivity.toolbarViewModel
        val factory = SupplementViewModelFactory(SupplementRepository())
        supplementViewModel = ViewModelProvider(this, factory)[SupplementViewModel::class.java]

        toolbarViewModel.run {
            selectedDate.observe(viewLifecycleOwner) {
                if (it != null) {
                    fragmentSupplementBinding.run {
                        supplementViewModel.getSupplements(
                            1,
                            it,
                        )
                    }
                }
            }
        }

        supplementViewModel.run {
            supplementList.observe(viewLifecycleOwner) {
                supplementIdListTemp =
                    it.associateBy(
                        { it.supplementsRecordId },
                        { it.intakeStatus },
                    ) as MutableMap<Int, Boolean>

                fragmentSupplementBinding.run {
                    if (supplementViewModel.supplementList.value.isNullOrEmpty()) {
                        recyclerViewSupplement.visibility = View.GONE
                        textViewSupplementEdit.visibility = View.GONE
                        layoutSupplementNoData.visibility = View.VISIBLE
                    } else {
                        recyclerViewSupplement.visibility = View.VISIBLE
                        textViewSupplementEdit.visibility = View.VISIBLE
                        layoutSupplementNoData.visibility = View.GONE
                    }
                    recyclerViewSupplement.run {
                        adapter = SupplementRecyclerViewAdapter()
                        layoutManager = LinearLayoutManager(context)
                    }
                }

                supplementCheckCount.observe(viewLifecycleOwner) {
                    fragmentSupplementBinding.run {
                        updatePercentage(
                            progressBarSupplementComplete,
                            textViewSupplementPercentage,
                            textViewSupplementProgressPercentageBottom,
                        )
                    }
                }
            }
        }

        navController = findNavController()

        fragmentSupplementBinding.run {
            textViewSupplementAdd.setOnClickListener {
                navController.navigate(R.id.action_supplement_to_supplementAdd)
            }

            textViewSupplementEdit.setOnClickListener {
                navController.navigate(R.id.action_supplement_to_supplementRoutineEdit)
            }
        }
        return fragmentSupplementBinding.root
    }

    inner class SupplementRecyclerViewAdapter :
        RecyclerView.Adapter<SupplementRecyclerViewAdapter.SupplementViewHolder>() {
        inner class SupplementViewHolder(itemSupplementBinding: ItemSupplementBinding) :
            RecyclerView.ViewHolder(itemSupplementBinding.root) {
            val itemSupplementName: TextView
            val itemSupplementTime: TextView
            val itemSupplementUnit: TextView
            val itemSupplementCheckImg: ImageView
            val itemSupplementCardView: CardView
            val itemSupplementLine: View
            val itemSupplementLayout: LinearLayout

            init {
                itemSupplementName = itemSupplementBinding.textViewItemSupplementName
                itemSupplementTime = itemSupplementBinding.textViewItemSupplementTime
                itemSupplementUnit = itemSupplementBinding.textViewItemSupplementUnit
                itemSupplementCheckImg = itemSupplementBinding.imageViewItemSupplementCheck
                itemSupplementCardView = itemSupplementBinding.cardViewItemSupplement
                itemSupplementLine = itemSupplementBinding.viewItemSupplementLine
                itemSupplementLayout = itemSupplementBinding.layoutItemSupplementText
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int,
        ): SupplementViewHolder {
            val itemSupplementBinding = ItemSupplementBinding.inflate(layoutInflater)
            val allViewHolder = SupplementViewHolder(itemSupplementBinding)

            if (viewType == lastItem) {
                val layoutParams =
                    allViewHolder.itemSupplementCardView.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.bottomMargin = 0
            }
            itemSupplementBinding.root.layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )

            return allViewHolder
        }

        override fun getItemCount(): Int {
            return supplementViewModel.supplementList.value!!.size
        }

        override fun getItemViewType(position: Int): Int {
            return if (position == itemCount - 1) {
                lastItem
            } else {
                regularItem
            }
        }

        override fun onBindViewHolder(
            holder: SupplementViewHolder,
            position: Int,
        ) {
//            val supplementId = supplementViewModel.supplementList.value!![position].supplementsId
            val currentTime =
                SupplementUtils.convertDateToTime(supplementViewModel.supplementList.value!![position].intakeTime)
            var prevTime = ""
            var nextTime = ""

            if (position != 0) {
                prevTime =
                    SupplementUtils.convertDateToTime(supplementViewModel.supplementList.value!![position - 1].intakeTime)
            }

            if (position != supplementViewModel.supplementList.value!!.size - 1) {
                nextTime =
                    SupplementUtils.convertDateToTime(supplementViewModel.supplementList.value!![position + 1].intakeTime)
            }

            holder.itemSupplementName.text =
                supplementViewModel.supplementList.value!![position].name

            holder.itemSupplementTime.text = currentTime

            if (prevTime == currentTime) {
                setCardViewMarginZero(holder)
                holder.itemSupplementTime.visibility = View.GONE
            }

            if (currentTime == nextTime) {
                setCardViewMarginZero(holder)
            } else if (position != supplementViewModel.supplementList.value!!.lastIndex) {
                holder.itemSupplementCardView.run {
                    val layoutParams =
                        holder.itemSupplementCardView.layoutParams as ViewGroup.MarginLayoutParams
                    layoutParams.bottomMargin = dpToPx(context, 50f)
                }
            }

            val intakeCount = supplementViewModel.supplementList.value!![position].intakeCount
            val intakeUnit = supplementViewModel.supplementList.value!![position].intakeUnit

            holder.itemSupplementUnit.text = "$intakeCount$intakeUnit"

            val supplementsRecordId =
                supplementViewModel.supplementList.value!![position].supplementsRecordId

            holder.itemSupplementCheckImg.run {
                val today = Calendar.getInstance().time

                if (toolbarViewModel.selectedDate.value!!.after(today)) {
                    visibility = View.GONE
                }

                isSelected = supplementIdListTemp[supplementsRecordId] == true
                setOnClickListener {
                    supplementViewModel.run {
                        checkSupplement(supplementsRecordId, holder.itemSupplementCheckImg)
                    }
                }
            }

            holder.itemSupplementLayout.setOnClickListener {
                val supplementsId = supplementViewModel.supplementList.value!![position].supplementsId
                val bundle = Bundle()
                bundle.putInt("supplementsId", supplementsId)
                navController.navigate(R.id.action_supplement_to_supplementInfo, bundle)
            }
        }

        private fun setCardViewMarginZero(holder: SupplementViewHolder) {
            holder.itemSupplementCardView.run {
                val layoutParams =
                    holder.itemSupplementCardView.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.bottomMargin = 0
            }
        }
    }

    fun dpToPx(
        context: Context,
        dp: Float,
    ): Int {
        val resources = context.resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics,
        ).toInt()
    }
}
