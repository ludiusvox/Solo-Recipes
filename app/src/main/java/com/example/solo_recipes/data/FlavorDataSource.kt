package com.example.solo_recipes.data

import android.content.Context
import com.example.solo_recipes.model.FoodCategory
import com.example.solo_recipes.model.FlavorKnowledgeBase
import kotlinx.serialization.json.Json

class FlavorDataSource(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun getFlavorCategories(): List<FoodCategory> {
        return try {
            val jsonString = context.assets.open("seasonings.json").bufferedReader().use { it.readText() }
            val response = json.decodeFromString<FlavorKnowledgeBase>(jsonString)
            response.categories
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun getCategoryByName(name: String): FoodCategory? {
        return getFlavorCategories().find { it.name.equals(name, ignoreCase = true) }
    }
}
