package com.jkuhldev.openhand.ui.card

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.jkuhldev.openhand.ui.theme.OpenHandTheme

/**
 * Placeholder card for functionality that isn't available yet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComingSoonCard() {
    OpenHandCard(title = "Coming Soon")
}

@PreviewLightDark()
@Composable
private fun ComingSoonCardPreview() {
    OpenHandTheme {
        ComingSoonCard()
    }
}
