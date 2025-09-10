package com.virtualmememachine.openhand.ui.card.status

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.virtualmememachine.openhand.data.PREVIEW_PRINTER_STATUS
import com.virtualmememachine.openhand.data.PrinterStatus
import com.virtualmememachine.openhand.ui.theme.OpenHandTheme

/**
 * Card for displaying information about active prints
 * @param status PrinterStatus object containing information about the printer
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressCard(status: PrinterStatus) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Progress", fontWeight = FontWeight.Bold)
                Text(text = "${status.printProgress ?: "N/A"}%", fontWeight = FontWeight.Bold)
            }
            Text(text = "Name: ${status.printName ?: "N/A"}")
            Text(text = "Layer: ${status.layerCurrent ?: "N/A"} / ${status.layerTotal ?: "N/A"}")
            Text(text = "Time Remaining: ${getTimeRemainingString(status.printTimeRemainingMinutes)}")
            LinearProgressIndicator(
                progress = { (status.printProgress ?: 0) / 100f },
                gapSize = (-100).dp,
                drawStopIndicator = {},
                modifier = Modifier
                    .height(30.dp)
                    .padding(top = 8.dp)
                    .fillMaxWidth()
            )
        }
    }
}

/**
 * Gets a nicely formatted time remaining string using the given time input
 * @param inputTime The remaining time value as an Int, or null if unavailable
 * @return String containing the time remaining or "N/A" if the input is null
 */
private fun getTimeRemainingString(inputTime: Int?): String {
    if (inputTime == null) return "N/A"
    val hours = inputTime / 60
    val minutes = inputTime % 60

    if (hours == 0 && minutes == 0) return "0 minutes"

    val timeString = StringBuilder()
    if (hours > 0) timeString.append("$hours hour")
    if (hours > 1) timeString.append("s")
    if (hours > 0 && minutes > 0) timeString.append(" ")
    if (minutes > 0) timeString.append("$minutes minute")
    if (minutes > 1) timeString.append("s")
    return timeString.toString()
}

@Preview(showBackground = true)
@Composable
private fun ProgressCardPreview() {
    OpenHandTheme {
        ProgressCard(
            status = PREVIEW_PRINTER_STATUS
        )
    }
}
