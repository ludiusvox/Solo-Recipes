package com.example.solo_recipes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.solo_recipes.data.FlavorDataSource
import com.example.solo_recipes.data.RecipeDataSource
import com.example.solo_recipes.model.FlavorComponent
import com.example.solo_recipes.model.FoodCategory
import com.example.solo_recipes.model.Recipe
import com.example.solo_recipes.ui.screens.PantryScreen
import com.example.solo_recipes.ui.screens.RecipeBookScreen
import com.example.solo_recipes.ui.screens.ShoppingListScreen
import com.example.solo_recipes.ui.stitcher.FlavorStitcherScreen
import com.example.solo_recipes.ui.theme.SoloRecipesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val flavorDataSource = FlavorDataSource(this)
        val recipeDataSource = RecipeDataSource(this)
        
        val flavorCategories = flavorDataSource.getFlavorCategories()
        val initialRecipes = recipeDataSource.getRecipes()

        setContent {
            SoloRecipesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(flavorCategories, initialRecipes)
                }
            }
        }
    }
}

@Composable
fun MainScreen(flavorCategories: List<FoodCategory>, initialRecipes: List<Recipe>) {
    var currentTab by remember { mutableStateOf("Pantry") }
    var selectedCategory by remember { mutableStateOf<FoodCategory?>(null) }
    
    val pantryItems = remember { 
        mutableStateOf(flavorCategories.flatMap { it.seasonings + it.condiments }.distinctBy { it.name })
    }
    val recipes = remember { mutableStateOf(initialRecipes) }

    fun updatePantryItem(updated: FlavorComponent) {
        val exists = pantryItems.value.any { it.name.equals(updated.name, ignoreCase = true) }
        if (exists) {
            pantryItems.value = pantryItems.value.map { if (it.name.equals(updated.name, ignoreCase = true)) updated else it }
        } else {
            pantryItems.value = pantryItems.value + updated
        }
    }

    fun deletePantryItem(name: String) {
        pantryItems.value = pantryItems.value.filter { !it.name.equals(name, ignoreCase = true) }
    }

    fun deleteRecipe(recipe: Recipe) {
        recipes.value = recipes.value.filter { it.title != recipe.title }
    }

    Scaffold(
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(32.dp)),
                tonalElevation = 8.dp,
                color = Color.White
            ) {
                NavigationBar(
                    containerColor = Color.Transparent,
                    windowInsets = WindowInsets(0, 0, 0, 0)
                ) {
                    val tabs = listOf(
                        Triple("Recipes", Icons.Default.MenuBook, "Recipes"),
                        Triple("Pantry", Icons.Default.Kitchen, "Pantry"),
                        Triple("Seasonings", Icons.Default.AutoAwesome, "Seasonings"),
                        Triple("Shopping", Icons.Default.ShoppingCart, "Shopping")
                    )
                    tabs.forEach { (name, icon, label) ->
                        NavigationBarItem(
                            selected = currentTab == name,
                            onClick = { currentTab = name; selectedCategory = null },
                            icon = { 
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(if (currentTab == name) MaterialTheme.colorScheme.primary else Color.Transparent)
                                        .padding(8.dp)
                                ) {
                                    Icon(
                                        icon, 
                                        null,
                                        tint = if (currentTab == name) Color.White else Color.Gray
                                    ) 
                                }
                            },
                            label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                unselectedIconColor = Color.Gray,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).background(MaterialTheme.colorScheme.background)) {
            when (currentTab) {
                "Recipes" -> RecipeBookScreen(
                    recipes = recipes.value, 
                    allFlavorItems = pantryItems.value,
                    onAddRecipe = { recipes.value = recipes.value + it },
                    onDeleteRecipe = { deleteRecipe(it) }
                )
                "Pantry" -> PantryScreen(
                    items = pantryItems.value, 
                    allRecipes = recipes.value, 
                    onItemsUpdated = { updatedItems -> pantryItems.value = updatedItems },
                    onDeleteItem = { name -> deletePantryItem(name) }
                )
                "Seasonings" -> {
                    FlavorStitcherScreen(
                        categories = flavorCategories,
                        selectedCategory = selectedCategory,
                        onCategorySelected = { selectedCategory = it },
                        pantryItems = pantryItems.value,
                        onUpdatePantryItem = { updatePantryItem(it) }
                    )
                }
                "Shopping" -> ShoppingListScreen(
                    items = pantryItems.value, 
                    onUpdate = { updated -> updatePantryItem(updated) },
                    onDelete = { name -> deletePantryItem(name) }
                )
            }
        }
    }
}
