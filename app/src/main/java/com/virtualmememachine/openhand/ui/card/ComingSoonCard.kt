package com.virtualmememachine.openhand.ui.card

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.virtualmememachine.openhand.ui.theme.OpenHandTheme

/**
 * Placeholder card for functionality that isn't available yet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComingSoonCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Coming Soon", fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ComingSoonCardPreview() {
    OpenHandTheme {
        ComingSoonCard()
    }
}
