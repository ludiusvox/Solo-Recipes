package com.example.solo_recipes

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.solo_recipes.data.FlavorDataSource
import com.example.solo_recipes.data.RecipeDataSource
import com.example.solo_recipes.model.FlavorComponent
import com.example.solo_recipes.model.FoodCategory
import com.example.solo_recipes.model.Recipe
import com.example.solo_recipes.ui.theme.SoloRecipesTheme
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

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
                    modifier = Modifier.height(80.dp)
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
                                Icon(
                                    icon, 
                                    null,
                                    modifier = if (currentTab == name) 
                                        Modifier.background(MaterialTheme.colorScheme.primary, CircleShape).padding(8.dp) 
                                        else Modifier
                                ) 
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
                    if (selectedCategory == null) {
                        CategoryGrid(flavorCategories) { selectedCategory = it }
                    } else {
                        FlavorDetails(selectedCategory!!, pantryItems.value, onBack = { selectedCategory = null }) { updated -> 
                            updatePantryItem(updated)
                        }
                    }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeBookScreen(
    recipes: List<Recipe>, 
    allFlavorItems: List<FlavorComponent>, 
    onAddRecipe: (Recipe) -> Unit,
    onDeleteRecipe: (Recipe) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var recipeToDelete by remember { mutableStateOf<Recipe?>(null) }
    val filteredRecipes = recipes.filter { it.title.contains(searchQuery, ignoreCase = true) }

    if (showAddDialog) {
        AddRecipeDialog(onDismiss = { showAddDialog = false }, onConfirm = { onAddRecipe(it); showAddDialog = false })
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
                IconButton(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)
                ) { 
                    Icon(Icons.Default.Add, "Add Recipe", tint = MaterialTheme.colorScheme.primary) 
                } 
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search recipes...", style = MaterialTheme.typography.bodyLarge) },
            modifier = Modifier.fillMaxWidth().padding(16.dp).clip(RoundedCornerShape(16.dp)),
            leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFE5E7E2),
                unfocusedContainerColor = Color(0xFFE5E7E2),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
            items(filteredRecipes) { recipe ->
                RecipeCard(recipe, allFlavorItems, onDelete = { recipeToDelete = recipe })
            }
        }
    }
}

@Composable
fun RecipeCard(recipe: Recipe, allFlavorItems: List<FlavorComponent>, onDelete: () -> Unit) {
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
                    modifier = Modifier.fillMaxWidth().height(220.dp),
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
                HorizontalDivider(color = Color(0xFFF0F0F0))
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "${recipe.ingredients.size} Ingredients | ${recipe.directions.size} Steps",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.weight(1f)
                    )
                    Text("View recipe ✓", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantryScreen(items: List<FlavorComponent>, allRecipes: List<Recipe>, onItemsUpdated: (List<FlavorComponent>) -> Unit, onDeleteItem: (String) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var showAddItemDialog by remember { mutableStateOf(false) }
    val filteredItems = items.filter { it.name.contains(searchQuery, ignoreCase = true) }
    val stockedCount = items.count { it.isStocked }

    if (showAddItemDialog) {
        AddFlavorItemDialog(onDismiss = { showAddItemDialog = false }, onConfirm = { newItem -> onItemsUpdated(items + newItem.copy(isStocked = true)) })
    }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("My Pantry Stock", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary) },
            actions = { 
                IconButton(
                    onClick = { showAddItemDialog = true },
                    modifier = Modifier.background(MaterialTheme.colorScheme.primary, CircleShape)
                ) { 
                    Icon(Icons.Default.Add, null, tint = Color.White) 
                } 
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )
        
        LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
            item {
                Column(Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                    Text("Kitchen Inventory", style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.ExtraBold)
                    Text("Manage your seasonings and spices to automatically match recipes.", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                    
                    Spacer(Modifier.height(20.dp))
                    Surface(
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                    ) {
                        Row(Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarToday, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(12.dp))
                            Text("$stockedCount Stocked Items", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    
                    Spacer(Modifier.height(32.dp))
                    Text("✨ Active Recipes Matched", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(16.dp))
                }
            }
            
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp), 
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    items(allRecipes) { recipe ->
                        PantryMatchCard(recipe, items)
                    }
                }
            }
            
            item {
                Column(Modifier.padding(24.dp)) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search ingredients...") },
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)),
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                        colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
                    )
                }
            }
            
            val groups = filteredItems.groupBy { it.category }
            groups.forEach { (category, categoryItems) ->
                item { 
                    Row(Modifier.padding(horizontal = 24.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(category, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        Surface(color = Color(0xFFE0E0E0), shape = CircleShape) {
                            Text("${categoryItems.size}", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
                items(categoryItems) { item ->
                    Box(Modifier.padding(horizontal = 24.dp, vertical = 6.dp)) {
                        PantryItemRow(
                            item = item, 
                            onToggle = { updated -> onItemsUpdated(items.map { if (it.name.equals(updated.name, ignoreCase = true)) updated else it }) },
                            onDelete = { onDeleteItem(item.name) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddRecipeDialog(onDismiss: () -> Unit, onConfirm: (Recipe) -> Unit) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var ingredients by remember { mutableStateOf("") }
    var directions by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val tempPhotoUri = remember { mutableStateOf<Uri?>(null) }
    var showPhotoOptions by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) imageUri = uri
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && tempPhotoUri.value != null) imageUri = tempPhotoUri.value
    }

    if (showPhotoOptions) {
        AlertDialog(
            onDismissRequest = { showPhotoOptions = false },
            title = { Text("Add Recipe Photo") },
            confirmButton = {
                TextButton(onClick = {
                    showPhotoOptions = false
                    val photoFile = File.createTempFile("RECIPE_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}_", ".jpg", context.getExternalFilesDir(Environment.DIRECTORY_PICTURES))
                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
                    tempPhotoUri.value = uri
                    cameraLauncher.launch(uri)
                }) { Text("Camera") }
            },
            dismissButton = {
                TextButton(onClick = { showPhotoOptions = false; galleryLauncher.launch(androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) { Text("Gallery") }
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Recipe", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(140.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF0F0F0)).clickable { showPhotoOptions = true },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        AsyncImage(model = imageUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AddAPhoto, null, tint = Color.Gray)
                            Text("Add Photo", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        }
                    }
                }
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = ingredients, onValueChange = { ingredients = it }, label = { Text("Ingredients (comma separated)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = directions, onValueChange = { directions = it }, label = { Text("Directions (one per line)") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(Recipe(
                        title = title,
                        ingredients = ingredients.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                        directions = directions.split("\n").map { it.trim() }.filter { it.isNotEmpty() },
                        image = imageUri?.toString() ?: "https://images.unsplash.com/photo-1495521821757-a1efb6729352?w=800"
                    ))
                },
                enabled = title.isNotBlank()
            ) { Text("Add Recipe") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun PantryMatchCard(recipe: Recipe, stockedItems: List<FlavorComponent>) {
    var expanded by remember { mutableStateOf(false) }
    
    val missingIngredients = recipe.ingredients.filter { ing -> 
        !stockedItems.any { flavor -> 
            (ing.contains(flavor.name, ignoreCase = true) || flavor.name.contains(ing, ignoreCase = true)) && flavor.isStocked 
        } 
    }
    
    val totalCount = recipe.ingredients.size
    val ownedCount = totalCount - missingIngredients.size
    val matchPercent = if (totalCount > 0) (ownedCount * 100 / totalCount) else 0

    Card(
        modifier = Modifier
            .width(280.dp)
            .animateContentSize(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        onClick = { expanded = !expanded }
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("MATCH", style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontWeight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                Surface(
                    color = if (matchPercent > 70) Color(0xFFE8F5E9) else Color(0xFFFFF3E0), 
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "$matchPercent%", 
                        color = if (matchPercent > 70) Color(0xFF2E7D32) else Color(0xFFE65100),
                        style = MaterialTheme.typography.labelSmall, 
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), 
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(recipe.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, maxLines = 1)
            Text("You have $ownedCount of $totalCount ingredients.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            
            if (expanded) {
                Spacer(Modifier.height(16.dp))
                if (missingIngredients.isNotEmpty()) {
                    Text("Need to buy:", style = MaterialTheme.typography.labelMedium, color = Color(0xFFB14B6F), fontWeight = FontWeight.Bold)
                    missingIngredients.forEach { ingredient ->
                        Text("• $ingredient", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray, modifier = Modifier.padding(top = 4.dp))
                    }
                } else {
                    Text("You have everything! ✨", style = MaterialTheme.typography.labelMedium, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(8.dp))
            }

            HorizontalDivider(Modifier.padding(vertical = 12.dp), color = Color(0xFFF0F0F0))
            Text(
                text = if (expanded) "Show less" else "View needs ✓", 
                style = MaterialTheme.typography.labelLarge, 
                color = MaterialTheme.colorScheme.primary, 
                fontWeight = FontWeight.Bold, 
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
fun PantryItemRow(item: FlavorComponent, onToggle: (FlavorComponent) -> Unit, onDelete: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(28.dp)
                    .clip(CircleShape)
                    .background(if (item.isStocked) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .border(2.dp, if (item.isStocked) MaterialTheme.colorScheme.primary else Color.LightGray, CircleShape)
                    .clickable { onToggle(item.copy(isStocked = !item.isStocked)) },
                contentAlignment = Alignment.Center
            ) {
                if (item.isStocked) Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp), tint = Color.White)
            }
            Spacer(Modifier.width(16.dp))
            if (item.imageUrl != null) {
                AsyncImage(model = item.imageUrl, contentDescription = null, modifier = Modifier.size(48.dp).clip(CircleShape), contentScale = ContentScale.Crop)
                Spacer(Modifier.width(16.dp))
            }
            Text(item.name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null, tint = Color.LightGray, modifier = Modifier.size(24.dp)) }
        }
    }
}

@Composable
fun CategoryGrid(categories: List<FoodCategory>, onCategorySelected: (FoodCategory) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        items(categories) { category ->
            Card(
                modifier = Modifier.fillMaxWidth().height(200.dp).clickable { onCategorySelected(category) },
                shape = RoundedCornerShape(32.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Box {
                    AsyncImage(model = category.imageUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.7f)))))
                    Column(Modifier.align(Alignment.BottomStart).padding(24.dp)) {
                        Text(category.name, color = Color.White, style = MaterialTheme.typography.displayLarge, fontSize = 28.sp)
                        Text(category.description, color = Color.White.copy(0.8f), style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(items: List<FlavorComponent>, onUpdate: (FlavorComponent) -> Unit, onDelete: (String) -> Unit) {
    var showAddItemDialog by remember { mutableStateOf(false) }
    val neededItems = items.filter { !it.isStocked }

    if (showAddItemDialog) {
        AddFlavorItemDialog(onDismiss = { showAddItemDialog = false }, onConfirm = { newItem -> onUpdate(newItem.copy(isStocked = false)) })
    }

    Column(Modifier.fillMaxSize()) {
        LargeTopAppBar(
            title = { Text("Shopping List", style = MaterialTheme.typography.displayLarge) },
            actions = { 
                IconButton(
                    onClick = { showAddItemDialog = true },
                    modifier = Modifier.background(MaterialTheme.colorScheme.primary, CircleShape)
                ) { 
                    Icon(Icons.Default.Add, null, tint = Color.White) 
                } 
            },
            colors = TopAppBarDefaults.largeTopAppBarColors(containerColor = Color.Transparent)
        )
        if (neededItems.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { 
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.CheckCircle, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("Pantry is fully stocked! ✨", style = MaterialTheme.typography.titleLarge, color = Color.Gray) 
                }
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(neededItems) { item ->
                    FlavorItem(
                        component = item, 
                        onUpdate = onUpdate,
                        onDelete = { onDelete(item.name) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFlavorItemDialog(onDismiss: () -> Unit, onConfirm: (FlavorComponent) -> Unit) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Spices") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val tempPhotoUri = remember { mutableStateOf<Uri?>(null) }
    var showPhotoOptions by remember { mutableStateOf(false) }
    
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) imageUri = uri
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && tempPhotoUri.value != null) imageUri = tempPhotoUri.value
    }

    if (showPhotoOptions) {
        AlertDialog(
            onDismissRequest = { showPhotoOptions = false },
            title = { Text("Add Photo") },
            confirmButton = {
                TextButton(onClick = {
                    showPhotoOptions = false
                    val photoFile = File.createTempFile("FLAVOR_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}_", ".jpg", context.getExternalFilesDir(Environment.DIRECTORY_PICTURES))
                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
                    tempPhotoUri.value = uri
                    cameraLauncher.launch(uri)
                }) { Text("Camera") }
            },
            dismissButton = {
                TextButton(onClick = { showPhotoOptions = false; galleryLauncher.launch(androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) { Text("Gallery") }
            }
        )
    }

    val categories = listOf("Spices", "Herbs", "Pantry Basics")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Ingredient", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(
                    modifier = Modifier.size(100.dp).clip(CircleShape).background(Color(0xFFF0F0F0)).align(Alignment.CenterHorizontally).clickable { showPhotoOptions = true },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        AsyncImage(model = imageUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    } else {
                        Icon(Icons.Default.AddAPhoto, null, tint = Color.Gray, modifier = Modifier.size(32.dp))
                    }
                }
                OutlinedTextField(
                    value = name, 
                    onValueChange = { name = it }, 
                    label = { Text("Name") }, 
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Text("Category", style = MaterialTheme.typography.labelMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        },
        confirmButton = { 
            Button(
                onClick = { onConfirm(FlavorComponent(name = name, category = category, imageUrl = imageUri?.toString())) }, 
                enabled = name.isNotBlank(),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Add to Pantry") } 
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlavorDetails(category: FoodCategory, stockedItems: List<FlavorComponent>, onBack: () -> Unit, onUpdate: (FlavorComponent) -> Unit) {
    Column(Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            title = { Text(category.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = Color.Black) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
        )
        LazyColumn(contentPadding = PaddingValues(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            item { SectionHeader("Essential Seasonings", Icons.Default.Kitchen) }
            items(category.seasonings) { s ->
                val current = stockedItems.find { it.name.equals(s.name, ignoreCase = true) } ?: s
                FlavorItem(current, onUpdate)
            }
            item { SectionHeader("Perfect Condiments", Icons.Default.Restaurant) }
            items(category.condiments) { c ->
                val current = stockedItems.find { it.name.equals(c.name, ignoreCase = true) } ?: c
                FlavorItem(current, onUpdate)
            }
        }
    }
}

@Composable
fun FlavorItem(component: FlavorComponent, onUpdate: (FlavorComponent) -> Unit, onDelete: (() -> Unit)? = null) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(component.imageUrl?.let { Uri.parse(it) }) }
    val tempPhotoUri = remember { mutableStateOf<Uri?>(null) }
    var showPhotoOptions by remember { mutableStateOf(false) }
    
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) { imageUri = uri; onUpdate(component.copy(imageUrl = uri.toString())) }
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && tempPhotoUri.value != null) { imageUri = tempPhotoUri.value; onUpdate(component.copy(imageUrl = tempPhotoUri.value.toString())) }
    }

    if (showPhotoOptions) {
        AlertDialog(
            onDismissRequest = { showPhotoOptions = false },
            title = { Text("Add Photo") },
            confirmButton = {
                TextButton(onClick = {
                    showPhotoOptions = false
                    val photoFile = File.createTempFile("IMG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}_", ".jpg", context.getExternalFilesDir(Environment.DIRECTORY_PICTURES))
                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
                    tempPhotoUri.value = uri
                    cameraLauncher.launch(uri)
                }) { Text("Camera") }
            },
            dismissButton = {
                TextButton(onClick = { showPhotoOptions = false; galleryLauncher.launch(androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) { Text("Gallery") }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(), 
        colors = CardDefaults.cardColors(containerColor = Color.White), 
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(64.dp).clip(CircleShape).background(Color(0xFFF5F5F5)).clickable { showPhotoOptions = true }) {
                if (imageUri != null) AsyncImage(model = imageUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                else Icon(Icons.Default.AddAPhoto, null, modifier = Modifier.align(Alignment.Center), tint = Color.Gray)
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(component.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(component.note ?: "", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Checkbox(checked = component.isStocked, onCheckedChange = { onUpdate(component.copy(isStocked = it)) }, colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary))
            if (onDelete != null) {
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null, tint = Color.LightGray, modifier = Modifier.size(24.dp)) }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(8.dp))
        Text(title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold)
    }
}
