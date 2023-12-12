package com.project.meongcare.onboarding.view

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.project.meongcare.CalendarBottomSheetFragment
import com.project.meongcare.MainActivity
import com.project.meongcare.PhotoSelectBottomSheetFragment
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentDogAddOnBoardingBinding
import com.project.meongcare.onboarding.model.data.local.DateSubmitListener
import com.project.meongcare.onboarding.model.data.local.PhotoMenuListener
import com.project.meongcare.onboarding.viewmodel.DogAddViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class DogAddOnBoardingFragment : Fragment(), PhotoMenuListener, DateSubmitListener {
    lateinit var fragmentDogAddOnBoardingBinding: FragmentDogAddOnBoardingBinding
    lateinit var mainActivity: MainActivity

    private val dogAddViewModel: DogAddViewModel by viewModels()

    private var isCbxChecked = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        fragmentDogAddOnBoardingBinding = FragmentDogAddOnBoardingBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        dogAddViewModel.dogBirthDate.observe(viewLifecycleOwner){ date ->
            if (date != null) {
                fragmentDogAddOnBoardingBinding.textviewPetaddSelectBirthday.run {
                    fragmentDogAddOnBoardingBinding.edittextPetaddSelectBirthdayError.visibility = View.INVISIBLE
                    text = dateFormat(date)
                    setTextAppearance(R.style.Typography_Body1_Medium)
                }
            }
        }
        // 품종 뷰모델 옵저버 내에서 에러뷰 visibility 설정 필

        fragmentDogAddOnBoardingBinding.run {
            // 품종 검색 화면 연결 전 임시 값 설정
            edittextPetaddSelectType.text = "말티즈"

            // 사진 등록
            cardviewPetaddImage.setOnClickListener {
                val modalBottomSheet = PhotoSelectBottomSheetFragment()
                modalBottomSheet.setPhotoMenuListener(this@DogAddOnBoardingFragment)
                // 둥근 모서리 지정
                modalBottomSheet.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerPhotoDialogTheme)
                modalBottomSheet.show(mainActivity.supportFragmentManager, modalBottomSheet.tag)
            }

            // 품종 등록
            viewPetaddType.setOnClickListener {
                // 품종 검색 화면으로 이동
            }

            // 날짜 등록
            imageviewPetaddBirthdayCalender.setOnClickListener {
                val calendarBottomSheet = CalendarBottomSheetFragment()
                calendarBottomSheet.setDateSubmitListener(this@DogAddOnBoardingFragment)
                calendarBottomSheet.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerCalendarDialogTheme)
                calendarBottomSheet.show(mainActivity.supportFragmentManager, calendarBottomSheet.tag)
            }

            checkboxPetaddNeuterStatus.setOnCheckedChangeListener { buttonView, isChecked ->
                isCbxChecked = isChecked
            }

            // 중성화 여부 텍스트 클릭 시 체크박스 반전
            textviewPetaddNeuterStatus.setOnClickListener {
                checkboxPetaddNeuterStatus.isChecked = !isCbxChecked
            }

            edittextPetaddNameError.setOnClickListener {
                it.visibility = View.INVISIBLE
                edittextPetaddName.requestFocus()
            }
            edittextPetaddWeightError.setOnClickListener {
                it.visibility = View.INVISIBLE
                edittextPetaddWeight.requestFocus()
            }
            edittextPetaddSelectTypeError.setOnClickListener {
                // 품종 검색 화면으로 이동
            }

            // 완료
            buttonComplete.setOnClickListener {
                // 입력 검사
                if (edittextPetaddName.text.isEmpty()) {
                    edittextPetaddNameError.visibility = View.VISIBLE
                    return@setOnClickListener
                }

                if (edittextPetaddSelectType.text.isEmpty()) {
                    edittextPetaddSelectTypeError.visibility = View.VISIBLE
                    return@setOnClickListener
                }

                if (textviewPetaddSelectBirthday.text.isEmpty()) {
                    edittextPetaddSelectBirthdayError.visibility = View.VISIBLE
                    return@setOnClickListener
                }

                if (edittextPetaddWeight.text.isEmpty()) {
                    edittextPetaddWeightError.visibility = View.VISIBLE
                    return@setOnClickListener
                }

            }
        }

        return fragmentDogAddOnBoardingBinding.root
    }

    override fun onBitmapPassed(bitmap: Bitmap) {
        dogAddViewModel.getDogProfileImage(bitmap)

        fragmentDogAddOnBoardingBinding.run {
            imageviewPetaddImage.setImageBitmap(bitmap)
            imageviewPetaddDog.visibility = View.GONE
            textviewPetaddImageDescription.visibility = View.GONE
        }
    }

    override fun onDateSubmit(str: String) {
        dogAddViewModel.getDogBirthDate(str)
    }

    fun dateFormat(str: String): String {
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val outputDateFormat = SimpleDateFormat("yyyy년 MM월 dd일")

        val parsedDate = inputDateFormat.parse(str)
        return outputDateFormat.format(parsedDate)
    }
}
