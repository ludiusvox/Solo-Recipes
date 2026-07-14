package com.example.solo_recipes.model

import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val title: String,
    val ingredients: List<String>,
    val directions: List<String>,
    val language: String? = null,
    val source: String? = null,
    val tags: List<String> = emptyList(),
    val url: String? = null,
    val image: String? = null
)

@Serializable
data class RecipeResponse(
    val recipes: List<Recipe>
)
