package com.project.meongcare.feed.view

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.archit.calendardaterangepicker.customviews.CalendarListener
import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView
import com.bumptech.glide.Glide
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentFeedAddEditBinding
import com.project.meongcare.excreta.utils.SUCCESS
import com.project.meongcare.feed.model.data.local.FeedPhotoListener
import com.project.meongcare.feed.model.entities.FeedDetailGetResponse
import com.project.meongcare.feed.model.entities.FeedPutInfo
import com.project.meongcare.feed.model.entities.FeedUploadRequest
import com.project.meongcare.feed.model.utils.FeedDateUtils.convertDateFormat
import com.project.meongcare.feed.model.utils.FeedInfoUtils.calculateRecommendDailyIntake
import com.project.meongcare.feed.model.utils.FeedInfoUtils.convertFeedFile
import com.project.meongcare.feed.model.utils.FeedInfoUtils.convertFeedPutDto
import com.project.meongcare.feed.model.utils.FeedInfoUtils.initRecommendDailyIntake
import com.project.meongcare.feed.model.utils.FeedValidationUtils.validationBrandAndFeedName
import com.project.meongcare.feed.model.utils.FeedValidationUtils.validationIngredient
import com.project.meongcare.feed.model.utils.FeedValidationUtils.validationIntakePeriod
import com.project.meongcare.feed.model.utils.FeedValidationUtils.validationKcal
import com.project.meongcare.feed.model.utils.FeedValidationUtils.validationTotalIngredient
import com.project.meongcare.feed.viewmodel.DogViewModel
import com.project.meongcare.feed.viewmodel.FeedPutViewModel
import com.project.meongcare.snackbar.view.CustomSnackBar
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.concurrent.thread

@AndroidEntryPoint
class FeedEditFragment : Fragment(), FeedPhotoListener {
    private var _binding: FragmentFeedAddEditBinding? = null
    val binding
        get() = _binding!!

    private var feedId = 0L
    private var feedRecordId = 0L
    private lateinit var feedInfo: FeedDetailGetResponse
    private lateinit var feedPutInfo: FeedPutInfo
    private var recommendIntake = 0.0
    private var selectedStartDate = ""
    private var selectedEndDate: String? = null

    private val feedPutViewModel: FeedPutViewModel by viewModels()
    private val dogViewModel: DogViewModel by viewModels()
    private lateinit var inputMethodManager: InputMethodManager

    private var proteinValue = 0.0
    private var fatValue = 0.0
    private var ashValue = 0.0
    private var moistureValue = 0.0
    private var kcal = ""
    private var weight = 0.0

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
        dogViewModel.fetchDogWeight()
        dogViewModel.dogWeight.observe(viewLifecycleOwner) { response ->
            weight = response
        }
        initInputMethodManager()
        feedId = getFeedId()
        feedRecordId = getFeedRecordId()
        feedInfo = getFeedInfo()
        initToolbar()
        fetchFeedInfo()
        initPhotoAttachModalBottomSheet()
        applyKcalContentEditorBehavior()
        updateCalendarVisibility()
        updateSelectedIntakePeriod()
        validationFeedInfo()
    }

    private fun fetchFeedInfo() {
        binding.apply {
            recommendIntake = feedInfo.recommendIntake.toDouble()
            if (feedInfo.imageURL.isNotEmpty()) {
                Glide.with(this@FeedEditFragment)
                    .load(feedInfo.imageURL)
                    .into(imageviewFeedaddeditPicture)
                layoutFeedaddeditImage.root.visibility = View.INVISIBLE
            }
            edittextFeedaddeditBrand.setText(feedInfo.brand)
            edittextFeedaddeditName.setText(feedInfo.feedName)
            edittextFeedaddeditCrudeProteinPercentage.setText(feedInfo.protein.toString())
            edittextFeedaddeditCrudeFatPercent.setText(feedInfo.fat.toString())
            edittextFeedaddeditCrudeAshPercent.setText(feedInfo.crudeAsh.toString())
            edittextFeedaddeditMoisturePercent.setText(feedInfo.moisture.toString())
            edittextFeedaddeditKcalContent.setText(feedInfo.kcal.toString())
            textviewFeedaddeditDailyIntakeContent.text = "${recommendIntake}g"
            textviewFeedaddeditIntakePeriodStart.apply {
                selectedStartDate = feedInfo.startDate
                text = convertDateFormat(feedInfo.startDate)
                setTextColor(resources.getColor(R.color.black, null))
            }
            textviewFeedaddeditIntakePeriodEnd.apply {
                text =
                    if (feedInfo.endDate != null) {
                        selectedEndDate = feedInfo.endDate
                        convertDateFormat(feedInfo.endDate)
                    } else {
                        "모름"
                    }
                setTextColor(resources.getColor(R.color.black, null))
            }
        }
    }

    private fun initToolbar() {
        binding.toolbarFeedaddedit.apply {
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun initPhotoAttachModalBottomSheet() {
        binding.cardviewFeedaddeditImage.setOnClickListener {
            val photoAttachModalBottomSheet = FeedPhotoAttachModalBottomSheetFragment()
            photoAttachModalBottomSheet.setPhotoListener(this@FeedEditFragment)
            photoAttachModalBottomSheet.show(
                requireActivity().supportFragmentManager,
                FeedPhotoAttachModalBottomSheetFragment.TAG,
            )
        }
    }

    private fun applyKcalContentEditorBehavior() {
        binding.apply {
            edittextFeedaddeditKcalContent.apply {
                setOnEditorActionListener { _, _, _ ->
                    recommendIntake =
                        calculateRecommendDailyIntake(
                            weight,
                            text.toString().toDoubleOrNull() ?: 0.0,
                        )
                    initRecommendDailyIntake(
                        recommendIntake,
                        textviewFeedaddeditDailyIntakeContent,
                    )
                    hideSoftKeyboard()
                    true
                }
            }
        }
    }

    private fun updateCalendarVisibility() {
        binding.apply {
            textviewFeedaddeditIntakePeriodStart.apply {
                setOnClickListener {
                    setTextColor(resources.getColor(R.color.black, null))
                    calendarviewFeedaddeditStartDate.visibility = View.VISIBLE
                    calendarviewFeedaddeditEndDate.visibility = View.GONE
                    checkboxFeedaddeditDoNotKnowEndDate.visibility = View.GONE
                    textviewFeedaddeditDoNotKnowEndDate.visibility = View.GONE
                    textviewFeedaddeditIntakePeriodEnd.setTextColor(
                        resources.getColor(
                            R.color.gray4,
                            null,
                        ),
                    )
                }
            }
            textviewFeedaddeditIntakePeriodEnd.apply {
                setOnClickListener {
                    setTextColor(resources.getColor(R.color.black, null))
                    calendarviewFeedaddeditEndDate.visibility = View.VISIBLE
                    calendarviewFeedaddeditStartDate.visibility = View.INVISIBLE
                    checkboxFeedaddeditDoNotKnowEndDate.visibility = View.VISIBLE
                    textviewFeedaddeditDoNotKnowEndDate.visibility = View.VISIBLE
                    textviewFeedaddeditIntakePeriodStart.setTextColor(
                        resources.getColor(
                            R.color.gray4,
                            null,
                        ),
                    )
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
                        selectedEndDate = null
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

    private fun createFeedInfo() {
        binding.apply {
            val brand = edittextFeedaddeditBrand.text.toString()
            val feedName = edittextFeedaddeditName.text.toString()
            kcal = edittextFeedaddeditKcalContent.text.toString()
            feedPutInfo =
                FeedPutInfo(
                    feedId,
                    brand,
                    feedName,
                    proteinValue,
                    fatValue,
                    ashValue,
                    moistureValue,
                    kcal.toDouble(),
                    recommendIntake.toInt(),
                    selectedStartDate,
                    selectedEndDate,
                    feedRecordId,
                )
        }
    }

    private fun validationFeedInfo() {
        binding.run {
            buttonFeedaddeditCompletion.setOnClickListener {
                var isValid = true

                if (selectedEndDate != null) {
                    val startDate = selectedStartDate.replace("-", "").toInt()
                    val endDate = selectedEndDate?.replace("-","")?.toInt()!!

                    if (startDate > endDate) {
                        textviewFeedaddeditIntakePeriodError.visibility = View.VISIBLE
                        isValid = false
                    }
                }

                if (textviewFeedaddeditIntakePeriodStart.text == "시작 일자") {
                    validationIntakePeriod(
                        textviewFeedaddeditIntakePeriodStart,
                    )
                    isValid = false
                }

                if (textviewFeedaddeditIntakePeriodEnd.text == "종료 일자") {
                    validationIntakePeriod(
                        textviewFeedaddeditIntakePeriodEnd,
                    )
                    isValid = false
                }

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

                proteinValue = protein.toDoubleOrNull() ?: 0.0
                fatValue = fat.toDoubleOrNull() ?: 0.0
                ashValue = ash.toDoubleOrNull() ?: 0.0
                moistureValue = moisture.toDoubleOrNull() ?: 0.0

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
                    editFeedInfo()
                }
            }
        }
    }

    private fun editFeedInfo() {
        binding.apply {
            createFeedInfo()
            val imageUri = feedPutViewModel.feedImage.value

            val dto = convertFeedPutDto(feedPutInfo)
            val file =
                convertFeedFile(
                    requireContext(),
                    imageUri ?: Uri.EMPTY,
                )

            val feedUploadRequest =
                FeedUploadRequest(
                    dto,
                    file,
                )
            feedPutViewModel.putFeed(feedUploadRequest)
            feedPutViewModel.feedPut.observe(viewLifecycleOwner) { response ->
                if (response == SUCCESS) {
                    findNavController().popBackStack()
                    CustomSnackBar.make(requireView(), R.drawable.snackbar_success_16dp, "사료 정보가 수정되었습니다!")
                        .show()
                } else {
                    CustomSnackBar.make(
                        requireView(),
                        R.drawable.snackbar_error_16dp,
                        "서버가 불안정 하여 사료 정보 수정에 실패하였습니다.\n잠시 후 다시 시도해 주세요.",
                    ).show()
                }
            }
        }
    }

    private fun initInputMethodManager() {
        thread {
            SystemClock.sleep(300)
            inputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            hideSoftKeyboard()
        }
    }

    private fun hideSoftKeyboard() {
        if (requireActivity().currentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(
                requireActivity().currentFocus!!.windowToken,
                0,
            )
            requireActivity().currentFocus!!.clearFocus()
        }
    }

    private fun getFeedId() = arguments?.getLong("feedId")!!

    private fun getFeedRecordId() = arguments?.getLong("feedRecordId")!!

    private fun getFeedInfo() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("feedInfo", FeedDetailGetResponse::class.java)!!
        } else {
            arguments?.getParcelable("feedInfo")!!
        }

    override fun onUriPassed(uri: Uri) {
        feedPutViewModel.getImageFeed(uri)
        binding.apply {
            Glide.with(this@FeedEditFragment)
                .load(uri)
                .into(imageviewFeedaddeditPicture)
            layoutFeedaddeditImage.root.visibility = View.INVISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
