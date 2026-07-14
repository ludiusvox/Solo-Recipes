package com.example.solo_recipes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.solo_recipes.data.SeasoningDataSource
import com.example.solo_recipes.model.SeasoningCategory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataSource = SeasoningDataSource(this)
        val categories = dataSource.getSeasoningCategories()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SeasoningApp(categories)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeasoningApp(categories: List<SeasoningCategory>) {
    var selectedCategory by remember { mutableStateOf<SeasoningCategory?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(selectedCategory?.let { "Seasonings for ${it.name}" } ?: "Select Food Category") },
                navigationIcon = {
                    if (selectedCategory != null) {
                        IconButton(onClick = { selectedCategory = null }) {
                            Text("<") // Simple back button for now
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (selectedCategory == null) {
                CategoryList(categories) { selectedCategory = it }
            } else {
                SeasoningList(selectedCategory!!)
            }
        }
    }
}

@Composable
fun CategoryList(categories: List<SeasoningCategory>, onCategorySelected: (SeasoningCategory) -> Unit) {
    LazyColumn {
        items(categories) { category ->
            ListItem(
                headlineContent = { Text(category.name) },
                supportingContent = { category.description?.let { Text(it) } },
                modifier = Modifier.clickable { onCategorySelected(category) }
            )
            HorizontalDivider()
        }
    }
}

@Composable
fun SeasoningList(category: SeasoningCategory) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(category.seasonings) { seasoning ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = seasoning,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
