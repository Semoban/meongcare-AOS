package com.project.meongcare.onboarding.util

import android.view.View
import com.google.android.material.chip.Chip
import com.project.meongcare.onboarding.model.entities.Gender

object DogAddOnBoardingInfoUtils {
    fun getCheckedGender(
        view: View,
        checkedChipId: Int,
    ): String {
        val checkedChip = view.findViewById<Chip>(checkedChipId)
        return if (checkedChip.text.toString() == Gender.FEMALE.korean) Gender.FEMALE.english else Gender.MALE.english
    }

    fun bodySizeCheck(str: String): Double? {
        return if (str.isEmpty()) null else str.toDouble()
    }
}
