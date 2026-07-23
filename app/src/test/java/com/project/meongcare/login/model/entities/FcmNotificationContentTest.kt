package com.project.meongcare.login.model.entities

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalTime

class FcmNotificationContentTest {
    private val commonKeys =
        mapOf(
            "title" to "서버 완성 제목",
            "body" to "서버 완성 본문",
            "logoImageUrl" to "",
            "dogId" to "1",
        )

    @Test
    fun `SUPPLEMENTS 페이로드는 구조화 키가 온전하면 Supplements로 분류된다`() {
        val data =
            commonKeys +
                mapOf(
                    "notificationType" to "SUPPLEMENTS",
                    "dogName" to "몽실이",
                    "supplementsName" to "오메가3",
                    "intakeTime" to "20:00",
                )

        val content = FcmNotificationContent.from(data)

        assertEquals(
            FcmNotificationContent.Supplements("몽실이", "오메가3", LocalTime.of(20, 0)),
            content,
        )
    }

    @Test
    fun `SHARE_DOG 페이로드는 구조화 키가 온전하면 ShareDog로 분류된다`() {
        val data =
            commonKeys +
                mapOf(
                    "notificationType" to "SHARE_DOG",
                    "dogName" to "몽실이",
                    "requesterEmail" to "someone@example.com",
                )

        val content = FcmNotificationContent.from(data)

        assertEquals(
            FcmNotificationContent.ShareDog("몽실이", "someone@example.com"),
            content,
        )
    }

    @Test
    fun `재전송 알림처럼 타입별 키가 없으면 서버 완성 문구 Fallback이 된다`() {
        val data = commonKeys + mapOf("notificationType" to "SUPPLEMENTS")

        val content = FcmNotificationContent.from(data)

        assertEquals(
            FcmNotificationContent.Fallback("서버 완성 제목", "서버 완성 본문"),
            content,
        )
    }

    @Test
    fun `알 수 없는 타입(ANNOUNCEMENT 등)은 Fallback이 된다`() {
        val data = commonKeys + mapOf("notificationType" to "ANNOUNCEMENT")

        val content = FcmNotificationContent.from(data)

        assertEquals(
            FcmNotificationContent.Fallback("서버 완성 제목", "서버 완성 본문"),
            content,
        )
    }

    @Test
    fun `notificationType이 없으면 Fallback이 된다`() {
        val content = FcmNotificationContent.from(commonKeys)

        assertEquals(
            FcmNotificationContent.Fallback("서버 완성 제목", "서버 완성 본문"),
            content,
        )
    }

    @Test
    fun `intakeTime이 HHmm 형식이 아니면 Fallback이 된다`() {
        val data =
            commonKeys +
                mapOf(
                    "notificationType" to "SUPPLEMENTS",
                    "dogName" to "몽실이",
                    "supplementsName" to "오메가3",
                    "intakeTime" to "오후 8시",
                )

        val content = FcmNotificationContent.from(data)

        assertEquals(
            FcmNotificationContent.Fallback("서버 완성 제목", "서버 완성 본문"),
            content,
        )
    }
}
