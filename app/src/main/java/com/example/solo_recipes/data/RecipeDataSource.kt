package com.example.solo_recipes.data

import android.content.Context
import com.example.solo_recipes.model.Recipe
import com.example.solo_recipes.model.RecipeResponse
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.IOException

@Serializable
data class PilgrimRecipe(
    val title: String,
    val instructions: String,
    val author: String? = null
)

class RecipeDataSource(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun getRecipes(): List<Recipe> {
        val modernRecipes = try {
            val jsonString = context.assets.open("recipes.json").bufferedReader().use { it.readText() }
            val response = json.decodeFromString<RecipeResponse>(jsonString)
            response.recipes
        } catch (e: Exception) {
            emptyList()
        }

        val pilgrimRecipes = try {
            val jsonString = context.assets.open("pilgrim_recipes.json").bufferedReader().use { it.readText() }
            val pilgrimMap = json.decodeFromString<Map<String, List<PilgrimRecipe>>>(jsonString)
            val recipes = pilgrimMap.flatMap { (category, recipes) ->
                recipes.map { pr ->
                    Recipe(
                        title = pr.title,
                        directions = listOf(pr.instructions),
                        author = pr.author,
                        source = "Pilgrim Cook Book",
                        tags = listOf(category),
                        isPilgrim = true
                    )
                }
            }
            android.util.Log.d("RecipeDataSource", "Loaded ${recipes.size} pilgrim recipes")
            recipes
        } catch (e: Exception) {
            android.util.Log.e("RecipeDataSource", "Error loading pilgrim recipes", e)
            emptyList()
        }

        return modernRecipes + pilgrimRecipes
    }
}
