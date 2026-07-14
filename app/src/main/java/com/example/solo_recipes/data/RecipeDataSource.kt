package com.example.solo_recipes.data

import android.content.Context
import com.example.solo_recipes.model.Recipe
import com.example.solo_recipes.model.RecipeResponse
import kotlinx.serialization.json.Json
import java.io.IOException

class RecipeDataSource(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun getRecipes(): List<Recipe> {
        return try {
            val jsonString = context.assets.open("recipes.json").bufferedReader().use { it.readText() }
            val response = json.decodeFromString<RecipeResponse>(jsonString)
            response.recipes
        } catch (e: IOException) {
            emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
