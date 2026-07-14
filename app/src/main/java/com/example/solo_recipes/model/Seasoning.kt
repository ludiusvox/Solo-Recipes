package com.example.solo_recipes.model

import kotlinx.serialization.Serializable

@Serializable
data class FlavorComponent(
    val name: String,
    val note: String? = null,
    val icon: String? = null,
    val imageUrl: String? = null,
    val isStocked: Boolean = false,
    val category: String = "Spices" // e.g., "Spices", "Herbs", "Pantry Basics"
)

@Serializable
data class FoodCategory(
    val name: String,
    val description: String,
    val imageUrl: String? = null,
    val seasonings: List<FlavorComponent>,
    val condiments: List<FlavorComponent>
)

@Serializable
data class FlavorKnowledgeBase(
    val categories: List<FoodCategory>
)
