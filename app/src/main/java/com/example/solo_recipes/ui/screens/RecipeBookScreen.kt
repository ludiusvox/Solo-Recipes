package com.example.solo_recipes.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.solo_recipes.model.FlavorComponent
import com.example.solo_recipes.model.Recipe
import com.example.solo_recipes.ui.components.AddRecipeDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeBookScreen(
    recipes: List<Recipe>,
    allFlavorItems: List<FlavorComponent>,
    unitSystem: String = "English",
    onAddRecipe: (Recipe) -> Unit,
    onDeleteRecipe: (Recipe) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var recipeToDelete by remember { mutableStateOf<Recipe?>(null) }
    val filteredRecipes = recipes.filter { it.title.contains(searchQuery, ignoreCase = true) }
    val ancientRecipes = filteredRecipes.filter { !it.latinTitle.isNullOrEmpty() }
    val pilgrimRecipes = filteredRecipes.filter { it.isPilgrim || it.source == "Pilgrim Cook Book" }
    val newRecipes = filteredRecipes.filter { 
        it.latinTitle.isNullOrEmpty() && !it.isPilgrim && it.source != "Pilgrim Cook Book" 
    }

    if (showAddDialog) {
        AddRecipeDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = {
                onAddRecipe(it)
                showAddDialog = false
            }
        )
    }

    if (recipeToDelete != null) {
        AlertDialog(
            onDismissRequest = { recipeToDelete = null },
            title = { Text("Delete Recipe?") },
            text = { Text("Are you sure you want to remove \"${recipeToDelete?.title}\"? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        recipeToDelete?.let { onDeleteRecipe(it) }
                        recipeToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { recipeToDelete = null }) { Text("Cancel") }
            }
        )
    }

    Column(Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f)
                    ),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Recipe Book",
                                style = MaterialTheme.typography.displayLarge.copy(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.tertiary
                                        )
                                    )
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { showAddDialog = true },
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                            ) {
                                Icon(Icons.Default.Add, "Add Recipe", tint = Color.White)
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search recipes...", style = MaterialTheme.typography.bodyLarge) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp)),
                            leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF0EBE0).copy(alpha = 0.45f),
                                unfocusedContainerColor = Color(0xFFF0EBE0).copy(alpha = 0.45f),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )

                        if (newRecipes.isNotEmpty()) {
                            Spacer(Modifier.height(24.dp))
                            Text(
                                text = "Modern Recipes",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            if (newRecipes.isNotEmpty()) {
                items(newRecipes) { recipe ->
                    RecipeCard(recipe, allFlavorItems, unitSystem, onDelete = { recipeToDelete = recipe })
                }
            }

            if (pilgrimRecipes.isNotEmpty()) {
                item {
                    Text(
                        text = "Pilgrims Cookbook",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                    )
                }

                val grouped = pilgrimRecipes.groupBy { it.tags.firstOrNull() ?: "General" }
                grouped.forEach { (category, categoryRecipes) ->
                    item {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }
                    items(categoryRecipes) { recipe ->
                        RecipeCard(recipe, allFlavorItems, unitSystem, onDelete = { recipeToDelete = recipe })
                    }
                }
            }

            if (ancientRecipes.isNotEmpty()) {
                item {
                    Text(
                        text = "Ancient Recipes",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                    )
                }
                items(ancientRecipes) { recipe ->
                    RecipeCard(recipe, allFlavorItems, unitSystem, onDelete = { recipeToDelete = recipe })
                }
            }
        }
    }
}

@Composable
fun RecipeCard(recipe: Recipe, allFlavorItems: List<FlavorComponent>, unitSystem: String, onDelete: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val seasonings = recipe.ingredients.filter { ing ->
        allFlavorItems.any { flavor ->
            flavor.category != "Pantry Basics" && ing.contains(flavor.name, ignoreCase = true)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = recipe.title, style = MaterialTheme.typography.titleLarge, color = Color.Black)

                    if (!recipe.latinTitle.isNullOrEmpty()) {
                        Text(
                            text = recipe.latinTitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    if (recipe.isPilgrim) {
                        val subtitle = buildString {
                            if (recipe.tags.isNotEmpty()) append(recipe.tags.first())
                            if (!recipe.author.isNullOrEmpty()) {
                                if (isNotEmpty()) append(" • ")
                                append(recipe.author)
                            }
                        }
                        if (subtitle.isNotEmpty()) {
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete Recipe",
                        tint = Color.LightGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (seasonings.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Seasonings: ${seasonings.joinToString(", ")}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFEFEBE9))
            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "${recipe.ingredients.size} Ingredients | ${recipe.directions.size} Steps",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = { expanded = !expanded }) {
                    Text(
                        if (expanded) "Close ✕" else "View ✓",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Ingredients",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                recipe.ingredients.forEach { ingredient ->
                    Text(
                        text = "• $ingredient",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(vertical = 1.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Directions",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                recipe.directions.forEachIndexed { index, step ->
                    Text(
                        text = "${index + 1}. $step",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
            
            if (unitSystem == "Metric") {
                Text(
                    "* Metric conversion active",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

