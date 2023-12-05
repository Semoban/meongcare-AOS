package com.project.meongcare.symptom.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.meongcare.R
import com.project.meongcare.TempActivity
import com.project.meongcare.databinding.FragmentSymptomBinding
import com.project.meongcare.databinding.ItemSymptomBinding
import com.project.meongcare.databinding.ItemToolbarCalendarWeekBinding
import com.project.meongcare.symptom.model.entities.Symptom
import com.project.meongcare.symptom.model.entities.SymptomType
import com.project.meongcare.symptom.viewmodel.SymptomViewModel
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale

class SymptomFragment : Fragment() {
    lateinit var fragmentSymptomBinding: FragmentSymptomBinding
    lateinit var tempActivity: TempActivity
    lateinit var symptomViewModel: SymptomViewModel
    private val calendar = Calendar.getInstance()
    private var currentMonth = 0
    var selectDatePosition = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentSymptomBinding = FragmentSymptomBinding.inflate(layoutInflater)
        tempActivity = activity as TempActivity

        calendar.time = Date()
        currentMonth = calendar[Calendar.MONTH]

        symptomViewModel = ViewModelProvider(this)[SymptomViewModel::class.java]

        symptomViewModel.run {
            symptomList.observe(viewLifecycleOwner) {
                if (it.isNullOrEmpty()) {
                    fragmentSymptomBinding.run {
                        textViewSymptomNoData.visibility = View.VISIBLE
                        recyclerViewSymptom.visibility = View.GONE
                    }
                }

                fragmentSymptomBinding.run {
                    textViewSymptomNoData.visibility = View.GONE
                    recyclerViewSymptom.visibility = View.VISIBLE
                    recyclerViewSymptom.run {
                        adapter = SymptomRecyclerViewAdapter()
                        layoutManager = LinearLayoutManager(context)
                    }
                }
            }
            symptomDateList.observe(viewLifecycleOwner) { dateList ->
                fragmentSymptomBinding.toolbarSymptom.recyclerViewToolbarCalendarWeek.run {
                    tempActivity.runOnUiThread {
                        adapter?.notifyDataSetChanged()
                    }
                    Log.d("클릭2", "$dateList")
                }
            }

            selectedDate.observe(viewLifecycleOwner) {
                // date 갱신
                // symptomList.value =
                val localDateTime = changeDateToLocale(it)
                Log.d("클릭4", localDateTime.toString())
                fragmentSymptomBinding.toolbarSymptom.textViewToolbarCalendarWeekTitleDay.text =
                    getMonthDateDay(it)
            }
        }

        // 현재 로그인한 유저의 현재 강아지 이름
        val dogName = "김대박"

        fragmentSymptomBinding.run {
            textViewSymptomDogName.text = dogName

            textViewSymptomAdd.setOnClickListener {
                tempActivity.replaceFragment(TempActivity.SYMPTOM_ADD_FRAGMENT, true, null)
            }

            textViewSymptomEdit.setOnClickListener {
                tempActivity.replaceFragment(TempActivity.SYMPTOM_LIST_EDIT_FRAGMENT, true, null)
            }

            toolbarSymptom.imageViewToolbarCalendarWeekPrevious.setOnClickListener {
                Log.d("클릭", "클릭")
                generatePrevNewData()
                symptomViewModel.selectedDate.value =
                    symptomViewModel.symptomDateList.value!![symptomViewModel.selectDatePosition.value!!]
            }

            toolbarSymptom.imageViewToolbarCalendarWeekNext.setOnClickListener {
                Log.d("클릭", "클릭")
                generateNextNewData()
                symptomViewModel.selectedDate.value =
                    symptomViewModel.symptomDateList.value!![symptomViewModel.selectDatePosition.value!!]
            }

            toolbarSymptom.recyclerViewToolbarCalendarWeek.run {
                adapter = SymptomDateRecyclerViewAdapter()
//                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                layoutManager = GridLayoutManager(requireContext(), 7)
            }
        }
        return fragmentSymptomBinding.root
    }

    // 이상증상 타임라인
    inner class SymptomRecyclerViewAdapter :
        RecyclerView.Adapter<SymptomRecyclerViewAdapter.SymptomViewHolder>() {
        inner class SymptomViewHolder(itemSymptomBinding: ItemSymptomBinding) :
            RecyclerView.ViewHolder(itemSymptomBinding.root) {
                val itemSymptomName: TextView
                val itemSymptomTime: TextView
                val itemSymptomImg: ImageView

                init {
                    itemSymptomName = itemSymptomBinding.textViewItemSymptom
                    itemSymptomTime = itemSymptomBinding.textViewItemSymptomTime
                    itemSymptomImg = itemSymptomBinding.imageViewItemSymptom
                }
            }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int,
        ): SymptomViewHolder {
            val itemSymptomBinding = ItemSymptomBinding.inflate(layoutInflater)
            val allViewHolder = SymptomViewHolder(itemSymptomBinding)

            itemSymptomBinding.root.layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )

            itemSymptomBinding.root.setOnClickListener {
                tempActivity.replaceFragment(TempActivity.SYMPTOM_INFO_FRAGMENT, true, null)
            }

            return allViewHolder
        }

        override fun getItemCount(): Int {
            return symptomViewModel.symptomList.value!!.size
        }

        override fun onBindViewHolder(
            holder: SymptomViewHolder,
            position: Int,
        ) {
            holder.itemSymptomName.text = symptomViewModel.symptomList.value!![position].note
            holder.itemSymptomTime.text = symptomViewModel.symptomList.value!![position].dateTime
            holder.itemSymptomImg.setImageResource(getSymptomImg(symptomViewModel.symptomList.value!![position]))
        }
    }

    inner class SymptomDateRecyclerViewAdapter :
        RecyclerView.Adapter<SymptomDateRecyclerViewAdapter.SymptomDateViewHolder>() {
        inner class SymptomDateViewHolder(itemSymptomDateBinding: ItemToolbarCalendarWeekBinding) :
            RecyclerView.ViewHolder(itemSymptomDateBinding.root) {
            val itemSymptomDate: TextView
            val itemSymptomDay: TextView
            val itemLayout: LinearLayout

            init {
                itemSymptomDate = itemSymptomDateBinding.tvDateCalendarItem
                itemSymptomDay = itemSymptomDateBinding.tvDayCalendarItem
                itemLayout = itemSymptomDateBinding.clCalendarItem
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int,
        ): SymptomDateViewHolder {
            val itemSymptomDateBinding = ItemToolbarCalendarWeekBinding.inflate(layoutInflater)
            val symptomDateHolder = SymptomDateViewHolder(itemSymptomDateBinding)

//            val screenWidthDp = 328
//            val density = parent.resources.displayMetrics.density
//            val itemWidth = (screenWidthDp * density).toInt() / 7
//
//            itemSymptomDateBinding.root.layoutParams = ViewGroup.LayoutParams(
//                itemWidth,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//            )

            itemSymptomDateBinding.root.layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )

            return symptomDateHolder
        }

        override fun getItemCount(): Int {
            return symptomViewModel.symptomDateList.value!!.size
        }

        override fun onBindViewHolder(
            holder: SymptomDateViewHolder,
            position: Int,
        ) {
            holder.itemSymptomDate.text = getDate(getItem(position))
            holder.itemSymptomDay.text = getDay(getItem(position))

            symptomViewModel.selectDatePosition.observe(viewLifecycleOwner) {
                // 클릭한 아이템에 대한 레이아웃을 적용
                if (position == it) {
                    holder.itemLayout.setBackgroundResource(R.drawable.toolbar_rect_main1_r10)
                    holder.itemSymptomDate.setTextColor(
                        ContextCompat.getColor(
                            tempActivity,
                            R.color.main4,
                        ),
                    )
                    holder.itemSymptomDay.setTextColor(
                        ContextCompat.getColor(
                            tempActivity,
                            R.color.main4,
                        ),
                    )
                } else {
                    // 클릭하지 않은 아이템에 대한 레이아웃을 적용
                    holder.itemLayout.setBackgroundResource(R.drawable.toolbar_rect_white_r10)
                    holder.itemSymptomDate.setTextColor(
                        ContextCompat.getColor(
                            tempActivity,
                            R.color.black,
                        ),
                    )
                    holder.itemSymptomDay.setTextColor(
                        ContextCompat.getColor(
                            tempActivity,
                            R.color.black,
                        ),
                    )
                }
            }

            holder.itemLayout.setOnClickListener {
                symptomViewModel.selectDatePosition.value = position
                symptomViewModel.selectedDate.value = getItem(position)
            }
        }

        // getItem 함수는 데이터 리스트에서 특정 위치(position)의 아이템을 가져옵니다.
        private fun getItem(position: Int): Date {
            selectDatePosition = position
            return symptomViewModel.symptomDateList.value!![position]
        }
    }

    fun getSymptomImg(symptomData: Symptom): Int {
        return when (symptomData.symptomString) {
            SymptomType.WEIGHT_LOSS.symptomName -> R.drawable.all_weighing_machine
            SymptomType.HIGH_FEVER.symptomName -> R.drawable.all_temperature_measurement
            SymptomType.COUGH.symptomName -> R.drawable.symptom_cough
            SymptomType.DIARRHEA.symptomName -> R.drawable.symptom_diarrhea
            SymptomType.LOSS_OF_APPETITE.symptomName -> R.drawable.symptom_loss_appetite
            SymptomType.ACTIVITY_DECREASE.symptomName -> R.drawable.symptom_amount_activity
            else -> R.drawable.symptom_stethoscope
        }
    }

    fun getDay(date: Date): String = SimpleDateFormat("EE", Locale.getDefault()).format(date)

    fun getMonthDateDay(date: Date): String = SimpleDateFormat("MM.dd EE", Locale.getDefault()).format(date)

    fun getDate(date: Date): String = SimpleDateFormat("d", Locale.getDefault()).format(date)

    private fun generatePrevNewData() {
        val calendar = Calendar.getInstance()
        calendar.time = symptomViewModel.symptomDateList.value!![0]

        calendar.add(Calendar.DAY_OF_YEAR, -7)

        // 현재 날짜의 요일을 가져옵니다. (일요일: 1, 월요일: 2, ..., 토요일: 7)
        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        // 현재 날짜를 기준으로 주의 시작일로 이동합니다.
        calendar.add(Calendar.DAY_OF_YEAR, 1 - currentDayOfWeek)

        // 주의 시작일부터 7일 동안의 날짜 리스트를 만듭니다.
        val weekDates = mutableListOf<Date>()
        repeat(7) {
            weekDates.add(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        symptomViewModel.symptomDateList.value =
            symptomViewModel.symptomDateList.value?.let {
                val updatedList = mutableListOf<Date>()
                updatedList.addAll(weekDates)
                // updatedList.addAll(it)
                updatedList
            }

        Log.d("클릭", symptomViewModel.symptomDateList.value.toString())
    }

    private fun generateNextNewData() {
        val calendar = Calendar.getInstance()
        calendar.time = symptomViewModel.symptomDateList.value!!.last()
        calendar.add(Calendar.DAY_OF_YEAR, +7)

        // 현재 날짜의 요일을 가져옵니다. (일요일: 1, 월요일: 2, ..., 토요일: 7)
        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        // 현재 날짜를 기준으로 주의 시작일로 이동합니다.
        calendar.add(Calendar.DAY_OF_YEAR, 1 - currentDayOfWeek)

        // 주의 시작일부터 7일 동안의 날짜 리스트를 만듭니다.
        val weekDates = mutableListOf<Date>()
        repeat(7) {
            weekDates.add(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        symptomViewModel.symptomDateList.value =
            symptomViewModel.symptomDateList.value?.let {
                val updatedList = mutableListOf<Date>()
                // updatedList.addAll(it)
                updatedList.addAll(weekDates)
                updatedList
            }

        Log.d("클릭", symptomViewModel.symptomDateList.value.toString())
    }

    fun changeDateToLocale(date: Date): LocalDateTime {
        // Date를 Instant로 변환
        val instant: Instant = date.toInstant()

        // Instant를 ZoneId를 사용하여 LocalDateTime으로 변환
        val localDateTime: LocalDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
        return localDateTime
    }
}
