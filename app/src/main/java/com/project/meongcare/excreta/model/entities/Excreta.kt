package com.project.meongcare.excreta.model.entities

import androidx.annotation.StringRes
import com.project.meongcare.R

// 서버 통신에는 enum 이름(FECES/URINE)을 쓰고, labelRes는 화면 표시 전용이다
enum class Excreta(
    @StringRes val labelRes: Int,
) {
    FECES(R.string.excreta_feces),
    URINE(R.string.excreta_urine),
}
