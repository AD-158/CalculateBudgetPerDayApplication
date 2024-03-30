package com.intern.calculator.costperday.ui.item.edit

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.intern.calculator.costperday.R
import com.intern.calculator.costperday.ui.AppViewModelProvider
import com.intern.calculator.costperday.ui.components.MyTopAppBar
import com.intern.calculator.costperday.ui.item.entry.ItemEntryBody
import com.intern.calculator.costperday.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

// Destination for the item edit screen
object ItemEditDestination : NavigationDestination {
    override val route = "item_edit"
    override val titleRes = R.string.edit_item
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

// Composable function for the item edit screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemEditScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ItemEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    // Remember coroutine scope for launching coroutines
    val coroutineScope = rememberCoroutineScope()
    // Retrieve the application context
    val context = LocalContext.current
    // The undo dialog is opened/closed
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            MyTopAppBar(
                title = stringResource(ItemEditDestination.titleRes),
                navigationIcon = Icons.AutoMirrored.Outlined.ArrowBack,
                navigationIconContentDescription = "Navigate back",
                actionIcon = null,
                actionIconContentDescription = null,
                onNavigationClick = onNavigateUp
            )
        },
        modifier = modifier
    ) { innerPadding ->
        // Column layout for the item entry body
        ItemEntryBody(
            itemUiState = viewModel.itemUiState,
            onItemValueChange = viewModel::updateUiState,
            onSaveClick = {
                // Note: If the user rotates the screen very fast, the operation may get cancelled
                // and the item may not be updated in the Database. This is because when config
                // change occurs, the Activity will be recreated and the rememberCoroutineScope will
                // be cancelled - since the scope is bound to composition.

                // Launch a coroutine for updating item
                coroutineScope.launch {
                    // Update the item
                    viewModel.updateItem()
                    // Navigate back
                    navigateBack()
                }
            },
            onDelete = {
                // Note: If the user rotates the screen very fast, the operation may get cancelled
                // and the item may not be deleted from the Database. This is because when config
                // change occurs, the Activity will be recreated and the rememberCoroutineScope will
                // be cancelled - since the scope is bound to composition.

                // Launch coroutine to delete item
                coroutineScope.launch {
                    viewModel.deleteItem()
                    navigateBack()
                }
            },
            modifier = Modifier.padding(innerPadding),
            buttonText = R.string.nav_drawer_modal_action_1_approve,
            snackbarHostState = snackbarHostState,
        )
    }
}