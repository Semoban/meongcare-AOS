package com.project.meongcare.onboarding.model.data.repository

import android.content.Context
import android.content.res.Configuration
import com.project.meongcare.R
import java.text.Collator
import java.util.Locale
import javax.inject.Inject

class DogTypeRepository
    @Inject
    constructor(
        private val context: Context,
    ) {
        // dog_types 배열은 로케일 간 인덱스가 정렬되어 있어 같은 위치의 한/영 이름이 같은 품종이다.
        private val koreanTypes: List<String> by lazy { loadDogTypes(Locale.KOREAN) }
        private val englishTypes: List<String> by lazy { loadDogTypes(Locale.ENGLISH) }

        private fun loadDogTypes(locale: Locale): List<String> {
            val configuration =
                Configuration(context.resources.configuration).apply {
                    setLocale(locale)
                }
            return context.createConfigurationContext(configuration)
                .resources
                .getStringArray(R.array.dog_types)
                .toList()
        }

        fun searchTypes(query: String): List<String> {
            val trimmedQuery = query.trim()
            val displayTypes = context.resources.getStringArray(R.array.dog_types)
            return displayTypes
                .filterIndexed { index, _ ->
                    koreanTypes[index].contains(trimmedQuery, ignoreCase = true) ||
                        englishTypes[index].contains(trimmedQuery, ignoreCase = true)
                }
                .sortedWith(Collator.getInstance(context.resources.configuration.locales[0]))
        }
    }
