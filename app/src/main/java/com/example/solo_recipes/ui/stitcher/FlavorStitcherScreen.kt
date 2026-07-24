package com.example.solo_recipes.ui.stitcher

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.solo_recipes.model.FlavorComponent
import com.example.solo_recipes.model.FoodCategory
import com.example.solo_recipes.ui.components.FlavorItem
import com.example.solo_recipes.ui.components.SectionHeader

@Composable
fun FlavorStitcherScreen(
    categories: List<FoodCategory>,
    selectedCategory: FoodCategory?,
    onCategorySelected: (FoodCategory?) -> Unit,
    pantryItems: List<FlavorComponent>,
    onUpdatePantryItem: (FlavorComponent) -> Unit
) {
    if (selectedCategory == null) {
        CategoryGrid(categories, onCategorySelected)
    } else {
        FlavorDetails(
            category = selectedCategory,
            stockedItems = pantryItems,
            onBack = { onCategorySelected(null) },
            onUpdate = onUpdatePantryItem
        )
    }
}

@Composable
fun CategoryGrid(categories: List<FoodCategory>, onCategorySelected: (FoodCategory) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        items(categories) { category ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable { onCategorySelected(category) },
                shape = RoundedCornerShape(32.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Box {
                    AsyncImage(
                        model = category.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.7f))))
                    )
                    Column(
                        Modifier
                            .align(Alignment.BottomStart)
                            .padding(24.dp)
                    ) {
                        Text(
                            category.name,
                            color = Color.White,
                            style = MaterialTheme.typography.displayLarge,
                            fontSize = 28.sp
                        )
                        Text(
                            category.description,
                            color = Color.White.copy(0.8f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlavorDetails(
    category: FoodCategory,
    stockedItems: List<FlavorComponent>,
    onBack: () -> Unit,
    onUpdate: (FlavorComponent) -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    category.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
        )
        LazyColumn(
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
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
