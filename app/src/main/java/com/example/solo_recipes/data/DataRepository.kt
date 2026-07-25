package com.example.solo_recipes.data

import android.content.Context
import com.example.solo_recipes.model.FlavorComponent
import com.example.solo_recipes.model.Recipe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class DataRepository(private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }
    private val pantryFile = File(context.filesDir, "pantry_v2.json")
    private val recipesFile = File(context.filesDir, "recipes_v2.json")
    private val settingsFile = File(context.filesDir, "settings.json")

    fun savePantry(items: List<FlavorComponent>) {
        pantryFile.writeText(json.encodeToString(items))
    }

    fun loadPantry(defaults: List<FlavorComponent>): List<FlavorComponent> {
        return if (pantryFile.exists()) {
            try {
                json.decodeFromString(pantryFile.readText())
            } catch (e: Exception) {
                defaults
            }
        } else {
            defaults
        }
    }

    fun saveRecipes(recipes: List<Recipe>) {
        recipesFile.writeText(json.encodeToString(recipes))
    }

    fun loadRecipes(defaults: List<Recipe>): List<Recipe> {
        return if (recipesFile.exists()) {
            try {
                json.decodeFromString(recipesFile.readText())
            } catch (e: Exception) {
                defaults
            }
        } else {
            defaults
        }
    }
    
    fun saveUnitSystem(system: String) {
        settingsFile.writeText(system)
    }
    
    fun loadUnitSystem(): String {
        return if (settingsFile.exists()) settingsFile.readText() else "English"
    }

    fun clearAll() {
        pantryFile.delete()
        recipesFile.delete()
        settingsFile.delete()
        val imageDir = File(context.filesDir, "images")
        if (imageDir.exists()) {
            imageDir.deleteRecursively()
        }
    }
}
