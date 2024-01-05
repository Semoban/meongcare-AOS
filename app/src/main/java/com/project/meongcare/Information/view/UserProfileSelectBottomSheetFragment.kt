package com.project.meongcare.Information.view

import android.app.Activity
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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.meongcare.MainActivity
import com.project.meongcare.databinding.FragmentPhotoSelectBottomSheetBinding
import com.project.meongcare.onboarding.model.data.local.PhotoMenuListener
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

class UserProfileSelectBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentPhotoSelectBottomSheetBinding
    private lateinit var mainActivity: MainActivity

    private var photoMenuListener: PhotoMenuListener? = null
    private lateinit var photoURI: Uri

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPhotoSelectBottomSheetBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        binding.run {
            textviewSelectCamera.setOnClickListener {
                executeCamera(mainActivity)
            }
            textviewSelectAlbum.setOnClickListener {
                executeAlbum()
            }
        }

        return binding.root
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
                it.data?.data?.let {  uri ->
                    if (uri != null) {
                        sendUri(uri)
                    }
                    dismiss()
                }
            }
        }

    private fun makeProfileFile(context: Context): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "UserProfile")

        // 디렉토리 존재하지 않으면 생성
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }

        return File.createTempFile(
            "USERPROFILE_${timeStamp}_",
            ".jpg",
            storageDir,
        )
    }

    private fun executeCamera(context: Context) {
        val file = makeProfileFile(context)
        photoURI =
            FileProvider.getUriForFile(
                context,
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

    private fun sendUri(uri: Uri) {
        photoMenuListener?.onUriPassed(uri)
    }

    fun setPhotoMenuListener(listener: PhotoMenuListener) {
        this.photoMenuListener = listener
    }
}
