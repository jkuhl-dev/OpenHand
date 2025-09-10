package com.virtualmememachine.openhand.ui.card

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.virtualmememachine.openhand.ui.theme.OpenHandTheme

/**
 * Preconfigured Card with a nicely formatted layout
 * @param title Title to be displayed in top left corner of the card
 * @param indicator Indicator to be displayed in the top right corner of the card
 * @param content Composable content to be displayed in the card
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpenHandCard(
    title: String,
    indicator: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {}
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, fontWeight = FontWeight.Bold)
                indicator()
            }
            content()
        }
    }
}

@PreviewLightDark()
@Composable
private fun FilamentCardPreview() {
    OpenHandTheme {
        OpenHandCard(
            title = "Preview Card",
            indicator = { Text(text = "Indicator") },
            content = { Text(text = "Content") }
        )
    }
}
