package com.example.solo_recipes

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.solo_recipes.data.DataRepository
import com.example.solo_recipes.data.FlavorDataSource
import com.example.solo_recipes.data.RecipeDataSource
import com.example.solo_recipes.model.FlavorComponent
import com.example.solo_recipes.model.Recipe
import com.example.solo_recipes.ui.screens.PantryScreen
import com.example.solo_recipes.ui.screens.RecipeBookScreen
import com.example.solo_recipes.ui.screens.SettingsScreen
import com.example.solo_recipes.ui.screens.ShoppingListScreen
import com.example.solo_recipes.ui.theme.SoloRecipesTheme

class MainActivity : ComponentActivity() {
    private lateinit var dataRepository: DataRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val flavorDataSource = FlavorDataSource(this)
        val recipeDataSource = RecipeDataSource(this)
        dataRepository = DataRepository(this)
        
        val flavorCategories = flavorDataSource.getFlavorCategories()
        val defaultRecipes = recipeDataSource.getRecipes()
        val defaultPantry = flavorCategories.flatMap { it.seasonings + it.condiments }.distinctBy { it.name }
        
        val savedPantry = dataRepository.loadPantry(defaultPantry)
        
        // Force merge pilgrim recipes if they aren't in the saved list
        val loadedRecipes = dataRepository.loadRecipes(defaultRecipes)
        val pilgrimTitles = defaultRecipes.filter { it.isPilgrim }.map { it.title }.toSet()
        val hasPilgrim = loadedRecipes.any { it.isPilgrim || pilgrimTitles.contains(it.title) }
        
        val savedRecipes = if (!hasPilgrim) {
            loadedRecipes + defaultRecipes.filter { it.isPilgrim }
        } else {
            loadedRecipes
        }
        
        val savedUnit = dataRepository.loadUnitSystem()
        val savedShoppingList = dataRepository.loadShoppingList()

        setContent {
            SoloRecipesTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.feast_background),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background.copy(alpha = 0.6f)
                    ) {
                        val context = LocalContext.current
                    var hasCameraPermission by remember { 
                        mutableStateOf(
                            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        ) 
                    }

                    val permissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission()
                    ) { isGranted ->
                        hasCameraPermission = isGranted
                    }

                    LaunchedEffect(Unit) {
                        if (!hasCameraPermission) {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }

                    MainScreen(defaultPantry, defaultRecipes, savedPantry, savedRecipes, savedUnit, savedShoppingList, dataRepository)
                }
            }
        }
    }
}
}

@Composable
fun MainScreen(
    defaultPantry: List<FlavorComponent>,
    defaultRecipes: List<Recipe>,
    initialPantry: List<FlavorComponent>,
    initialRecipes: List<Recipe>,
    initialUnit: String,
    initialShoppingList: Map<String, List<String>>,
    repository: DataRepository
) {
    var currentTab by remember { mutableStateOf("Pantry") }
    var unitSystem by remember { mutableStateOf(initialUnit) }
    
    val pantryItems = remember { mutableStateOf(initialPantry) }
    val recipes = remember { mutableStateOf(initialRecipes) }
    val shoppingList = remember { mutableStateOf(initialShoppingList) }

    fun updatePantryItem(updated: FlavorComponent) {
        val exists = pantryItems.value.any { it.name.equals(updated.name, ignoreCase = true) }
        val newList = if (exists) {
            pantryItems.value.map { if (it.name.equals(updated.name, ignoreCase = true)) updated else it }
        } else {
            pantryItems.value + updated
        }
        pantryItems.value = newList
        repository.savePantry(newList)
    }

    fun deletePantryItem(name: String) {
        val newList = pantryItems.value.filter { !it.name.equals(name, ignoreCase = true) }
        pantryItems.value = newList
        repository.savePantry(newList)
    }

    fun deleteRecipe(recipe: Recipe) {
        val newList = recipes.value.filter { it.title != recipe.title }
        recipes.value = newList
        repository.saveRecipes(newList)
    }

    fun factoryReset() {
        pantryItems.value = defaultPantry
        recipes.value = defaultRecipes
        repository.clearAll()
        repository.savePantry(defaultPantry)
        repository.saveRecipes(defaultRecipes)
    }

    Scaffold(
        containerColor = Color.Transparent,
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
                        Triple("Shopping", Icons.Default.ShoppingCart, "Shopping"),
                        Triple("Settings", Icons.Default.Settings, "Settings")
                    )
                    tabs.forEach { (name, icon, label) ->
                        NavigationBarItem(
                            selected = currentTab == name,
                            onClick = { currentTab = name },
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
        Box(modifier = Modifier.padding(padding)) {
            when (currentTab) {
                "Recipes" -> RecipeBookScreen(
                    recipes = recipes.value, 
                    allFlavorItems = pantryItems.value,
                    unitSystem = unitSystem,
                    onAddRecipe = { 
                        val newList = recipes.value + it
                        recipes.value = newList
                        repository.saveRecipes(newList)
                    },
                    onDeleteRecipe = { deleteRecipe(it) }
                )
                "Pantry" -> PantryScreen(
                    items = pantryItems.value, 
                    onItemsUpdated = { updatedItems -> 
                        pantryItems.value = updatedItems
                        repository.savePantry(updatedItems)
                    },
                    onDeleteItem = { name -> deletePantryItem(name) }
                )
                "Shopping" -> ShoppingListScreen(
                    shoppingList = shoppingList.value,
                    onUpdate = { updated ->
                        shoppingList.value = updated
                        repository.saveShoppingList(updated)
                    }
                )
                "Settings" -> SettingsScreen(
                    unitSystem = unitSystem,
                    onUnitSystemChange = { 
                        unitSystem = it
                        repository.saveUnitSystem(it)
                    },
                    pantryItems = pantryItems.value,
                    recipes = recipes.value,
                    onFactoryReset = { factoryReset() }
                )
            }
        }
    }
}
