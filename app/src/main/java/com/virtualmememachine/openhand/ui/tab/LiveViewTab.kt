package com.virtualmememachine.openhand.ui.tab

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.virtualmememachine.openhand.data.PREVIEW_PRINTERS
import com.virtualmememachine.openhand.data.Printer
import com.virtualmememachine.openhand.ui.activity.PrinterDetailScreen
import com.virtualmememachine.openhand.ui.cards.ComingSoonCard
import com.virtualmememachine.openhand.ui.theme.OpenHandTheme

/**
 * Tab for viewing the printer's camera feed
 * @param printer Printer that is being interacted with
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveViewTab(printer: Printer) {
    ComingSoonCard()
}

@Preview(showBackground = true)
@Composable
private fun LiveViewTabPreview() {
    OpenHandTheme {
        PrinterDetailScreen(
            printerMapKey = PREVIEW_PRINTERS.first().ipAddress,
            startTab = 1,
            previewPrinters = PREVIEW_PRINTERS
        )
    }
}
