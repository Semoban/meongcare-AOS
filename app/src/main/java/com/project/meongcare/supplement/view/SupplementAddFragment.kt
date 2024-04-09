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
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.meongcare.BuildConfig
import com.project.meongcare.MainActivity
import com.project.meongcare.R
import com.project.meongcare.aws.util.AWSS3ImageUtils.convertUriToFile
import com.project.meongcare.aws.util.PARENT_FOLDER_PATH
import com.project.meongcare.aws.util.SUPPLEMENTS_FOLDER_PATH
import com.project.meongcare.aws.viewmodel.AWSS3ViewModel
import com.project.meongcare.databinding.FragmentSupplementAddBinding
import com.project.meongcare.databinding.ItemSupplementAddTimeBinding
import com.project.meongcare.medicalRecord.viewmodel.DogViewModel
import com.project.meongcare.medicalRecord.viewmodel.UserViewModel
import com.project.meongcare.snackbar.view.CustomSnackBar
import com.project.meongcare.supplement.model.data.local.OnPictureChangedListener
import com.project.meongcare.supplement.model.data.repository.SupplementRepository
import com.project.meongcare.supplement.model.entities.SupplementPostRequest
import com.project.meongcare.supplement.utils.SupplementUtils.Companion.convertDateToTime
import com.project.meongcare.supplement.utils.SupplementUtils.Companion.hideKeyboard
import com.project.meongcare.supplement.utils.SupplementUtils.Companion.showCycleBottomSheet
import com.project.meongcare.supplement.utils.SupplementUtils.Companion.showTimeBottomSheet
import com.project.meongcare.supplement.view.bottomSheet.SupplementPictureBottomSheetDialogFragment
import com.project.meongcare.supplement.viewmodel.SupplementViewModel
import com.project.meongcare.supplement.viewmodel.SupplementViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@AndroidEntryPoint
class SupplementAddFragment : Fragment(), OnPictureChangedListener {
    lateinit var fragmentSupplementAddBinding: FragmentSupplementAddBinding
    lateinit var mainActivity: MainActivity
    lateinit var supplementViewModel: SupplementViewModel
    lateinit var navController: NavController

    private val awsS3ViewModel: AWSS3ViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val dogViewModel: DogViewModel by viewModels()

    private lateinit var imageFile: File
    private lateinit var filePath: String

    private var accessToken = ""
    private var dogId = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentSupplementAddBinding = FragmentSupplementAddBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        navController = findNavController()
        getAccessToken()

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

            supplementViewModel.supplementCode.observe(viewLifecycleOwner) {
                if (it == 200) {
                    showSuccessSnackbar()
                    navController.popBackStack()
                } else {
                    showFailSnackbar()
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
                    val uri = supplementViewModel.supplementAddImg.value
                    if (uri == null) {
                        postSupplement(null)
                    } else {
                        getPreSignedUrl(uri)
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

    private fun getAccessToken() {
        userViewModel.accessTokenPreferencesLiveData.observe(viewLifecycleOwner) { accessToken ->
            if (accessToken != null) {
                this.accessToken = accessToken
                getDogId()
            }
        }
    }

    private fun getDogId() {
        dogViewModel.dogIdPreferencesLiveData.observe(viewLifecycleOwner) { dogId ->
            if (dogId != null) {
                this.dogId = dogId
            }
        }
    }

    private fun getPreSignedUrl(uri: Uri) {
        imageFile = convertUriToFile(requireContext(), uri)
        filePath = "$PARENT_FOLDER_PATH$SUPPLEMENTS_FOLDER_PATH${imageFile.name}"
        awsS3ViewModel.getPreSignedUrl(accessToken, filePath)
        awsS3ViewModel.preSignedUrl.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                val requestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                uploadImage(response.preSignedUrl, requestBody)
            }
        }
    }

    private fun uploadImage(preSignedUrl: String, requestBody: RequestBody) {
        awsS3ViewModel.uploadImageToS3(preSignedUrl, requestBody)
        awsS3ViewModel.uploadImageResponse.observe(viewLifecycleOwner) { response ->
            if (response == 200) {
                val imageURL = BuildConfig.AWS_S3_BASE_URL + filePath
                postSupplement(imageURL)
            }
        }
    }

    private fun postSupplement(imageURL: String?) {
        supplementViewModel.addSupplement(
            accessToken,
            createSupplementInfo(imageURL)
        )
    }

    private fun createSupplementInfo(imageURL: String?): SupplementPostRequest {
        val brandName = fragmentSupplementAddBinding.editTextSupplementAddBrandName.text.toString()
        val name = fragmentSupplementAddBinding.editTextSupplementAddName.text.toString()
        val intakeCycle = supplementViewModel.supplementCycle.value!!
        val intakeUnit = supplementViewModel.intakeTimeUnit.value!!
        val intakeInfos = supplementViewModel.intakeTimeList.value!!
        return SupplementPostRequest(dogId, brandName, name, intakeCycle, intakeUnit, imageURL, intakeInfos)
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

    private fun showSuccessSnackbar() {
        CustomSnackBar.make(
            activity?.findViewById(android.R.id.content)!!,
            R.drawable.snackbar_success_16dp,
            "추가가 완료되었습니다",
        ).show()
    }

    private fun showFailSnackbar() {
        CustomSnackBar.make(
            activity?.findViewById(android.R.id.content)!!,
            R.drawable.snackbar_error_16dp,
            "추가에 실패하였습니다.\n잠시 후 다시 시도해주세요",
        ).show()
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
