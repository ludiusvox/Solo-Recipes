package com.example.solo_recipes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
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
        TopAppBar(
            title = { Text("Recipe Book", style = MaterialTheme.typography.displayLarge) },
            actions = {
                Box(modifier = Modifier.padding(end = 16.dp)) {
                    IconButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier
                            .size(48.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(Icons.Default.Add, "Add Recipe", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search recipes...", style = MaterialTheme.typography.bodyLarge) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp)),
            leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF0EBE0),
                unfocusedContainerColor = Color(0xFFF0EBE0),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            items(filteredRecipes) { recipe ->
                RecipeCard(recipe, allFlavorItems, unitSystem, onDelete = { recipeToDelete = recipe })
            }
        }
    }
}

@Composable
fun RecipeCard(recipe: Recipe, allFlavorItems: List<FlavorComponent>, unitSystem: String, onDelete: () -> Unit) {
    val seasonings = recipe.ingredients.filter { ing ->
        allFlavorItems.any { flavor ->
            flavor.category != "Pantry Basics" && ing.contains(flavor.name, ignoreCase = true)
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = recipe.image,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Recipe", tint = Color.White)
                }
            }
            Column(modifier = Modifier.padding(24.dp)) {
                Text(text = recipe.title, style = MaterialTheme.typography.headlineMedium, color = Color.Black)

                if (seasonings.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Seasonings: ${seasonings.joinToString(", ")}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = Color(0xFFEFEBE9))
                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "${recipe.ingredients.size} Ingredients | ${recipe.directions.size} Steps",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "View recipe ✓",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
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
}

