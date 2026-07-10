package com.project.meongcare

import android.app.Activity
import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.meongcare.databinding.FragmentPhotoSelectBottomSheetBinding
import com.project.meongcare.onboarding.model.data.local.PhotoMenuListener
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 카메라 촬영·앨범 선택으로 사진 Uri를 전달하는 공용 바텀시트.
// setDefaultImageRes()를 호출하면 "기본 이미지로 변경" 메뉴가 추가로 노출된다.
class PhotoSelectBottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentPhotoSelectBottomSheetBinding? = null
    private val binding get() = _binding!!

    private var photoMenuListener: PhotoMenuListener? = null
    private var defaultImageRes: Int? = null
    private lateinit var photoURI: Uri

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPhotoSelectBottomSheetBinding.inflate(inflater, container, false)

        binding.run {
            textviewSelectCamera.setOnClickListener {
                executeCamera()
            }
            textviewSelectAlbum.setOnClickListener {
                executeAlbum()
            }
            if (defaultImageRes != null) {
                divider2.visibility = View.VISIBLE
                textviewSelectDefault.visibility = View.VISIBLE
                textviewSelectDefault.setOnClickListener {
                    sendDefaultImage()
                }
            }
        }

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.behavior.run {
            peekHeight = 0
            state = BottomSheetBehavior.STATE_EXPANDED
        }
        return dialog
    }

    private val cameraLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                sendUri(photoURI)
                dismiss()
            }
        }

    private val albumLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.data?.let { uri ->
                    sendUri(uri)
                    dismiss()
                }
            }
        }

    // 카메라 촬영본을 저장할 임시 파일 만들기
    private fun makePhotoFile(context: Context): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Photo")

        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }

        return File.createTempFile(
            "PHOTO_${timeStamp}_",
            ".jpg",
            storageDir,
        )
    }

    private fun executeCamera() {
        val file = makePhotoFile(requireContext())
        photoURI =
            FileProvider.getUriForFile(
                requireContext(),
                "com.project.meongcare",
                file,
            )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        cameraLauncher.launch(intent)
    }

    private fun executeAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setType("image/*")
        albumLauncher.launch(intent)
    }

    private fun sendDefaultImage() {
        val resId = defaultImageRes ?: return
        val defaultImageUri =
            requireContext().resources.let { resources ->
                Uri.Builder()
                    .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                    .authority(resources.getResourcePackageName(resId))
                    .appendPath(resources.getResourceTypeName(resId))
                    .appendPath(resources.getResourceEntryName(resId))
                    .build()
            }
        sendUri(defaultImageUri)
        dismiss()
    }

    private fun sendUri(uri: Uri) {
        photoMenuListener?.onUriPassed(uri)
    }

    fun setPhotoMenuListener(listener: PhotoMenuListener) {
        this.photoMenuListener = listener
    }

    // "기본 이미지로 변경" 메뉴를 노출하고 선택 시 해당 리소스 Uri를 전달한다
    fun setDefaultImageRes(resId: Int) {
        this.defaultImageRes = resId
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "PhotoSelectBottomSheetFragment"
    }
}
