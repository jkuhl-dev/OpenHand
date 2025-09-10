package com.virtualmememachine.openhand.ui.card.status

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.virtualmememachine.openhand.data.PREVIEW_PRINTER_STATUS
import com.virtualmememachine.openhand.data.PrinterStatus
import com.virtualmememachine.openhand.ui.card.OpenHandCard
import com.virtualmememachine.openhand.ui.theme.OpenHandTheme

/**
 * Card for displaying printer thermals information
 * @param status PrinterStatus object containing information about the printer
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThermalsCard(status: PrinterStatus) {
    OpenHandCard(title = "Thermals") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            ThermalColumn("Nozzle", status.nozzleTemperature, status.nozzleTargetTemperature)
            ThermalColumn("Bed", status.bedTemperature, status.bedTargetTemperature)
            ThermalColumn("Chamber", status.chamberTemperature, status.chamberTargetTemperature)
        }
    }
}

/**
 * Column containing nicely formated thermal data for a single component
 * @param label Name of the component who's thermals are displayed
 * @param inputTemperature1 The first temperature value as a Double, or null if unavailable
 * @param inputTemperature2 The second temperature value as a Double, or null if unavailable
 */
@Composable
private fun ThermalColumn(label: String, inputTemperature1: Double?, inputTemperature2: Double?) {
    val temperature1 = if (inputTemperature1 == null) "-" else "$inputTemperature1°"
    val temperature2 = if (inputTemperature2 == null) "-" else "$inputTemperature2°"

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .width(90.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text(text = label)
            Text(
                text = "$temperature1 / $temperature2",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@PreviewLightDark()
@Composable
private fun ThermalsCardPreview() {
    OpenHandTheme {
        ThermalsCard(
            status = PREVIEW_PRINTER_STATUS
        )
    }
}
