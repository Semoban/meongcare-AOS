package com.project.meongcare.symptom.view

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.project.meongcare.R
import com.project.meongcare.designsystem.theme.SemobanTheme
import com.project.meongcare.snackbar.view.CustomSnackBar

class SymptomSelectFragment : Fragment() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        editor = sharedPreferences.edit()

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SemobanTheme {
                    SymptomSelectScreen(
                        onCancel = { findNavController().popBackStack() },
                        onEmptySelection = ::showEmptySelectionSnackbar,
                        onSelected = ::saveSelectedSymptom,
                    )
                }
            }
        }
    }

    private fun saveSelectedSymptom(
        imgRes: Int,
        title: String,
    ) {
        editor.putInt("symptomItemImgId", imgRes)
        editor.putString("symptomItemTitle", title)
        editor.apply()
        findNavController().popBackStack()
    }

    private fun showEmptySelectionSnackbar() {
        CustomSnackBar.make(
            activity?.findViewById(android.R.id.content)!!,
            R.drawable.snackbar_error_16dp,
            getString(R.string.symptom_select_required),
        ).show()
    }
}
