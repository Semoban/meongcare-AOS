package com.project.meongcare.excreta.view

import android.app.Activity
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
import com.project.meongcare.databinding.FragmentPhotoSelectBottomSheetBinding
import com.project.meongcare.excreta.model.data.local.PhotoListener
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PhotoAttachModalBottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentPhotoSelectBottomSheetBinding? = null
    val binding get() = _binding!!

    private var photoListener: PhotoListener? = null
    private lateinit var photoURI: Uri
    lateinit var file: File

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPhotoSelectBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            textviewSelectCamera.setOnClickListener {
                openCamera()
            }
            textviewSelectAlbum.setOnClickListener {
                openAlbum()
            }
        }
    }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                sendUri(photoURI)
                dismiss()
            }
        }

    private val albumLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    sendUri(uri)
                    dismiss()
                }
            }
        }

    private fun photoFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File =
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.let {
                File(it, "Excreta")
            } ?: throw Exception("Cannot access external storage.")

        storageDir.mkdirs()
        return File.createTempFile(
            "EXCRETA_${timeStamp}_",
            ".jpg",
            storageDir,
        )
    }

    private fun openCamera() {
        val file = photoFile()
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

    private fun openAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        albumLauncher.launch(intent)
    }

    private fun sendUri(uri: Uri) {
        photoListener?.onUriPassed(uri)
    }

    fun setPhotoListener(listener: PhotoListener) {
        this.photoListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}
