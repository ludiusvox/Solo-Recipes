package com.example.solo_recipes.data

import android.content.Context
import com.example.solo_recipes.model.SeasoningCategory
import com.example.solo_recipes.model.SeasoningKnowledgeBase
import kotlinx.serialization.json.Json
import java.io.IOException

class SeasoningDataSource(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun getSeasoningCategories(): List<SeasoningCategory> {
        return try {
            val jsonString = context.assets.open("seasonings.json").bufferedReader().use { it.readText() }
            val response = json.decodeFromString<SeasoningKnowledgeBase>(jsonString)
            response.categories
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getSeasoningsForCategory(categoryName: String): List<String> {
        return getSeasoningCategories()
            .find { it.name.equals(categoryName, ignoreCase = true) }
            ?.seasonings ?: emptyList()
    }
}
