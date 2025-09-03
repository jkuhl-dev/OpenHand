package com.virtualmememachine.openhand.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.virtualmememachine.openhand.ui.theme.OpenHandTheme

private val AccessCodeRegex: Regex = Regex("^[A-Za-z0-9]{8}$")
private val IPv4Regex: Regex =
    Regex("^(25[0-5]|2[0-4]\\d|1?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|1?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|1?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|1?\\d?\\d)$")

/**
 * Dialog for adding a printer
 * @param onConfirm Callback invoked when the user completes input, returns user input as strings
 * @param onDismiss Callback invoked when the user cancels or dismisses the dialog
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPrinterDialog(
    onConfirm: (printerName: String, ipAddress: String, accessCode: String) -> Unit,
    onDismiss: () -> Unit,
    editMode: Boolean = false,
    initialName: String = "New Printer",
    initialIpAddress: String = "",
    initialAccessCode: String = "",
) {
    var printerName by remember { mutableStateOf(initialName) }
    var ipAddress by remember { mutableStateOf(initialIpAddress) }
    var accessCode by remember { mutableStateOf(initialAccessCode) }
    val isIpAddressValid = remember(ipAddress) { IPv4Regex.matches(ipAddress.trim()) }
    val isAccessCodeValid = remember(accessCode) { AccessCodeRegex.matches(accessCode.trim()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (editMode) "Edit Printer" else "Add Printer") },
        text = {
            Column {
                TextField(
                    value = printerName,
                    onValueChange = { printerName = it },
                    singleLine = true,
                    label = { Text("Printer Name") },
                    placeholder = { Text("Example: New Printer") },
                    isError = printerName.isBlank()
                )
                TextField(
                    value = ipAddress,
                    onValueChange = { ipAddress = it },
                    singleLine = true,
                    label = { Text("IP Address") },
                    placeholder = { Text("Example: 192.168.1.10") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.padding(top = 8.dp),
                    isError = ipAddress.isNotBlank() && !isIpAddressValid
                )
                TextField(
                    value = accessCode,
                    onValueChange = { accessCode = it },
                    singleLine = true,
                    label = { Text("Access Code") },
                    placeholder = { Text("Example: abcd1234") },
                    modifier = Modifier.padding(top = 8.dp),
                    isError = accessCode.isNotBlank() && !isAccessCodeValid
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(printerName.trim(), ipAddress.trim(), accessCode.trim())
                },
                enabled = isIpAddressValid && isAccessCodeValid
            ) {
                Text(if (editMode) "Save" else "Add")
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
        AddPrinterDialog(
            onConfirm = { _, _, _ -> },
            onDismiss = {},
        )
    }
}
