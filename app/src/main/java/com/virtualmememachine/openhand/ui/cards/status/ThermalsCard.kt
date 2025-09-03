package com.virtualmememachine.openhand.ui.cards.status

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.virtualmememachine.openhand.data.PREVIEW_PRINTER_STATUS
import com.virtualmememachine.openhand.data.PrinterStatus
import com.virtualmememachine.openhand.ui.theme.OpenHandTheme

/**
 * Card for displaying printer thermals information
 * @param status PrinterStatus object containing information about the printer
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThermalsCard(status: PrinterStatus) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Thermals", fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    Text(text = "Nozzle")
                    Text(
                        text = getTemperatureString(
                            status.nozzleTemperature,
                            status.nozzleTargetTemperature
                        )
                    )
                }
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    Text(text = "Bed")
                    Text(
                        text = getTemperatureString(
                            status.bedTemperature,
                            status.bedTargetTemperature
                        )
                    )
                }
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    Text(text = "Chamber")
                    Text(
                        text = getTemperatureString(
                            status.chamberTemperature,
                            status.chamberTargetTemperature
                        )
                    )
                }
            }
        }
    }
}

/**
 * Gets a nicely formatted temperature comparison string using the given input temperatures
 * @param inputTemperature1 The first temperature value as a Double, or null if unavailable
 * @param inputTemperature2 The second temperature value as a Double, or null if unavailable
 * @return String containing the temperatures or "-" if null
 */
private fun getTemperatureString(inputTemperature1: Double?, inputTemperature2: Double?): String {
    val temperature1 = if (inputTemperature1 == null) "-" else "$inputTemperature1°"
    val temperature2 = if (inputTemperature2 == null) "-" else "$inputTemperature2°"

    return "$temperature1 / $temperature2"
}

@Preview(showBackground = true)
@Composable
private fun ThermalsCardPreview() {
    OpenHandTheme {
        ThermalsCard(
            status = PREVIEW_PRINTER_STATUS
        )
    }
}
