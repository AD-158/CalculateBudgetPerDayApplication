package com.intern.calculator.costperday.ui.components

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.intern.calculator.costperday.R
import java.util.Calendar

/**
 * Composable function to display a custom top app bar.
 * @param title The title text to be displayed.
 * @param modifier Modifier for styling.
 * @param navigationIcon The icon for the navigation button.
 * @param navigationIconContentDescription The content description for the navigation button.
 * @param actionIcon The icon for the action button.
 * @param actionIconContentDescription The content description for the action button.
 * @param onNavigationClick Callback function triggered when the navigation button is clicked.
 * @param onActionClick Callback function triggered when the action button is clicked.
 * @param colors Custom colors for the top app bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(
    title: String?,
    modifier: Modifier = Modifier,
    navigationIcon: ImageVector?,
    navigationIconContentDescription: String?,
    actionIcon: ImageVector?,
    actionIconContentDescription: String?,
    onNavigationClick: () -> Unit = {},
    onActionClick: () -> Unit = {},
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        navigationIconContentColor = MaterialTheme.colorScheme.primary,
        actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
    ),
) {
    // Determine the appropriate greeting based on the current time
    var greeting = 0
    if (title.isNullOrBlank())
    {
        // Getting the system time
        greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 4..11 -> R.string.main_appbar_greeting_1
            in 12..16 -> R.string.main_appbar_greeting_2
            in 17..23 -> R.string.main_appbar_greeting_3
            else -> R.string.main_appbar_greeting_4
        }
    }

    // Composable for the center-aligned top app bar
    CenterAlignedTopAppBar(
        // Display the title text or the greeting if the title is blank
        title = {
            Text(
                text = if (title.isNullOrBlank()) {
                    stringResource(greeting)
                } else {
                    title
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        // Display the navigation icon button if provided
        navigationIcon = {
            if (navigationIcon != null) {
                IconButton(onClick = onNavigationClick) {
                    Icon(
                        imageVector = navigationIcon,
                        contentDescription = navigationIconContentDescription,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
        // Display the action icon button if provided
        actions = {
            if (actionIcon != null) {
                IconButton(onClick = onActionClick) {
                    Icon(
                        imageVector = actionIcon,
                        contentDescription = actionIconContentDescription,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
        modifier = modifier,
        // Apply custom colors for the top app bar
        colors = colors,
    )
}