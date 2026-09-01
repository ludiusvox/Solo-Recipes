package com.example.solo_recipes.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.solo_recipes.model.FlavorComponent
import com.example.solo_recipes.model.Recipe
import com.example.solo_recipes.ui.components.AddFlavorItemDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantryScreen(
    items: List<FlavorComponent>,
    onItemsUpdated: (List<FlavorComponent>) -> Unit,
    onDeleteItem: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showAddItemDialog by remember { mutableStateOf(false) }
    val filteredItems = items.filter { it.name.contains(searchQuery, ignoreCase = true) }
    val stockedCount = items.count { it.isStocked }

    if (showAddItemDialog) {
        AddFlavorItemDialog(
            onDismiss = { showAddItemDialog = false },
            onConfirm = { newItem: FlavorComponent -> 
                onItemsUpdated(items + newItem.copy(isStocked = true))
                showAddItemDialog = false
            }
        )
    }

    Column(Modifier.fillMaxSize()) {
        LazyColumn(contentPadding = PaddingValues(16.dp)) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
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
                                "My Pantry Stock",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { showAddItemDialog = true },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                            ) {
                                Icon(Icons.Default.Add, null, tint = Color.White)
                            }
                        }

                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Kitchen Inventory",
                            style = MaterialTheme.typography.displayLarge.copy(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.tertiary
                                    )
                                )
                            ),
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            "Manage seasonings to match recipes.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.DarkGray
                        )

                        Spacer(Modifier.height(16.dp))
                        Surface(
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                        ) {
                            Row(
                                Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.CalendarToday,
                                    null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "$stockedCount Stocked Items",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            item {
                Column(Modifier.padding(24.dp)) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search ingredients...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp)),
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(alpha = 0.8f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.8f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                }
            }

            val groups = filteredItems.groupBy { it.category }
            groups.forEach { (category, categoryItems) ->
                item {
                    Row(
                        Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(category, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        Surface(color = Color(0xFFEDE7E3), shape = CircleShape) {
                            Text(
                                "${categoryItems.size}",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
                items(categoryItems) { item ->
                    Box(Modifier.padding(horizontal = 24.dp, vertical = 6.dp)) {
                        PantryItemRow(
                            item = item,
                            onToggle = { updated ->
                                onItemsUpdated(items.map {
                                    if (it.name.equals(updated.name, ignoreCase = true)) updated else it
                                })
                            },
                            onDelete = { onDeleteItem(item.name) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PantryItemRow(item: FlavorComponent, onToggle: (FlavorComponent) -> Unit, onDelete: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.8f),
        shadowElevation = 1.dp
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(if (item.isStocked) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .border(
                        2.dp,
                        if (item.isStocked) MaterialTheme.colorScheme.primary else Color.LightGray,
                        CircleShape
                    )
                    .clickable { onToggle(item.copy(isStocked = !item.isStocked)) },
                contentAlignment = Alignment.Center
            ) {
                if (item.isStocked) Icon(
                    Icons.Default.Check,
                    null,
                    modifier = Modifier.size(18.dp),
                    tint = Color.White
                )
            }
            Spacer(Modifier.width(16.dp))
            if (item.imageUrl != null) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(16.dp))
            }
            Text(
                item.name,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    null,
                    tint = Color.LightGray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
