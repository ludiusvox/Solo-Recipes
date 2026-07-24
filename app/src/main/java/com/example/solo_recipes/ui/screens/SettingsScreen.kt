package com.example.solo_recipes.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.solo_recipes.model.FlavorComponent
import com.example.solo_recipes.model.Recipe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    unitSystem: String,
    onUnitSystemChange: (String) -> Unit,
    pantryItems: List<FlavorComponent>,
    recipes: List<Recipe>,
    onFactoryReset: () -> Unit
) {
    val context = LocalContext.current
    var showResetDialog by remember { mutableStateOf(false) }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain"),
        onResult = { uri ->
            uri?.let { saveExportFile(context, it, pantryItems, recipes) }
        }
    )

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Factory Reset") },
            text = { Text("Are you sure you want to reset all data? This will restore initial recipes and pantry items. Your custom changes will be lost.") },
            confirmButton = {
                Button(
                    onClick = {
                        onFactoryReset()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Reset Everything", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text("Cancel") }
            }
        )
    }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Settings", style = MaterialTheme.typography.displayLarge) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

        LazyColumn(
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { SettingsSectionHeader("General") }
            item {
                SettingsToggleRow(
                    title = "Unit System",
                    subtitle = "Current: $unitSystem",
                    icon = Icons.Default.Straighten,
                    options = listOf("English", "Metric"),
                    selectedOption = unitSystem,
                    onOptionSelected = onUnitSystemChange
                )
            }

            item { Spacer(Modifier.height(16.dp)) }
            item { SettingsSectionHeader("Data Management") }
            item {
                SettingsActionCard(
                    title = "Export Data",
                    subtitle = "Export recipes and pantry to a text file",
                    icon = Icons.Default.FileUpload,
                    onClick = { exportLauncher.launch("Solo-Recipes-Export.txt") }
                )
            }
            item {
                SettingsActionCard(
                    title = "Factory Reset",
                    subtitle = "Restore default recipes and items",
                    icon = Icons.Default.Restore,
                    onClick = { showResetDialog = true },
                    contentColor = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun SettingsToggleRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(title, fontWeight = FontWeight.Bold)
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                options.forEach { option ->
                    FilterChip(
                        selected = selectedOption == option,
                        onClick = { onOptionSelected(option) },
                        label = { Text(option) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    contentColor: Color = Color.Black
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        onClick = onClick
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = if (contentColor == Color.Black) MaterialTheme.colorScheme.primary else contentColor, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, color = contentColor)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray)
        }
    }
}

fun saveExportFile(context: Context, uri: Uri, pantryItems: List<FlavorComponent>, recipes: List<Recipe>) {
    val exportText = buildString {
        appendLine("SOLO-RECIPES DATA EXPORT")
        appendLine("=========================")
        appendLine()
        appendLine("=== PANTRY INVENTORY ===")
        pantryItems.forEach { item ->
            val status = if (item.isStocked) "[X]" else "[ ]"
            appendLine("$status ${item.name} (${item.category}) - ${item.note ?: ""}")
        }
        appendLine()
        appendLine("=== RECIPE BANK ===")
        recipes.forEachIndexed { index, recipe ->
            appendLine("${index + 1}. ${recipe.title}")
            appendLine("   Ingredients: ${recipe.ingredients.joinToString(", ")}")
            appendLine("   Directions:")
            recipe.directions.forEach { step ->
                appendLine("   - $step")
            }
            appendLine()
        }
    }

    try {
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            outputStream.write(exportText.toByteArray())
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
