package com.virtualmememachine.openhand.ui.card.status

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.virtualmememachine.openhand.data.PREVIEW_PRINTER_STATUS
import com.virtualmememachine.openhand.data.PrinterStatus
import com.virtualmememachine.openhand.ui.card.OpenHandCard
import com.virtualmememachine.openhand.ui.theme.OpenHandTheme

/**
 * Card for displaying information about active prints
 * @param status PrinterStatus object containing information about the printer
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressCard(status: PrinterStatus) {
    OpenHandCard(
        title = "Progress",
        indicator = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LinearProgressIndicator(
                    progress = { (status.printProgress ?: 0) / 100f },
                    gapSize = (-100).dp,
                    drawStopIndicator = {},
                    modifier = Modifier
                        .height(15.dp)
                        .width(175.dp)
                )
                Text(text = "${status.printProgress ?: "N/A"}%", fontWeight = FontWeight.Bold)
            }
        }
    ) {
        Text(text = "Name: ${status.printName ?: "N/A"}")
        Text(text = "Layer: ${status.layerCurrent ?: "N/A"} / ${status.layerTotal ?: "N/A"}")
        Text(text = "Time Remaining: ${getTimeRemainingString(status.printTimeRemainingMinutes)}")
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

@PreviewLightDark()
@Composable
private fun ProgressCardPreview() {
    OpenHandTheme {
        ProgressCard(
            status = PREVIEW_PRINTER_STATUS
        )
    }
}
