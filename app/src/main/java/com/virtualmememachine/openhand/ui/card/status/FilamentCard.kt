package com.virtualmememachine.openhand.ui.card.status

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.virtualmememachine.openhand.data.FilamentStatus
import com.virtualmememachine.openhand.data.PREVIEW_PRINTER_STATUS
import com.virtualmememachine.openhand.data.PrinterStatus
import com.virtualmememachine.openhand.ui.card.OpenHandCard
import com.virtualmememachine.openhand.ui.theme.OpenHandTheme

/**
 * Card for displaying printer filament information
 * @param status PrinterStatus object containing information about the printer
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilamentCard(status: PrinterStatus) {
    OpenHandCard(title = "Filament") {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            status.loadedFilament.chunked(4).forEach { row ->
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        row.forEach { filament -> FilamentSwatch(filament) }
                    }
                }
            }
        }
    }
}

/**
 * Swatch that displays filament color and type
 * @param filament FilamentStatus for the filament that should be displayed in the swatch
 */
@Composable
private fun FilamentSwatch(filament: FilamentStatus) {
    val swatchColor = filament.color ?: MaterialTheme.colorScheme.surfaceVariant
    val textColorDark =
        if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.inverseOnSurface
    val textColorLight =
        if (isSystemInDarkTheme()) MaterialTheme.colorScheme.inverseOnSurface else MaterialTheme.colorScheme.onSurface

    Surface(
        modifier = Modifier.size(60.dp),
        shape = MaterialTheme.shapes.medium,
        color = swatchColor,
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = filament.type ?: "Empty",
                color = if (swatchColor.luminance() < 0.25) textColorDark else textColorLight,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@PreviewLightDark()
@Composable
private fun FilamentCardPreview() {
    OpenHandTheme {
        FilamentCard(
            status = PREVIEW_PRINTER_STATUS,
        )
    }
}
