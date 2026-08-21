package com.example.solo_recipes.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

object DirectionsSerializer : KSerializer<List<String>> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Directions", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): List<String> {
        val input = decoder as? JsonDecoder ?: return emptyList()
        val element = input.decodeJsonElement()
        return when (element) {
            is JsonArray -> element.map { it.jsonPrimitive.content }
            is JsonPrimitive -> listOf(element.content)
            else -> emptyList()
        }
    }

    override fun serialize(encoder: Encoder, value: List<String>) {
        encoder.encodeSerializableValue(ListSerializer(String.serializer()), value)
    }
}

@Serializable
data class Recipe(
    val title: String,
    @SerialName("latin_title")
    val latinTitle: String? = null,
    val ingredients: List<String> = emptyList(),
    @Serializable(with = DirectionsSerializer::class)
    val directions: List<String> = emptyList(),
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
