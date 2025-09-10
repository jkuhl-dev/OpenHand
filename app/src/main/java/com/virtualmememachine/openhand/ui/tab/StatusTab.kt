package com.virtualmememachine.openhand.ui.tab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.virtualmememachine.openhand.data.ConnectionState
import com.virtualmememachine.openhand.data.PREVIEW_PRINTER
import com.virtualmememachine.openhand.data.PREVIEW_PRINTER_STATUS
import com.virtualmememachine.openhand.data.Printer
import com.virtualmememachine.openhand.network.PrinterStatusClient
import com.virtualmememachine.openhand.ui.card.status.ConnectionCard
import com.virtualmememachine.openhand.ui.card.status.ProgressCard
import com.virtualmememachine.openhand.ui.card.status.ThermalsCard
import com.virtualmememachine.openhand.ui.screen.PrinterDetailScreen
import com.virtualmememachine.openhand.ui.theme.OpenHandTheme

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

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (status.connectionState == ConnectionState.SUCCESS) {
            ProgressCard(status = status)
            ThermalsCard(status = status)
        }
        ConnectionCard(printer = printer, status = status)
    }
}

@Preview(showBackground = true)
@Composable
private fun StatusTabPreview() {
    OpenHandTheme {
        PrinterDetailScreen(
            printer = PREVIEW_PRINTER,
            startTab = 0,
        )
    }
}
