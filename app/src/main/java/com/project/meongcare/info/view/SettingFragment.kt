package com.project.meongcare.info.view

import android.content.Context
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentSettingBinding
import com.project.meongcare.info.viewmodel.ProfileViewModel
import com.project.meongcare.login.model.data.local.UserPreferences
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment : Fragment() {
    private lateinit var binding: FragmentSettingBinding
    private lateinit var currentAccessToken: String

    private val settingViewModel: ProfileViewModel by viewModels()
    private var pushAgreement = false

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pushAgreement = arguments?.getBoolean("pushAgreement")!!
        getAccessToken()
    }

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
                    true -> makeSnackBar(binding.root, requireContext(), "알림 수신 처리되었습니다.")
                    false -> makeSnackBar(binding.root, requireContext(), "알림 거부 처리되었습니다.")
                }
            }
        }

        binding.run {
            switchSettingNotification.run {
                isChecked = pushAgreement
            }

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
                    settingViewModel.deleteUser(currentAccessToken)
                }
            }

            buttonSettingNotification.setOnClickListener {
                switchSettingNotification.isChecked = !switchSettingNotification.isChecked
            }

            switchSettingNotification.setOnCheckedChangeListener { buttonView, isChecked ->
                settingViewModel.patchPushAgreement(isChecked, currentAccessToken)
            }
        }

        return binding.root
    }

    private fun getAccessToken() {
        lifecycleScope.launch {
            userPreferences.accessToken.collectLatest { accessToken ->
                if (accessToken != null) {
                    currentAccessToken = accessToken
                }
            }
        }
    }
}

fun makeSnackBar(
    view: View,
    context: Context,
    message: String,
) {
    val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
    val snackBarLayout = snackBar.view as Snackbar.SnackbarLayout

    val imageView = ImageView(context)
    imageView.setImageResource(R.drawable.all_snack_bar_complete)
    val imageViewStartPadding = context.resources.getDimensionPixelSize(R.dimen.snackbar_image_start_padding)
    imageView.setPadding(imageViewStartPadding, 0, 0, 0)

    val params =
        LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.MATCH_PARENT,
        )
    params.gravity = Gravity.CENTER_VERTICAL or Gravity.START
    snackBarLayout.addView(imageView, 0, params)

    val textView = snackBarLayout.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)

    val textViewStartPadding = context.resources.getDimensionPixelSize(R.dimen.snackbar_text_start_padding)
    textView.setPadding(textViewStartPadding, 0, 0, 0)

    snackBar.show()
}
