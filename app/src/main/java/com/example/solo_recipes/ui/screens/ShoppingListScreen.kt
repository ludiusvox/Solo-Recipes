package com.example.solo_recipes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.solo_recipes.model.FlavorComponent
import com.example.solo_recipes.ui.components.AddFlavorItemDialog
import com.example.solo_recipes.ui.components.FlavorItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(
    items: List<FlavorComponent>,
    onUpdate: (FlavorComponent) -> Unit,
    onDelete: (String) -> Unit
) {
    var showAddItemDialog by remember { mutableStateOf(false) }
    val neededItems = items.filter { !it.isStocked }

    if (showAddItemDialog) {
        AddFlavorItemDialog(
            onDismiss = { showAddItemDialog = false },
            onConfirm = { newItem -> 
                onUpdate(newItem.copy(isStocked = false))
                showAddItemDialog = false
            }
        )
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.75f)
            ),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Shopping List",
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
                    onClick = { showAddItemDialog = true },
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                ) {
                    Icon(Icons.Default.Add, null, tint = Color.White)
                }
            }
        }

        if (neededItems.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.CheckCircle,
                        null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("Pantry is fully stocked! ✨", style = MaterialTheme.typography.titleLarge, color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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
