package com.project.meongcare.info.view

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentSettingBinding
import com.project.meongcare.info.viewmodel.ProfileViewModel
import com.project.meongcare.login.model.data.local.UserPreferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment : Fragment() {
    private lateinit var binding: FragmentSettingBinding

    private val settingViewModel: ProfileViewModel by viewModels()

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSettingBinding.inflate(inflater)

        settingViewModel.userDeleteResponse.observe(viewLifecycleOwner) { response ->
            if (response != null && response == 200) {
                userPreferences.setProvider(null)
                findNavController().navigate(R.id.action_settingFragment_to_loginFragment)
            }
        }

        settingViewModel.patchPushResponse.observe(viewLifecycleOwner) { response ->
            if (response == 200) {
                when (binding.switchSettingNotification.isChecked) {
                    true -> makeSnackBar("알림 수신 처리되었습니다.")
                    false -> makeSnackBar("알림 거부 처리되었습니다.")
                }
            }
        }

        val accessToken = ""
        binding.run {
            imagebuttonSettingBack.setOnClickListener {
                findNavController().popBackStack()
            }

            textviewSettingMembershipWithdrawal.setOnClickListener {
                includeDeleteAccountDialog.root.visibility = View.VISIBLE
            }

            includeDeleteAccountDialog.run {
                constraintlayoutBg.setOnClickListener {
                    includeDeleteAccountDialog.root.visibility = View.GONE
                }
                cardviewDeleteAccountDialog.setOnClickListener {
                    includeDeleteAccountDialog.root.visibility = View.VISIBLE
                }
                buttonDeleteAccountDialogCancel.setOnClickListener {
                    includeDeleteAccountDialog.root.visibility = View.GONE
                }
                buttonDeleteAccountDialogDelete.setOnClickListener {
                    settingViewModel.deleteUser(accessToken)
                }
            }

            buttonSettingNotification.setOnClickListener {
                switchSettingNotification.isChecked = !switchSettingNotification.isChecked
            }

            switchSettingNotification.setOnCheckedChangeListener { buttonView, isChecked ->
                settingViewModel.patchPushAgreement(isChecked, accessToken)
            }
        }

        return binding.root
    }

    fun makeSnackBar(message: String) {
        val snackBar = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
        val snackBarLayout = snackBar.view as Snackbar.SnackbarLayout

        val imageView = ImageView(context)
        imageView.setImageResource(R.drawable.all_snack_bar_complete)
        val imageViewStartPadding = resources.getDimensionPixelSize(R.dimen.snackbar_image_start_padding)
        imageView.setPadding(imageViewStartPadding, 0, 0, 0)

        val params =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
            )
        params.gravity = Gravity.CENTER_VERTICAL or Gravity.START
        snackBarLayout.addView(imageView, 0, params)

        val textView = snackBarLayout.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)

        val textViewStartPadding = resources.getDimensionPixelSize(R.dimen.snackbar_text_start_padding)
        textView.setPadding(textViewStartPadding, 0, 0, 0)

        snackBar.show()
    }
}
