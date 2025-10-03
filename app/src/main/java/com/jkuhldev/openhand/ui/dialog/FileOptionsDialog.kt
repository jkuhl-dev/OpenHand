package com.jkuhldev.openhand.ui.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.jkuhldev.openhand.data.FileOptionsItemData
import com.jkuhldev.openhand.ui.theme.OpenHandTheme
import org.apache.commons.net.ftp.FTPFile

/**
 * Dialog for triggering file management actions
 * @param targetFile File that we are interacting with
 * @param onDismiss Callback invoked when the dialog is dismissed or cancel is selected
 * @param onDownload Callback invoked when download is selected
 * @param onRename Callback invoked when rename is selected
 * @param onDelete Callback invoked when delete is selected
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileOptionsDialog(
    targetFile: FTPFile,
    onDismiss: () -> Unit,
    onDownload: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (targetFile.isFile) "File Options" else "Folder Options") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                listOf(
                    FileOptionsItemData("Download", Icons.Filled.Download, onDownload),
                    FileOptionsItemData("Rename", Icons.Filled.Edit, onRename),
                    FileOptionsItemData("Delete", Icons.Filled.Delete, onDelete),
                    FileOptionsItemData("Cancel", Icons.Filled.Cancel, onDismiss),
                ).forEach { option ->
                    ListItem(
                        headlineContent = { Text(option.label) },
                        leadingContent = { Icon(option.icon, contentDescription = option.label) },
                        modifier = Modifier.clickable { option.onClick(); onDismiss() },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}

@PreviewLightDark
@Composable
private fun FileMenuDialogPreview() {
    OpenHandTheme {
        FileOptionsDialog(
            targetFile = FTPFile(),
            onDismiss = {},
            onDownload = {},
            onRename = {},
            onDelete = {},
        )
    }
}
