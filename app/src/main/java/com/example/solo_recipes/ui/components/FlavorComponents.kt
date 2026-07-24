package com.example.solo_recipes.ui.components

import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.solo_recipes.model.FlavorComponent
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FlavorItem(
    component: FlavorComponent,
    onUpdate: (FlavorComponent) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(component.imageUrl?.let { Uri.parse(it) }) }
    val tempPhotoUri = remember { mutableStateOf<Uri?>(null) }
    var showPhotoOptions by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            imageUri = uri
            onUpdate(component.copy(imageUrl = uri.toString()))
        }
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && tempPhotoUri.value != null) {
            imageUri = tempPhotoUri.value
            onUpdate(component.copy(imageUrl = tempPhotoUri.value.toString()))
        }
    }

    if (showPhotoOptions) {
        AlertDialog(
            onDismissRequest = { showPhotoOptions = false },
            title = { Text("Add Photo") },
            confirmButton = {
                TextButton(onClick = {
                    showPhotoOptions = false
                    val photoFile = File.createTempFile(
                        "IMG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}_",
                        ".jpg",
                        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    )
                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
                    tempPhotoUri.value = uri
                    cameraLauncher.launch(uri)
                }) { Text("Camera") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPhotoOptions = false
                    galleryLauncher.launch(androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }) { Text("Gallery") }
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
            Box(
                Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFAF9F6))
                    .clickable { showPhotoOptions = true }
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.AddAPhoto,
                        null,
                        modifier = Modifier.align(Alignment.Center),
                        tint = Color.Gray
                    )
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(component.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(component.note ?: "", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Checkbox(
                checked = component.isStocked,
                onCheckedChange = { onUpdate(component.copy(isStocked = it)) },
                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
            )
            if (onDelete != null) {
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
}

@Composable
fun SectionHeader(title: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(8.dp))
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.ExtraBold
        )
    }
}
