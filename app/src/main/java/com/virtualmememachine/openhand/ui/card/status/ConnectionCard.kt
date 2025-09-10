package com.virtualmememachine.openhand.ui.card.status

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.virtualmememachine.openhand.data.ConnectionState
import com.virtualmememachine.openhand.data.PREVIEW_PRINTER
import com.virtualmememachine.openhand.data.PREVIEW_PRINTER_STATUS
import com.virtualmememachine.openhand.data.Printer
import com.virtualmememachine.openhand.data.PrinterStatus
import com.virtualmememachine.openhand.ui.card.OpenHandCard
import com.virtualmememachine.openhand.ui.theme.OpenHandTheme

/**
 * Card for displaying printer connection information
 * @param printer Printer that we are connecting to
 * @param status PrinterStatus object containing information about the printer
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionCard(printer: Printer, status: PrinterStatus) {
    OpenHandCard(
        title = "Connection",
        indicator = {
            Icon(
                imageVector = when (status.connectionState) {
                    ConnectionState.CONNECTING -> Icons.Default.AccessTimeFilled
                    ConnectionState.ERROR -> Icons.Default.Cancel
                    ConnectionState.SUCCESS -> Icons.Default.CheckCircle
                },
                contentDescription = "Connection Indicator",
            )
        }
    ) {
        when (status.connectionState) {
            ConnectionState.CONNECTING -> Text(text = "Connecting...")
            ConnectionState.ERROR -> Text(text = "Error: ${status.error}")
            ConnectionState.SUCCESS -> {
                Text(text = "IP Address: ${printer.ipAddress}")
                Text(text = "Last Updated: ${getLastUpdatedString(status.lastUpdatedMillis)}")
                Text(text = "Printer Serial: ${status.printerSerial ?: "N/A"}")
            }
        }
    }
}

/**
 * Gets a nicely formatted last updated string using the given last updated input
 * @param lastUpdatedMillis Unix timestamp containing the time when the status was last updated
 * @return String containing the last updated time in seconds or "N/A" if the input is null
 */
private fun getLastUpdatedString(lastUpdatedMillis: Long?): String {
    if (lastUpdatedMillis == null) return "N/A"
    val diffMs = (System.currentTimeMillis() - lastUpdatedMillis).coerceAtLeast(0)
    return "%.2f seconds ago".format(diffMs / 1000f)
}

@PreviewLightDark
@Composable
private fun ConnectionCardPreview() {
    OpenHandTheme {
        ConnectionCard(
            printer = PREVIEW_PRINTER,
            status = PREVIEW_PRINTER_STATUS
        )
    }
}
