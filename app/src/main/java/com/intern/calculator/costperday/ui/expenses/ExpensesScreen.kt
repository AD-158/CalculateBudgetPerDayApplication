package com.intern.calculator.costperday.ui.expenses

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.magnifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.intern.calculator.costperday.R
import com.intern.calculator.costperday.data.classes.Item
import com.intern.calculator.costperday.ui.AppViewModelProvider
import com.intern.calculator.costperday.ui.components.MyTopAppBar
import com.intern.calculator.costperday.ui.navigation.NavigationDestination
import com.intern.calculator.costperday.ui.settings.SettingsViewModel
import com.intern.calculator.costperday.ui.settings.Theme
import com.intern.calculator.costperday.ui.settings.UserPreferences
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Destination for the home screen
object ExpensesDestination : NavigationDestination {
    override val route = "expenses"
    override val titleRes = R.string.expenses_title
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(
    onNavigateUp: () -> Unit,
    navigateToItemUpdate: (Int) -> Unit,
    navigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExpensesViewModel = viewModel(factory = AppViewModelProvider.Factory),
    settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    // Remember coroutine scope for launching coroutines
    val coroutineScope = rememberCoroutineScope()
    // Retrieve the application context
    val context = LocalContext.current
    // State for snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    // Collect user preferences state
    val userPreferences by settingsViewModel.userPreferences.collectAsState(
        initial = UserPreferences(
            Theme.System,
            settingsViewModel.toLanguage(Locale.getDefault().language),
            1,
            4000L,
            15453.0,
            30,
            0L)
    )// Collect home UI state
    val itemUiState by viewModel.itemUiState.collectAsState()

    MaterialTheme {
        Scaffold(
            topBar = {
                MyTopAppBar(
                    title = context.getString(R.string.expenses_title),
                    navigationIcon = Icons.AutoMirrored.Outlined.ArrowBack,
                    navigationIconContentDescription = "Navigation icon",
                    actionIcon = Icons.Outlined.Settings,
                    actionIconContentDescription = "Action icon",
                    onNavigationClick = onNavigateUp,
                    onActionClick = navigateToSettings
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            floatingActionButton = {},
        ) { innerPadding ->
            // Body of the screen
            ExpensesBody(
                itemList = itemUiState.itemList,
                onItemClick = navigateToItemUpdate,
                modifier = modifier
                    .padding(innerPadding)
                    .padding(4.dp)
                    .fillMaxSize(),
                onClick = {
                }
            )
        }
    }
}

// Composable function for the body of the screen
@Composable
private fun ExpensesBody(
    itemList: List<Item>,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        // Display message if item list is empty, otherwise display item list
        if (itemList.isEmpty()) {
            Text(
                text = stringResource(R.string.no_item_description),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        } else {
            InventoryList(
                itemList = itemList,
                onItemClick = { onItemClick(it.id) },
                modifier = Modifier.padding(horizontal = 0.dp),
            )
        }
    }
}

// Composable function for displaying inventory list
@Composable
private fun InventoryList(
    itemList: List<Item>,
    onItemClick: (Item) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        items(items = itemList, key = { it.id }) { item ->
            InventoryItem(
                item = item,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { onItemClick(item) },
                containerColor = MaterialTheme.colorScheme.surface
            )
        }
    }
}

// Composable function for displaying inventory item
@SuppressLint("SimpleDateFormat")
@Composable
private fun InventoryItem(
    item: Item,
    modifier: Modifier = Modifier,
    containerColor: Color,
    settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val userPreferences by settingsViewModel.userPreferences.collectAsState(
        initial = UserPreferences(
            Theme.System,
            settingsViewModel.toLanguage(Locale.getDefault().language),
            1,
            4000L,
            15453.0,
            30,
            0L)
    )
    val pattern = SimpleDateFormat("EE dd MMMM yyyy", Locale(if (userPreferences.language.toString() == "English") "en" else "ru"))
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
        ),
    ) {
        Column(
            modifier = Modifier.padding(vertical = 20.dp, horizontal = 10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column (
                    modifier = Modifier.weight(3f)
                ) {
                    Text(
                        text = pattern.format(Date(item.date)).toString(),
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Column (
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = item.formatedPrice(),
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column (
                    modifier = Modifier.padding(0.dp),
                ) {
                    Text(
                        text = if (item.name == "") "Empty" else item.name,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}