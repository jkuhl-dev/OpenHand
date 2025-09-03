package com.virtualmememachine.openhand.ui.cards.status

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.virtualmememachine.openhand.data.ConnectionState
import com.virtualmememachine.openhand.data.PREVIEW_PRINTERS
import com.virtualmememachine.openhand.data.PREVIEW_PRINTER_STATUS
import com.virtualmememachine.openhand.data.Printer
import com.virtualmememachine.openhand.data.PrinterStatus
import com.virtualmememachine.openhand.ui.theme.OpenHandTheme

/**
 * Card for displaying printer connection information
 * @param printer Printer that we are connecting to
 * @param status PrinterStatus object containing information about the printer
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionCard(printer: Printer, status: PrinterStatus) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(text = "Connection", fontWeight = FontWeight.Bold)
                Icon(
                    imageVector = when (status.connectionState) {
                        ConnectionState.CONNECTING -> {
                            Icons.Default.AccessTimeFilled
                        }

                        ConnectionState.SUCCESS -> {
                            Icons.Default.CheckCircle
                        }

                        ConnectionState.ERROR -> {
                            Icons.Default.Cancel
                        }
                    },
                    contentDescription = "Connection Indicator",
                )
            }
            when (status.connectionState) {
                ConnectionState.CONNECTING -> {
                    Text(text = "Connecting...")
                }

                ConnectionState.SUCCESS -> {
                    Text(text = "IP Address: ${printer.ipAddress}")
                    Text(text = "Last Updated: ${getLastUpdatedString(status.lastUpdatedMillis)}")
                    Text(text = "Printer Serial: ${status.printerSerial ?: "N/A"}")
                }

                ConnectionState.ERROR -> {
                    Text(text = "Error: ${status.error}")
                }
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

@Preview(showBackground = true)
@Composable
private fun ConnectionCardPreview() {
    OpenHandTheme {
        ConnectionCard(
            printer = PREVIEW_PRINTERS.first(),
            status = PREVIEW_PRINTER_STATUS
        )
    }
}
