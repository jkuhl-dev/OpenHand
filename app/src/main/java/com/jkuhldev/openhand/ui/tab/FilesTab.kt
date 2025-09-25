package com.jkuhldev.openhand.ui.tab

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.jkuhldev.openhand.data.PREVIEW_PRINTER
import com.jkuhldev.openhand.data.Printer
import com.jkuhldev.openhand.network.PrinterFilesClient
import com.jkuhldev.openhand.ui.screen.PrinterDetailScreen
import com.jkuhldev.openhand.ui.theme.OpenHandTheme
import kotlinx.coroutines.launch
import org.apache.commons.net.ftp.FTPFile

/**
 * Tab for managing files stored on the printer
 * @param printer Printer that is being interacted with
 * @param isPreview Boolean that denotes if the Composable is being rendered in a preview or not
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilesTab(printer: Printer, isPreview: Boolean = LocalInspectionMode.current) {
    val scope = rememberCoroutineScope()
    var files by remember(printer.ipAddress, printer.accessCode) {
        mutableStateOf<List<FTPFile>>(emptyList())
    }
    val printerFilesClient = remember(printer.ipAddress, printer.accessCode) {
        PrinterFilesClient(
            scope = scope,
            printer = printer
        )
    }

    if (!isPreview) {
        DisposableEffect(printerFilesClient) {
            onDispose { scope.launch { printerFilesClient.stop() } }
        }

        LaunchedEffect(printerFilesClient) {
            printerFilesClient.start()
            files = printerFilesClient.listCurrentDirectory()
        }
    } else {
        files = listOf(
            FTPFile().apply { name = "Preview_File1.3mf" },
            FTPFile().apply { name = "Preview_File2.zip" },
            FTPFile().apply { name = "Preview_File3.mp4" }
        )
    }

    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
        items(files, key = { it.name + "-" + it.timestamp?.time?.time }) { file ->
            ListItem(headlineContent = { Text(file.name ?: "Unnamed") })
        }
    }
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
