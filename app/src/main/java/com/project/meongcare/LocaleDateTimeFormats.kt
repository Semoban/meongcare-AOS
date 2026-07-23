package com.project.meongcare

import androidx.annotation.StringRes
import com.project.meongcare.login.view.GlobalApplication
import java.time.format.DateTimeFormatter

// 화면 표시용 날짜/시간 포매터. 패턴을 로케일별 리소스(strings-datetime.xml)에서 읽어
// 기기 언어에 맞는 형식으로 표시한다. 서버 통신용 포맷(yyyy-MM-dd 등)에는 사용하지 않는다.
object LocaleDateTimeFormats {
    fun datePadded(): DateTimeFormatter = ofPatternRes(R.string.datetime_pattern_date_padded)

    fun date(): DateTimeFormatter = ofPatternRes(R.string.datetime_pattern_date)

    fun monthDay(): DateTimeFormatter = ofPatternRes(R.string.datetime_pattern_month_day)

    fun time12h(): DateTimeFormatter = ofPatternRes(R.string.datetime_pattern_time_12h)

    fun time12hShort(): DateTimeFormatter = ofPatternRes(R.string.datetime_pattern_time_12h_short)

    private fun ofPatternRes(
        @StringRes patternRes: Int,
    ): DateTimeFormatter = DateTimeFormatter.ofPattern(GlobalApplication.applicationContext().getString(patternRes))
}
