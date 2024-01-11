package com.project.meongcare.onboarding.model.data.repository

import android.content.Context
import org.json.JSONObject
import javax.inject.Inject

class DogTypeRepository
    @Inject
    constructor(
        private val context: Context,
    ) {
        private val dogTypes: List<String> by lazy {
            loadDogTypes()
        }

        fun loadDogTypes(): List<String> {
            val jsonData =
                context.assets.open("dog_types.json").bufferedReader().use {
                    it.readText()
                }

            val jsonRoot = JSONObject(jsonData)
            val jsonArray = jsonRoot.getJSONArray("dogTypes")
            val typeList = mutableListOf<String>()

            for (i in 0 until jsonArray.length()) {
                val type = jsonArray.getString(i)
                typeList.add(type)
            }

            return typeList
        }

        fun searchTypes(query: String): List<String> {
            return dogTypes.filter { it.contains(query) }
        }
    }
