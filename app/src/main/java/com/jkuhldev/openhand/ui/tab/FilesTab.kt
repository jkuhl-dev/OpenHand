package com.jkuhldev.openhand.ui.tab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jkuhldev.openhand.data.PREVIEW_PRINTER
import com.jkuhldev.openhand.data.Printer
import com.jkuhldev.openhand.ui.card.OpenHandCard
import com.jkuhldev.openhand.ui.screen.PrinterDetailScreen
import com.jkuhldev.openhand.ui.theme.OpenHandTheme
import java.util.Locale

/**
 * Tab for managing files stored on the printer
 * @param printer Printer that is being interacted with
 * @param viewModel ViewModel that yields state data for this tab
 * @param isPreview Boolean that denotes if the Composable is being rendered in a preview or not
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilesTab(
    printer: Printer,
    viewModel: FilesTabViewModel = viewModel(),
    isPreview: Boolean = LocalInspectionMode.current
) {
    val currentPath by viewModel.currentPath.collectAsStateWithLifecycle()
    val exception by viewModel.exception.collectAsStateWithLifecycle()
    val files by viewModel.files.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel, printer) {
        viewModel.startClient(printer, isPreview)
    }

    Surface(color = MaterialTheme.colorScheme.surfaceContainerHigh) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(50.dp)
        ) {
            Text(
                text = if (isLoading) "Loading..." else currentPath,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
            )
            VerticalDivider()
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(50.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }
            }
        }
    }

    if (exception != null) {
        Spacer(modifier = Modifier.size(8.dp))
        OpenHandCard("Error") {
            Text(text = exception?.toString() ?: "Unknown error occurred")
        }
        return
    }

    LazyColumn {
        itemsIndexed(files) { index, file ->
            ListItem(
                headlineContent = {
                    Text(
                        text = file.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                leadingContent = {
                    Icon(
                        imageVector = if (file.isDirectory) Icons.Filled.Folder else Icons.AutoMirrored.Filled.InsertDriveFile,
                        contentDescription = file.name
                    )
                },
                trailingContent = { if (!file.isDirectory) Text(text = getFileSizeString(file.size)) },
            )
            if (index < files.size - 1) {
                HorizontalDivider()
            }
        }
    }
}

/**
 * Generates a nicely formatted file size string and returns it
 * @param fileSize Long representing the file size as bytes
 * @return Nicely formatted file size string derived from the number of bytes
 */
private fun getFileSizeString(fileSize: Long): String {
    if (fileSize < 1024) return "$fileSize B"
    val kb = fileSize / 1024.0
    if (kb < 1024) return String.format(Locale.getDefault(), "%.1f KB", kb)
    val mb = kb / 1024.0
    if (mb < 1024) return String.format(Locale.getDefault(), "%.1f MB", mb)
    val gb = mb / 1024.0
    return String.format(Locale.getDefault(), "%.1f GB", gb)
}

@PreviewLightDark()
@Composable
private fun FilesTabPreview() {
    OpenHandTheme {
        PrinterDetailScreen(
            printer = PREVIEW_PRINTER,
            startTab = 2,
        )
    }
}
