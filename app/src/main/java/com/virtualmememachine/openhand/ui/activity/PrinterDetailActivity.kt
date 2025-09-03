package com.virtualmememachine.openhand.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
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
import com.virtualmememachine.openhand.ui.tab.FilesTab
import com.virtualmememachine.openhand.ui.tab.LiveViewTab
import com.virtualmememachine.openhand.ui.tab.StatusTab
import com.virtualmememachine.openhand.ui.theme.OpenHandTheme

/**
 * Activity for interacting with a selected printer
 */
class PrinterDetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OpenHandTheme {
                PrinterDetailScreen(intent.getStringExtra(PRINTER_MAP_KEY) ?: "")
            }
        }
    }

    companion object {
        const val PRINTER_MAP_KEY = "printerMapKey"
    }
}

/**
 * Activity for interacting with a selected printer
 * @param printerMapKey Key for retrieving the selected Printer from the data store
 * @param previewPrinters List of printers to be displayed in previews
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrinterDetailScreen(
    printerMapKey: String,
    startTab: Int = 0,
    previewPrinters: List<Printer> = emptyList()
) {
    val context = LocalContext.current
    val printerDataStore = remember(context) { PrinterDataStore(context.applicationContext) }
    val printersMap by printerDataStore.printersFlow.collectAsState(initial = previewPrinters.associateBy { it.ipAddress })
    val printer = printersMap[printerMapKey]
    var selectedTab by rememberSaveable { mutableIntStateOf(startTab) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = printer?.name ?: "Printer Not Found",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        (context as? android.app.Activity)?.finish()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.BarChart, contentDescription = "Status") },
                    label = { Text("Status") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Preview, contentDescription = "Live View") },
                    label = { Text("Live View") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Filled.InsertDriveFile,
                            contentDescription = "Files"
                        )
                    },
                    label = { Text("Files") }
                )
            }
        }
    ) { innerPadding ->

        // If printer is null don't load any tab content
        if (printer == null) {
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            when (selectedTab) {
                0 -> StatusTab(printer = printer)
                1 -> LiveViewTab(printer = printer)
                2 -> FilesTab(printer = printer)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PrinterDetailPreview() {
    OpenHandTheme {
        PrinterDetailScreen(
            printerMapKey = PREVIEW_PRINTERS.first().ipAddress,
            previewPrinters = PREVIEW_PRINTERS
        )
    }
}
