package com.jkuhldev.openhand.ui.tab

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.jkuhldev.openhand.data.ConnectionState
import com.jkuhldev.openhand.data.PREVIEW_PRINTER
import com.jkuhldev.openhand.data.PREVIEW_PRINTER_STATUS
import com.jkuhldev.openhand.data.Printer
import com.jkuhldev.openhand.network.PrinterStatusClient
import com.jkuhldev.openhand.ui.card.status.ConnectionCard
import com.jkuhldev.openhand.ui.card.status.FilamentCard
import com.jkuhldev.openhand.ui.card.status.ProgressCard
import com.jkuhldev.openhand.ui.card.status.ThermalsCard
import com.jkuhldev.openhand.ui.screen.PrinterDetailScreen
import com.jkuhldev.openhand.ui.theme.OpenHandTheme

/**
 * Tab for viewing printer status
 * @param printer Printer that is being interacted with
 * @param isPreview Boolean that denotes if the Composable is being rendered in a preview or not
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusTab(printer: Printer, isPreview: Boolean = LocalInspectionMode.current) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val printerStatusClient = remember(printer.ipAddress, printer.accessCode) {
        PrinterStatusClient(
            context = context.applicationContext,
            scope = scope,
            printer = printer
        )
    }
    val liveStatus by printerStatusClient.status.collectAsState()
    val status = if (isPreview) PREVIEW_PRINTER_STATUS else liveStatus

    if (!isPreview) {
        DisposableEffect(printerStatusClient) {
            printerStatusClient.start()
            onDispose { printerStatusClient.stop() }
        }
    }

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Spacer(modifier = Modifier.size(size = 8.dp))
        if (status.connectionState == ConnectionState.SUCCESS) {
            ProgressCard(status = status)
            ThermalsCard(status = status)
            FilamentCard(status = status)
        }
        ConnectionCard(printer = printer, status = status)
        Spacer(modifier = Modifier.size(size = 8.dp))
    }
}

@PreviewLightDark
@Composable
private fun StatusTabPreview() {
    OpenHandTheme {
        PrinterDetailScreen(
            printer = PREVIEW_PRINTER,
            startTab = 0,
        )
    }
}
