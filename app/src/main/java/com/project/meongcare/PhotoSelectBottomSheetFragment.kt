package com.project.meongcare

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.meongcare.databinding.FragmentPhotoSelectBottomSheetBinding
import com.project.meongcare.onboarding.model.data.local.PhotoMenuListener
import com.project.meongcare.onboarding.view.DogAddOnBoardingFragment
import com.project.meongcare.onboarding.viewmodel.DogAddViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

@AndroidEntryPoint
class PhotoSelectBottomSheetFragment : BottomSheetDialogFragment() {
    lateinit var fragmentPhotoSelectBottomSheetBinding: FragmentPhotoSelectBottomSheetBinding
    lateinit var mainActivity: MainActivity

    lateinit var requestCameraLauncher: ActivityResultLauncher<Intent>
    lateinit var file: File

    private var photoMenuListener: PhotoMenuListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = activity as MainActivity

        file = makeFile(mainActivity)
        createBitmap()
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
                Log.d("바텀 시트 메뉴 클릭", "album")
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
                "IMG_${timeStamp}_",
                ".jpg",
                storageDir
            )
        return file
    }

    // 카메라 앱 실행하는 인텐트 생성
    fun executeCamera(context: Context) {
        val photoURI: Uri =
            FileProvider.getUriForFile(
                context,
                "com.project.meongcare",
                file
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
                option.inSampleSize = 10
                val bitmap = BitmapFactory.decodeFile(file.absolutePath, option)
                bitmap?.let { bitmapFile ->
                    if (bitmapFile != null) {
                        sendBitmap(bitmapFile)
                    }
                    dismiss()
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
