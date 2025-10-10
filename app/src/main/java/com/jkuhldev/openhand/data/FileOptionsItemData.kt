package com.jkuhldev.openhand.data

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Model representing data for a single FileOptionsDialog ListItem
 * @property label Text label for the FileOptionsDialog ListItem
 * @property icon Icon image for the FileOptionsDialog ListItem
 * @property onClick Function executed when the FileOptionsDialog ListItem is clicked
 */
data class FileOptionsItemData(val label: String, val icon: ImageVector, val onClick: () -> Unit)
