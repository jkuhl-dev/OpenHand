package com.jkuhldev.openhand

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.jkuhldev.openhand.data.Printer
import com.jkuhldev.openhand.data.PrinterDataStore
import com.jkuhldev.openhand.ui.screen.PrinterDetailScreen
import com.jkuhldev.openhand.ui.screen.PrinterListScreen
import com.jkuhldev.openhand.ui.theme.OpenHandTheme

/**
 * Main activity that acts as an entry point to the app
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OpenHandTheme {
                OpenHandApp()
            }
        }
    }
}

/**
 * Top level Composable that handles navigation around the app
 * @param isPreview Boolean that denotes if the Composable is being rendered in a preview or not
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpenHandApp(isPreview: Boolean = LocalInspectionMode.current) {
    val context = LocalContext.current
    val printerDataStore = remember(context) { PrinterDataStore(if (isPreview) null else context) }
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "PrinterListScreen",
    ) {
        composable(
            route = "PrinterListScreen",
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) }
        ) {
            PrinterListScreen(
                printerDataStore = printerDataStore,
                onPrinterClick = { printer ->
                    navController.navigate(printer)
                }
            )
        }
        composable<Printer>(
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) { backStackEntry ->
            PrinterDetailScreen(
                printer = backStackEntry.toRoute(),
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@PreviewLightDark()
@Composable
fun OpenHandAppPreview() {
    OpenHandTheme {
        OpenHandApp()
    }
}
