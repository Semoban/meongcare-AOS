package com.project.meongcare.feed.view

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.archit.calendardaterangepicker.customviews.CalendarListener
import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentFeedAddEditBinding
import com.project.meongcare.excreta.utils.SUCCESS
import com.project.meongcare.feed.model.data.local.FeedPhotoListener
import com.project.meongcare.feed.model.entities.FeedInfo
import com.project.meongcare.feed.model.entities.FeedUploadRequest
import com.project.meongcare.feed.model.utils.FeedInfoUtils.convertFeedFile
import com.project.meongcare.feed.model.utils.FeedInfoUtils.convertFeedPostDto
import com.project.meongcare.feed.model.utils.FeedValidationUtils.validationBrandAndFeedName
import com.project.meongcare.feed.model.utils.FeedValidationUtils.validationIngredient
import com.project.meongcare.feed.model.utils.FeedValidationUtils.validationIntakePeriod
import com.project.meongcare.feed.model.utils.FeedValidationUtils.validationKcal
import com.project.meongcare.feed.model.utils.FeedValidationUtils.validationTotalIngredient
import com.project.meongcare.feed.viewmodel.FeedPostViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import kotlin.concurrent.thread

@AndroidEntryPoint
class FeedAddFragment : Fragment(), FeedPhotoListener {
    private var _binding: FragmentFeedAddEditBinding? = null
    val binding get() = _binding!!

    private lateinit var inputMethodManager: InputMethodManager
    private val feedPostViewModel: FeedPostViewModel by viewModels()

    private var recommendIntake = 0.0
    var selectedStartDate = ""
    var selectedEndDate = ""
    private lateinit var feedInfo: FeedInfo
    private var imageUri: Uri? = null

    private var proteinValue = 0.0
    private var fatValue = 0.0
    private var ashValue = 0.0
    private var moistureValue = 0.0
    private var kcalValue = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFeedAddEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initInputMethodManager()
        initToolbar()
        initPhotoAttachModalBottomSheet()
        applyKeyboardHidingAction()
        applyKcalContentEditorBehavior()
        updateCalendarVisibility()
        updateSelectedIntakePeriod()
        validationFeedInfo()
    }

    private fun applyKcalContentEditorBehavior() {
        binding.edittextFeedaddeditKcalContent.apply {
            setOnEditorActionListener { _, _, _ ->
                initRecommendDailyIntake(text.toString().toDouble())
                hideSoftKeyboard()
                true
            }
        }
    }

    private fun initToolbar() {
        binding.toolbarFeedaddedit.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initPhotoAttachModalBottomSheet() {
        binding.cardviewFeedaddeditImage.setOnClickListener {
            val photoAttachModalBottomSheet = FeedPhotoAttachModalBottomSheetFragment()
            photoAttachModalBottomSheet.setPhotoListener(this@FeedAddFragment)
            photoAttachModalBottomSheet.show(
                requireActivity().supportFragmentManager,
                FeedPhotoAttachModalBottomSheetFragment.TAG,
            )
        }
    }

    private fun hideKeyboardOnAction(editText: EditText) {
        editText.apply {
            setOnEditorActionListener { _, _, _ ->
                hideSoftKeyboard()
                true
            }
        }
    }

    private fun applyKeyboardHidingAction() {
        binding.apply {
            hideKeyboardOnAction(edittextFeedaddeditBrand)
            hideKeyboardOnAction(edittextFeedaddeditName)
            hideKeyboardOnAction(edittextFeedaddeditCrudeProteinPercentage)
            hideKeyboardOnAction(edittextFeedaddeditCrudeFatPercent)
            hideKeyboardOnAction(edittextFeedaddeditCrudeAshPercent)
            hideKeyboardOnAction(edittextFeedaddeditMoisturePercent)
        }
    }

    private fun initRecommendDailyIntake(feedKcal: Double) {
        val weight = 15.0
        recommendIntake = calculateRecommendDailyIntake(weight, feedKcal)
        binding.textviewFeedaddeditDailyIntakeContent.text = "${recommendIntake}g"
    }

    private fun calculateRecommendDailyIntake(
        weight: Double,
        feedKcal: Double,
    ): Double {
        val dailyEnergyRequirement = 1.6 * (30 * weight + 70)
        val recommendDailyIntake = dailyEnergyRequirement * 1000 / feedKcal
        return String.format("%.2f", recommendDailyIntake).toDouble()
    }

    private fun updateCalendarVisibility() {
        binding.apply {
            textviewFeedaddeditIntakePeriodStart.apply {
                setOnClickListener {
                    text = "시작 일자"
                    setBackgroundResource(R.drawable.all_rect_gray1_r5)
                    setTextColor(resources.getColor(R.color.black, null))
                    calendarviewFeedaddeditStartDate.visibility = View.VISIBLE
                    calendarviewFeedaddeditEndDate.visibility = View.GONE
                    checkboxFeedaddeditDoNotKnowEndDate.visibility = View.GONE
                    textviewFeedaddeditDoNotKnowEndDate.visibility = View.GONE
                    if (textviewFeedaddeditIntakePeriodEnd.text != "필수 입력 값입니다") {
                        textviewFeedaddeditIntakePeriodEnd.setTextColor(resources.getColor(R.color.gray4, null))
                    }
                    scrollviewFeedadd.smoothScrollTo(0, buttonFeedaddeditCompletion.bottom)
                }
            }
            textviewFeedaddeditIntakePeriodEnd.apply {
                setOnClickListener {
                    text = "종료 일자"
                    setBackgroundResource(R.drawable.all_rect_gray1_r5)
                    setTextColor(resources.getColor(R.color.black, null))
                    calendarviewFeedaddeditEndDate.visibility = View.VISIBLE
                    calendarviewFeedaddeditStartDate.visibility = View.INVISIBLE
                    checkboxFeedaddeditDoNotKnowEndDate.visibility = View.VISIBLE
                    textviewFeedaddeditDoNotKnowEndDate.visibility = View.VISIBLE
                    if (textviewFeedaddeditIntakePeriodEnd.text != "필수 입력 값입니다") {
                        textviewFeedaddeditIntakePeriodStart.setTextColor(resources.getColor(R.color.gray4, null))
                    }
                    scrollviewFeedadd.smoothScrollTo(0, buttonFeedaddeditCompletion.bottom)
                }
            }
        }
    }

    private fun updateSelectedIntakePeriodStartDate(
        calendar: DateRangeCalendarView,
        date: TextView,
    ) {
        calendar.setCalendarListener(
            object : CalendarListener {
                override fun onDateRangeSelected(
                    startDate: Calendar,
                    endDate: Calendar,
                ) {
                    calendar.resetAllSelectedViews()
                }

                override fun onFirstDateSelected(startDate: Calendar) {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    selectedStartDate = dateFormat.format(startDate.time)

                    date.text = convertDateFormat(selectedStartDate)
                }
            },
        )
    }

    private fun updateSelectedIntakePeriodEndDate(
        calendar: DateRangeCalendarView,
        date: TextView,
        checkBox: CheckBox,
    ) {
        calendar.setCalendarListener(
            object : CalendarListener {
                override fun onDateRangeSelected(
                    startDate: Calendar,
                    endDate: Calendar,
                ) {
                    calendar.resetAllSelectedViews()
                }

                override fun onFirstDateSelected(startDate: Calendar) {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    selectedEndDate = dateFormat.format(startDate.time)
                    date.text = convertDateFormat(selectedEndDate)

                    checkBox.setOnClickListener {
                        calendar.resetAllSelectedViews()
                        selectedEndDate = null.toString()
                        date.text = "모름"
                    }

                    if (date.text != "모름") {
                        checkBox.isChecked = false
                    }
                }
            },
        )
    }

    private fun updateSelectedIntakePeriod() {
        binding.apply {
            updateSelectedIntakePeriodStartDate(
                calendarviewFeedaddeditStartDate,
                textviewFeedaddeditIntakePeriodStart,
            )
            updateSelectedIntakePeriodEndDate(
                calendarviewFeedaddeditEndDate,
                textviewFeedaddeditIntakePeriodEnd,
                checkboxFeedaddeditDoNotKnowEndDate,
            )
        }
    }

    private fun initInputMethodManager() {
        thread {
            SystemClock.sleep(300)
            inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            hideSoftKeyboard()
        }
    }

    private fun hideSoftKeyboard() {
        if (requireActivity().currentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus!!.windowToken, 0)
            requireActivity().currentFocus!!.clearFocus()
        }
    }

    private fun createFeedInfo() {
        binding.apply {
            val brand = edittextFeedaddeditBrand.text.toString()
            val feedName = edittextFeedaddeditName.text.toString()
            feedInfo =
                FeedInfo(
                    2L,
                    brand,
                    feedName,
                    proteinValue,
                    fatValue,
                    ashValue,
                    moistureValue,
                    kcalValue,
                    recommendIntake.toInt(),
                    selectedStartDate,
                    selectedEndDate,
                )
            imageUri = feedPostViewModel.feedImage.value
        }
    }

    private fun validationFeedInfo() {
        binding.apply {
            buttonFeedaddeditCompletion.setOnClickListener {
                var isValid = true

                if (textviewFeedaddeditIntakePeriodEnd.text == "종료 일자") {
                    validationIntakePeriod(
                        textviewFeedaddeditIntakePeriodEnd,
                    )
                    isValid = false
                }

                if (textviewFeedaddeditIntakePeriodStart.text == "시작 일자") {
                    validationIntakePeriod(
                        textviewFeedaddeditIntakePeriodStart,
                    )
                    isValid = false
                }

                val kcal = edittextFeedaddeditKcalContent.text.toString()
                if (kcal.isEmpty() || kcal == "000.00") {
                    validationKcal(
                        edittextFeedaddeditKcalContent,
                        textviewFeedaddeditIngredientAndKcalError,
                    )
                    isValid = false
                }

                val protein = edittextFeedaddeditCrudeProteinPercentage.text.toString()
                val fat = edittextFeedaddeditCrudeFatPercent.text.toString()
                val ash = edittextFeedaddeditCrudeAshPercent.text.toString()
                val moisture = edittextFeedaddeditMoisturePercent.text.toString()

                proteinValue = protein.toDoubleOrNull() ?: 0.0
                fatValue = fat.toDoubleOrNull() ?: 0.0
                ashValue = ash.toDoubleOrNull() ?: 0.0
                moistureValue = moisture.toDoubleOrNull() ?: 0.0

                if (protein.isEmpty() || protein == "0.00") {
                    validationIngredient(
                        textviewFeedaddeditIngredientAndKcalError,
                        edittextFeedaddeditCrudeProteinPercentage,
                        scrollviewFeedadd,
                        textviewFeedaddeditIngredient,
                    )
                    isValid = false
                }

                if (fat.isEmpty() || fat == "0.00") {
                    validationIngredient(
                        textviewFeedaddeditIngredientAndKcalError,
                        edittextFeedaddeditCrudeFatPercent,
                        scrollviewFeedadd,
                        textviewFeedaddeditIngredient,
                    )
                    isValid = false
                }

                if (ash.isEmpty() || ash == "0.00") {
                    validationIngredient(
                        textviewFeedaddeditIngredientAndKcalError,
                        edittextFeedaddeditCrudeAshPercent,
                        scrollviewFeedadd,
                        textviewFeedaddeditIngredient,
                    )
                    isValid = false
                }

                if (moisture.isEmpty() || moisture == "0.00") {
                    validationIngredient(
                        textviewFeedaddeditIngredientAndKcalError,
                        edittextFeedaddeditMoisturePercent,
                        scrollviewFeedadd,
                        textviewFeedaddeditIngredient,
                    )
                    isValid = false
                }

                val totalIngredient = proteinValue + fatValue + ashValue + moistureValue

                if (totalIngredient > 100) {
                    validationTotalIngredient(
                        textviewFeedaddeditIngredientAndKcalError,
                        scrollviewFeedadd,
                        textviewFeedaddeditIngredient,
                    )
                    isValid = false
                }

                if (edittextFeedaddeditName.text.toString().isEmpty()) {
                    validationBrandAndFeedName(
                        edittextFeedaddeditName,
                        textviewFeedaddeditNameError,
                        scrollviewFeedadd,
                        textviewFeedaddeditName,
                        inputMethodManager,
                    )
                    isValid = false
                }

                if (edittextFeedaddeditBrand.text.toString().isEmpty()) {
                    validationBrandAndFeedName(
                        edittextFeedaddeditBrand,
                        textviewFeedaddeditBrandError,
                        scrollviewFeedadd,
                        textviewFeedaddeditBrand,
                        inputMethodManager
                    )
                    isValid = false
                }

                if (isValid) {
                    postFeedInfo()
                }
            }
        }
    }

    private fun postFeedInfo() {
        createFeedInfo()
        val dto = convertFeedPostDto(feedInfo)
        val file =
            convertFeedFile(
                requireContext(),
                imageUri ?: Uri.EMPTY,
            )
        val uploadRequest = FeedUploadRequest(dto, file)

        feedPostViewModel.postFeed(
            uploadRequest,
        )
        feedPostViewModel.feedPosted.observe(viewLifecycleOwner) { response ->
            if (response == SUCCESS) {
                findNavController().popBackStack()
                Snackbar.make(requireView(), "사료가 등록되었습니다!", Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(
                    requireView(),
                    "서버가 불안정 하여 사료 정보 등록에 실패하였습니다.\n잠시 후 다시 시도해 주세요.",
                    Snackbar.LENGTH_SHORT,
                ).show()
            }
        }
    }

    override fun onUriPassed(uri: Uri) {
        feedPostViewModel.getFeedImage(uri)
        binding.apply {
            Glide.with(this@FeedAddFragment)
                .load(uri)
                .into(imageviewFeedaddeditPicture)
            layoutFeedaddeditImage.root.visibility = View.INVISIBLE
        }
    }

    fun convertDateFormat(date: String): String {
        val outputFormat = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")

        val parsedDate = LocalDate.parse(date)
        return outputFormat.format(parsedDate)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
