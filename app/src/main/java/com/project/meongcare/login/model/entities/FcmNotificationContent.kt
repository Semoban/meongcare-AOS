package com.project.meongcare.login.model.entities

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

// FCM data 페이로드 분류. 서버 계약상 구조화 키가 온전한 경우에만 타입별 콘텐츠로 매핑되고,
// 재전송 알림(타입별 추가 키 없음)·알 수 없는 타입·키 누락 시에는
// 서버가 만든 한국어 완성 문구(title/body)를 그대로 쓰는 Fallback이 된다.
sealed interface FcmNotificationContent {
    data class Supplements(
        val dogName: String,
        val supplementsName: String,
        val intakeTime: LocalTime,
    ) : FcmNotificationContent

    data class ShareDog(
        val dogName: String,
        val requesterEmail: String,
    ) : FcmNotificationContent

    data class Fallback(
        val title: String?,
        val body: String?,
    ) : FcmNotificationContent

    companion object {
        private const val TYPE_SUPPLEMENTS = "SUPPLEMENTS"
        private const val TYPE_SHARE_DOG = "SHARE_DOG"

        private val intakeTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        fun from(data: Map<String, String>): FcmNotificationContent {
            val fallback = Fallback(data["title"], data["body"])
            return when (data["notificationType"]) {
                TYPE_SUPPLEMENTS -> {
                    val dogName = data["dogName"] ?: return fallback
                    val supplementsName = data["supplementsName"] ?: return fallback
                    val intakeTime = data["intakeTime"]?.toLocalTimeOrNull() ?: return fallback
                    Supplements(dogName, supplementsName, intakeTime)
                }
                TYPE_SHARE_DOG -> {
                    val dogName = data["dogName"] ?: return fallback
                    val requesterEmail = data["requesterEmail"] ?: return fallback
                    ShareDog(dogName, requesterEmail)
                }
                else -> fallback
            }
        }

        private fun String.toLocalTimeOrNull(): LocalTime? {
            return try {
                LocalTime.parse(this, intakeTimeFormatter)
            } catch (e: DateTimeParseException) {
                null
            }
        }
    }
}
