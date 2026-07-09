package com.project.meongcare.feed.view

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.project.meongcare.BuildConfig
import com.project.meongcare.PhotoSelectBottomSheetFragment
import com.project.meongcare.aws.util.AWSS3ImageUtils.convertUriToFile
import com.project.meongcare.aws.util.FEED_FOLDER_PATH
import com.project.meongcare.aws.util.PARENT_FOLDER_PATH
import com.project.meongcare.aws.viewmodel.AWSS3ViewModel
import com.project.meongcare.databinding.FragmentFeedAddEditBinding
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.excreta.utils.SUCCESS
import com.project.meongcare.feed.model.entities.FeedDetailGetResponse
import com.project.meongcare.feed.model.entities.FeedPutRequest
import com.project.meongcare.feed.model.utils.FEED_PUT_FAILURE
import com.project.meongcare.feed.model.utils.FEED_PUT_SUCCESS
import com.project.meongcare.feed.model.utils.FeedInfoUtils.showFailureSnackBar
import com.project.meongcare.feed.model.utils.FeedInfoUtils.showSuccessSnackBar
import com.project.meongcare.feed.viewmodel.DogViewModel
import com.project.meongcare.feed.viewmodel.FeedPutViewModel
import com.project.meongcare.feed.viewmodel.UserViewModel
import com.project.meongcare.onboarding.model.data.local.PhotoMenuListener
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@AndroidEntryPoint
class FeedEditFragment : Fragment(), PhotoMenuListener {
    private var _binding: FragmentFeedAddEditBinding? = null
    private val binding get() = _binding!!

    private lateinit var imageFile: File
    private lateinit var filePath: String
    private lateinit var feedInfo: FeedDetailGetResponse

    private val feedPutViewModel: FeedPutViewModel by viewModels()
    private val dogViewModel: DogViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val awsS3ViewModel: AWSS3ViewModel by viewModels()

    private var feedId = 0L
    private var feedRecordId = 0L
    private var accessToken = ""
    private var feedForm: FeedFormResult? = null

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
        feedId = getFeedId()
        feedRecordId = getFeedRecordId()
        feedInfo = getFeedInfo()
        initComposeView()
        fetchUserAndDogInfo()
        observeImageUpload()
        observeFeedPut()
    }

    private fun initComposeView() {
        binding.composeViewFeedAddEdit.run {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SemobanTheme {
                    val imageUri by feedPutViewModel.feedImage.observeAsState()
                    val weight by dogViewModel.dogWeight.observeAsState()

                    FeedAddEditScreen(
                        initialFeedInfo = feedInfo,
                        imageModel = imageUri ?: feedInfo.imageURL,
                        dogWeight = weight ?: 0.0,
                        onBackClick = { findNavController().popBackStack() },
                        onImageClick = ::showPhotoAttachModalBottomSheet,
                        onComplete = ::submitFeed,
                    )
                }
            }
        }
    }

    private fun fetchUserAndDogInfo() {
        userViewModel.fetchAccessToken()
        userViewModel.accessToken.observe(viewLifecycleOwner) { response ->
            accessToken = response
        }
        dogViewModel.fetchDogWeight()
    }

    private fun showPhotoAttachModalBottomSheet() {
        val photoAttachModalBottomSheet = PhotoSelectBottomSheetFragment()
        photoAttachModalBottomSheet.setPhotoMenuListener(this@FeedEditFragment)
        photoAttachModalBottomSheet.show(
            requireActivity().supportFragmentManager,
            PhotoSelectBottomSheetFragment.TAG,
        )
    }

    private fun submitFeed(form: FeedFormResult) {
        feedForm = form
        val uri = feedPutViewModel.feedImage.value
        if (uri == null) {
            putFeed(feedInfo.imageURL)
        } else {
            getPreSignedUrl(uri)
        }
    }

    private fun putFeed(imageURL: String?) {
        val form = feedForm ?: return
        val feedPutRequest =
            FeedPutRequest(
                feedId,
                form.brand,
                form.feedName,
                form.protein,
                form.fat,
                form.crudeAsh,
                form.moisture,
                form.etc,
                form.kcal,
                form.recommendIntake,
                form.startDate,
                form.endDate,
                feedRecordId,
                imageURL,
            )
        feedPutViewModel.putFeed(accessToken, feedPutRequest)
    }

    private fun observeFeedPut() {
        feedPutViewModel.feedPut.observe(viewLifecycleOwner) { response ->
            if (response == null) return@observe
            if (response == SUCCESS) {
                findNavController().popBackStack()
                showSuccessSnackBar(
                    requireView(),
                    FEED_PUT_SUCCESS,
                )
            } else {
                showFailureSnackBar(
                    requireView(),
                    FEED_PUT_FAILURE,
                )
            }
        }
    }

    private fun getPreSignedUrl(uri: Uri) {
        imageFile = convertUriToFile(requireContext(), uri)
        filePath = "$PARENT_FOLDER_PATH$FEED_FOLDER_PATH${imageFile.name}"
        awsS3ViewModel.getPreSignedUrl(accessToken, filePath)
    }

    private fun observeImageUpload() {
        awsS3ViewModel.preSignedUrl.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                val requestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                awsS3ViewModel.uploadImageToS3(response.preSignedUrl, requestBody)
            }
        }
        awsS3ViewModel.uploadImageResponse.observe(viewLifecycleOwner) { response ->
            if (response == 200) {
                val imageURL = BuildConfig.AWS_S3_BASE_URL + filePath
                putFeed(imageURL)
            }
        }
    }

    override fun onUriPassed(uri: Uri) {
        feedPutViewModel.getImageFeed(uri)
    }

    private fun getFeedId() = arguments?.getLong("feedId")!!

    private fun getFeedRecordId() = arguments?.getLong("feedRecordId")!!

    private fun getFeedInfo(): FeedDetailGetResponse =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("feedInfo", FeedDetailGetResponse::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable("feedInfo")!!
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
