package com.project.meongcare.onboarding.model.entities

// 화면 표시 문자열은 R.string.petadd_female/petadd_male을 사용한다 (FormComponents.GenderChip)
enum class Gender(val english: String) {
    MALE("male"),
    FEMALE("female"),
}
