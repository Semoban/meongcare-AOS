package com.project.meongcare

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.meongcare.databinding.FragmentPhotoSelectBottomSheetBinding
import com.project.meongcare.onboarding.model.data.local.PhotoMenuListener
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

@AndroidEntryPoint
class PhotoSelectBottomSheetFragment : BottomSheetDialogFragment() {
    lateinit var fragmentPhotoSelectBottomSheetBinding: FragmentPhotoSelectBottomSheetBinding
    lateinit var mainActivity: MainActivity

    lateinit var requestCameraLauncher: ActivityResultLauncher<Intent>
    lateinit var requestAlbumLauncher: ActivityResultLauncher<Intent>
    lateinit var file: File

    private var photoMenuListener: PhotoMenuListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = activity as MainActivity

        file = makeFile(mainActivity)
        createBitmap()
        createAlbumBitmap()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentPhotoSelectBottomSheetBinding = FragmentPhotoSelectBottomSheetBinding.inflate(inflater)

        fragmentPhotoSelectBottomSheetBinding.run {
            textviewSelectCamera.setOnClickListener {
                executeCamera(mainActivity)
            }
            textviewSelectAlbum.setOnClickListener {
                executeAlbum()
            }
        }

        return fragmentPhotoSelectBottomSheetBinding.root
    }

    // 사진 저장할 파일 만들기
    fun makeFile(context: Context): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file =
            File.createTempFile(
                "DOGPROFILE_${timeStamp}_",
                ".jpg",
                storageDir,
            )
        return file
    }

    // 카메라 앱 실행하는 인텐트 생성
    fun executeCamera(context: Context) {
        val photoURI: Uri =
            FileProvider.getUriForFile(
                context,
                "com.project.meongcare",
                file,
            )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        requestCameraLauncher.launch(intent)
    }

    // 비트맵 생성
    fun createBitmap() {
        requestCameraLauncher =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult(),
            ) {
                val option = BitmapFactory.Options()
                option.inSampleSize = 5
                val bitmap = BitmapFactory.decodeFile(file.absolutePath, option)
                bitmap?.let { bitmapFile ->
                    if (bitmapFile != null) {
                        sendBitmap(bitmapFile)
                    }
                    dismiss()
                }
            }
    }

    fun executeAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setType("image/*")
        requestAlbumLauncher.launch(intent)
    }

    fun createAlbumBitmap() {
        requestAlbumLauncher =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult(),
            ) {
                val option = BitmapFactory.Options()
                option.inSampleSize = 5

                if (it.resultCode == Activity.RESULT_OK) {
                    it.data?.data?.let { uri ->
                        if (uri != null) {
                            val inputStream = mainActivity.contentResolver.openInputStream(uri)
                            val bitmap = BitmapFactory.decodeStream(inputStream, null, option)
                            inputStream?.close()
                            bitmap?.let { bitmapFile ->
                                if (bitmapFile != null) {
                                    sendBitmap(bitmapFile)
                                }
                                dismiss()
                            }
                        }
                    }
                }
            }
    }

    private fun sendBitmap(bitmap: Bitmap) {
        photoMenuListener?.onBitmapPassed(bitmap)
    }

    fun setPhotoMenuListener(listener: PhotoMenuListener) {
        this.photoMenuListener = listener
    }
}
