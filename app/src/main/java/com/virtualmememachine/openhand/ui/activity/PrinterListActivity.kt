package com.virtualmememachine.openhand.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.virtualmememachine.openhand.data.PREVIEW_PRINTERS
import com.virtualmememachine.openhand.data.Printer
import com.virtualmememachine.openhand.data.PrinterDataStore
import com.virtualmememachine.openhand.ui.dialog.AddPrinterDialog
import com.virtualmememachine.openhand.ui.dialog.RemovePrinterDialog
import com.virtualmememachine.openhand.ui.theme.OpenHandTheme
import kotlinx.coroutines.launch

/**
 * Activity for selecting or adding a printer
 */
class PrinterListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OpenHandTheme {
                PrinterListScreen()
            }
        }
    }
}

/**
 * Activity for selecting or adding a printer
 * @param previewPrinters List of printers to be displayed in previews
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrinterListScreen(previewPrinters: List<Printer> = emptyList()) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val printerDataStore = remember(context) { PrinterDataStore(context.applicationContext) }
    val printersMap by printerDataStore.printersFlow.collectAsState(initial = previewPrinters.associateBy { it.ipAddress })
    val editPrinterTarget = remember { mutableStateOf<Printer?>(null) }
    val removePrinterTarget = remember { mutableStateOf<Printer?>(null) }
    var showAddPrinterDialog by rememberSaveable { mutableStateOf(false) }
    var showEditPrinterDialog by rememberSaveable { mutableStateOf(false) }
    var showRemovePrinterDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Printers", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddPrinterDialog = true }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Printer")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(printersMap.values.toList()) { printer ->
                    ListItem(
                        headlineContent = { Text(printer.name) },
                        supportingContent = { Text(printer.ipAddress) },
                        trailingContent = {
                            Row {
                                IconButton(onClick = {
                                    editPrinterTarget.value = printer
                                    showEditPrinterDialog = true
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Printer"
                                    )
                                }
                                IconButton(onClick = {
                                    removePrinterTarget.value = printer
                                    showRemovePrinterDialog = true
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove Printer"
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .clickable {
                                val intent = Intent(context, PrinterDetailActivity::class.java)
                                intent.putExtra(
                                    PrinterDetailActivity.PRINTER_MAP_KEY,
                                    printer.ipAddress
                                )
                                context.startActivity(intent)
                            }
                            .padding(horizontal = 8.dp)
                    )
                }
            }
        }

        if (showAddPrinterDialog) {
            AddPrinterDialog(
                onConfirm = { printerName: String, ipAddress: String, accessCode: String ->
                    scope.launch {
                        printerDataStore.addOrUpdatePrinter(
                            Printer(
                                name = printerName,
                                ipAddress = ipAddress,
                                accessCode = accessCode
                            )
                        )
                    }
                    showAddPrinterDialog = false
                },
                onDismiss = {
                    showAddPrinterDialog = false
                },
            )
        }

        if (showEditPrinterDialog) {
            val target = editPrinterTarget.value
            if (target != null) {
                AddPrinterDialog(
                    onConfirm = { printerName: String, ipAddress: String, accessCode: String ->
                        scope.launch {
                            // If the IP changed remove the old entry to prevent duplicates
                            if (ipAddress != target.ipAddress) {
                                printerDataStore.removePrinter(target)
                            }
                            printerDataStore.addOrUpdatePrinter(
                                Printer(
                                    name = printerName,
                                    ipAddress = ipAddress,
                                    accessCode = accessCode
                                )
                            )
                        }
                        showEditPrinterDialog = false
                        editPrinterTarget.value = null
                    },
                    onDismiss = {
                        showEditPrinterDialog = false
                        editPrinterTarget.value = null
                    },
                    editMode = true,
                    initialName = target.name,
                    initialIpAddress = target.ipAddress,
                    initialAccessCode = target.accessCode,
                )
            }
        }

        if (showRemovePrinterDialog) {
            RemovePrinterDialog(
                printer = removePrinterTarget.value,
                onConfirm = {
                    scope.launch {
                        printerDataStore.removePrinter(removePrinterTarget.value)
                        removePrinterTarget.value = null
                    }
                    showRemovePrinterDialog = false
                },
                onDismiss = {
                    showRemovePrinterDialog = false
                },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PrinterListScreenPreview() {
    OpenHandTheme {
        PrinterListScreen(
            previewPrinters = PREVIEW_PRINTERS
        )
    }
}
