package com.example.solo_recipes.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ShoppingListScreen(
    shoppingList: Map<String, List<String>>,
    onUpdate: (Map<String, List<String>>) -> Unit
) {
    val today = SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date())
    
    // Ensure we have at least today in the map if it's empty
    val effectiveMap = if (shoppingList.isEmpty()) {
        mapOf(today to emptyList())
    } else {
        shoppingList
    }

    val dates = effectiveMap.keys.toList().sortedDescending()
    var selectedDate by remember { mutableStateOf(dates.firstOrNull() ?: today) }

    val currentItems = effectiveMap[selectedDate] ?: emptyList()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        // Header
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
                    onClick = {
                        val newDate = SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date())
                        if (!effectiveMap.containsKey(newDate)) {
                            onUpdate(effectiveMap + (newDate to emptyList()))
                            selectedDate = newDate
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                ) {
                    Icon(Icons.Default.Add, "Add Date", tint = Color.White)
                }
            }
        }

        // Date Tabs (Mini Tabs)
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(dates) { _, date ->
                val isSelected = date == selectedDate
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isSelected) {
                                Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.tertiary
                                    )
                                )
                            } else {
                                SolidColor(Color.White.copy(alpha = 0.5f))
                            }
                        )
                        .clickable { selectedDate = date }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = date,
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = if (isSelected) Color.White else Color.DarkGray,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                }
            }
        }

        // Items List
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .animateContentSize(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.75f)
            )
        ) {
            Column(Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Items for $selectedDate",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        val newList = currentItems + ""
                        onUpdate(effectiveMap + (selectedDate to newList))
                    }) {
                        Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray.copy(alpha = 0.5f))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(currentItems) { index, item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 4.dp)
                            ) {
                                BasicTextField(
                                    value = item,
                                    onValueChange = { newValue ->
                                        val newList = currentItems.toMutableList()
                                        newList[index] = newValue
                                        onUpdate(effectiveMap + (selectedDate to newList))
                                    },
                                    textStyle = TextStyle(
                                        fontSize = 18.sp,
                                        color = Color.Black
                                    ),
                                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                    modifier = Modifier.fillMaxWidth(),
                                    decorationBox = { innerTextField ->
                                        Column {
                                            innerTextField()
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(1.dp)
                                                    .background(
                                                        Brush.linearGradient(
                                                            colors = listOf(
                                                                MaterialTheme.colorScheme.primary,
                                                                MaterialTheme.colorScheme.tertiary
                                                            )
                                                        )
                                                    )
                                                    .padding(top = 4.dp)
                                            )
                                        }
                                    }
                                )
                                if (item.isEmpty()) {
                                    Text(
                                        "New item...",
                                        style = TextStyle(fontSize = 18.sp, color = Color.Gray.copy(alpha = 0.5f))
                                    )
                                }
                            }
                            
                            IconButton(
                                onClick = {
                                    val newList = currentItems.toMutableList()
                                    newList.removeAt(index)
                                    onUpdate(effectiveMap + (selectedDate to newList))
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    null,
                                    tint = Color.Red.copy(alpha = 0.6f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
