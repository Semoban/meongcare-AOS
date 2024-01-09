package com.project.meongcare.supplement.view.bottomSheet

import android.app.Activity
import android.app.Dialog
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
import com.project.meongcare.databinding.BottomsheetSupplementAddPictureBinding
import com.project.meongcare.supplement.model.data.local.OnPictureChangedListener
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SupplementPictureBottomSheetDialogFragment : BottomSheetDialogFragment() {
    lateinit var fragmentSupplementPictureBottomSheetBinding: BottomsheetSupplementAddPictureBinding
    private lateinit var photoURI: Uri
    lateinit var file: File

    private var onPictureChangedListener: OnPictureChangedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentSupplementPictureBottomSheetBinding =
            BottomsheetSupplementAddPictureBinding.inflate(inflater, container, false)
        return fragmentSupplementPictureBottomSheetBinding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        fragmentSupplementPictureBottomSheetBinding.run {
            textViewSupplementAddPictureCamera.setOnClickListener {
                openCamera()
            }
            textViewSupplementAddPictureGallery.setOnClickListener {
                openAlbum()
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        val peekHeightInPixels = 0

        val behavior = dialog.behavior
        if (behavior != null) {
            behavior.peekHeight = peekHeightInPixels
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        return dialog
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
                File(it, "Supplement")
            } ?: throw Exception("Cannot access external storage.")

        storageDir.mkdirs()
        return File.createTempFile(
            "SUPPLEMENT_${timeStamp}_",
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
        onPictureChangedListener?.onPictureChanged(uri)
    }

    fun setOnPictureChangedListener(listener: OnPictureChangedListener) {
        this.onPictureChangedListener = listener
    }

}
