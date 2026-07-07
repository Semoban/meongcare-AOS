package com.project.meongcare.toolbar.view

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

// 주간 달력 RecyclerView에서 좌우 스와이프를 감지해 일주일 단위 이동 콜백을 전달한다.
// onInterceptTouchEvent에서 항상 false를 반환해 날짜 아이템 클릭은 그대로 동작한다.
class WeekSwipeListener(
    context: Context,
    private val onWeekMove: (days: Int) -> Unit,
) : RecyclerView.SimpleOnItemTouchListener() {
    private val gestureDetector =
        GestureDetector(
            context,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onFling(
                    e1: MotionEvent?,
                    e2: MotionEvent,
                    velocityX: Float,
                    velocityY: Float,
                ): Boolean {
                    if (e1 == null) return false

                    val diffX = e2.x - e1.x
                    val diffY = e2.y - e1.y
                    if (abs(diffX) > abs(diffY) &&
                        abs(diffX) > SWIPE_DISTANCE_THRESHOLD &&
                        abs(velocityX) > SWIPE_VELOCITY_THRESHOLD
                    ) {
                        onWeekMove(if (diffX < 0) DAYS_IN_WEEK else -DAYS_IN_WEEK)
                        return true
                    }
                    return false
                }
            },
        )

    override fun onInterceptTouchEvent(
        rv: RecyclerView,
        e: MotionEvent,
    ): Boolean {
        gestureDetector.onTouchEvent(e)
        return false
    }

    companion object {
        const val DAYS_IN_WEEK = 7
        private const val SWIPE_DISTANCE_THRESHOLD = 100
        private const val SWIPE_VELOCITY_THRESHOLD = 100
    }
}
