package com.jkuhldev.openhand.ui.screen

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.jkuhldev.openhand.data.NavigationBarItemData
import com.jkuhldev.openhand.data.PREVIEW_PRINTER
import com.jkuhldev.openhand.data.Printer
import com.jkuhldev.openhand.ui.tab.FilesTab
import com.jkuhldev.openhand.ui.tab.LiveViewTab
import com.jkuhldev.openhand.ui.tab.StatusTab
import com.jkuhldev.openhand.ui.theme.OpenHandTheme

/**
 * Screen for interacting with a selected printer
 * @param printer Printer that is being interacted with
 * @param startTab Tab in the bottom NavigationBar that should be selected on startup
 * @param onBack Callback triggered when the back button is pressed
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrinterDetailScreen(printer: Printer, startTab: Int = 0, onBack: () -> Unit = {}) {
    var selectedTab by rememberSaveable { mutableIntStateOf(startTab) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = printer.name,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
                listOf(
                    NavigationBarItemData("Status", Icons.Default.BarChart),
                    NavigationBarItemData("Live View", Icons.Default.Preview),
                    NavigationBarItemData("Files", Icons.AutoMirrored.Filled.InsertDriveFile)
                ).forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
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

@PreviewLightDark()
@Composable
private fun PrinterDetailPreview() {
    OpenHandTheme {
        PrinterDetailScreen(
            printer = PREVIEW_PRINTER
        )
    }
}
