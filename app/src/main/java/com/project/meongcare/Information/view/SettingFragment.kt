package com.project.meongcare.Information.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.project.meongcare.Information.viewmodel.ProfileViewModel
import com.project.meongcare.R
import com.project.meongcare.databinding.FragmentSettingBinding
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

        binding.run {
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
                    val accessToken = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MywiZXhwIjoxNzA0NjU0MzQwfQ.gnACPYtCCD7pBMQtj8lQNMfJurfzFW837r43Xlh70X4"
                    settingViewModel.deleteUser(accessToken)
                }
            }

            buttonSettingNotification.setOnClickListener {
                switchSettingNotification.isChecked = !switchSettingNotification.isChecked
            }
        }

        return binding.root
    }
}
