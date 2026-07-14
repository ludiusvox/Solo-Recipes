package com.example.solo_recipes.model

import kotlinx.serialization.Serializable

@Serializable
data class SeasoningCategory(
    val name: String,
    val seasonings: List<String>,
    val description: String? = null
)

@Serializable
data class SeasoningKnowledgeBase(
    val categories: List<SeasoningCategory>
)
