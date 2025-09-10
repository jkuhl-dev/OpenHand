package com.virtualmememachine.openhand.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.virtualmememachine.openhand.data.PREVIEW_PRINTER
import com.virtualmememachine.openhand.data.Printer
import com.virtualmememachine.openhand.ui.theme.OpenHandTheme

/**
 * Dialog for removing a printer
 * @param printer Printer that we are prompting to delete
 * @param onConfirm Callback invoked when the user taps 'Confirm'
 * @param onDismiss Callback invoked when the user cancels or dismisses the dialog
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemovePrinterDialog(
    printer: Printer?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Remove Printer") },
        text = {
            Column {
                Text("Are you sure you want to remove:")
                Text("${printer?.name}")
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun AddPrinterDialogPreview() {
    OpenHandTheme {
        RemovePrinterDialog(
            printer = PREVIEW_PRINTER,
            onConfirm = {},
            onDismiss = {},
        )
    }
}
