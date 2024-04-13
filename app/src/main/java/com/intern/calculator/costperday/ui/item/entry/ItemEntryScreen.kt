package com.intern.calculator.costperday.ui.item.entry

import android.annotation.SuppressLint
import android.icu.util.Currency
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.intern.calculator.costperday.R
import com.intern.calculator.costperday.data.classes.Item
import com.intern.calculator.costperday.ui.AppViewModelProvider
import com.intern.calculator.costperday.ui.components.CustomDialog
import com.intern.calculator.costperday.ui.components.MyTopAppBar
import com.intern.calculator.costperday.ui.navigation.NavigationDestination
import com.intern.calculator.costperday.ui.settings.SettingsViewModel
import com.intern.calculator.costperday.ui.settings.Theme
import com.intern.calculator.costperday.ui.settings.UserPreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ItemEntryDestination : NavigationDestination {
    override val route = "item_entry"
    override val titleRes = R.string.item_entry_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: ItemEntryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    buttonText: Int,
) {
    // Remember coroutine scope for launching coroutines
    val coroutineScope = rememberCoroutineScope()
    // The undo dialog is opened/closed
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            MyTopAppBar(
                title = stringResource(ItemEntryDestination.titleRes),
                navigationIcon = Icons.AutoMirrored.Outlined.ArrowBack,
                navigationIconContentDescription = "Navigate back",
                actionIcon = null,
                actionIconContentDescription = null,
                onNavigationClick = onNavigateUp
            )
        }
    ) { innerPadding ->
        // Display the item entry body
        ItemEntryBody(
            itemUiState = viewModel.itemUiState,
            onItemValueChange = viewModel::updateUiState,
            onSaveClick = {
                // Coroutine launched to update UI state and save item
                coroutineScope.launch {
                    viewModel.saveItem()
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
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth(),
            buttonText = buttonText,
            snackbarHostState = snackbarHostState,
        )
    }
}

@Composable
fun ItemEntryBody(
    itemUiState: ItemUiState,
    onItemValueChange: (ItemDetails) -> Unit,
    onSaveClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    buttonText: Int,
    snackbarHostState: SnackbarHostState,
) {
    // State for delete confirmation
    var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }
    // State for whether delete confirmation dialog is open
    val openDialogCustom = remember { mutableStateOf(true) }
    // Column for displaying item input form and save button
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier.padding(16.dp)
    ) {
        // Item input form
        ItemInputForm(
            itemDetails = itemUiState.itemDetails,
            onValueChange = onItemValueChange,
            modifier = Modifier.fillMaxWidth(),
        )
        // Save button
        Button(
            onClick = onSaveClick,
            enabled = itemUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(buttonText))
        }
        // Button to delete item
        OutlinedButton(
            onClick = {
                deleteConfirmationRequired = true
                openDialogCustom.value = true
            },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(R.string.delete_item),
            )
            Text(stringResource(R.string.delete_item))
        }
        // Show delete confirmation dialog if required
        if (deleteConfirmationRequired) {
            DeleteConfirmationDialog(
                onDeleteConfirm = {
                    deleteConfirmationRequired = false
                    onDelete()
                },
                onDeleteCancel = { deleteConfirmationRequired = false },
                modifier = Modifier.padding(16.dp),
                item = itemUiState.itemDetails.toItem(),
                openDialogCustom = openDialogCustom,
                snackbarHostState = snackbarHostState
            )
        }
    }
}

@SuppressLint("SimpleDateFormat")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemInputForm(
    itemDetails: ItemDetails,
    modifier: Modifier = Modifier,
    onValueChange: (ItemDetails) -> Unit = {},
    enabled: Boolean = true,
    settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val showDatePickerDialog = remember { mutableStateOf(false) }
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
    if (showDatePickerDialog.value) {
        val dateState = rememberDatePickerState(
            itemDetails.date,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return ((utcTimeMillis >= userPreferences.startDate) && (utcTimeMillis <= (userPreferences.startDate + (1000L * 60 * 60 * 24 * userPreferences.period))))
                }
            }
        )
        val confirmEnabled = remember {
            derivedStateOf { dateState.selectedDateMillis != null }
        }
        DatePickerDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onDismissRequest.
                showDatePickerDialog.value = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePickerDialog.value = false
                        onValueChange(
                            itemDetails.copy(date = dateState.selectedDateMillis ?: 0L)
                        )
                    },
                    enabled = confirmEnabled.value
                ) {
                    Text(stringResource(id = R.string.nav_drawer_modal_action_1_approve))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePickerDialog.value = false
                    }
                ) {
                    Text(stringResource(id = R.string.nav_drawer_modal_action_cancel_text))
                }
            }
        ) {
            DatePicker(state = dateState)
        }
    }

    // Column for displaying item input fields
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Row for item name input field
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            // Outlined text field for item date
            OutlinedTextField(
                value = itemDetails.name,
                onValueChange = {
                    onValueChange(
                        itemDetails.copy(name = it)
                    )
                },
                label = { Text(stringResource(R.string.item_entry_name_placeholder)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
                singleLine = true
            )
        }
        // Row for item date input field
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            OutlinedTextField(
                value = SimpleDateFormat("EE dd MMMM yyyy", Locale(if (userPreferences.language.toString() == "English") "en" else "ru")).format(Date(itemDetails.date)),
                onValueChange = {
                    onValueChange(
                        itemDetails.copy(name = it)
                    )
                },
                label = { Text(stringResource(R.string.item_entry_date_placeholder)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                singleLine = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = stringResource(id = R.string.item_entry_date_placeholder),
                        modifier = Modifier.clickable {
                            showDatePickerDialog.value = true
                        }
                    )
                },
            )
        }
        // Row for item price input field
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            // Outlined text field for item price
            OutlinedTextField(
                value = itemDetails.price,
                onValueChange = { onValueChange(itemDetails.copy(price = it)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                label = { Text(stringResource(R.string.item_entry_price_placeholder)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                leadingIcon = {
                    Text(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            Currency.getInstance(Locale.getDefault())?.symbol ?: "₽"
                        else
                            "₽"
                    ) },
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
                singleLine = true
            )
        }
        // Text indicating required fields
        if (enabled) {
            Text(
                text = stringResource(R.string.required_fields),
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

// Composable function to display delete confirmation dialog
@Composable
private fun DeleteConfirmationDialog(
    item: Item,
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    openDialogCustom: MutableState<Boolean>,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    // Remember coroutine scope for launching coroutines
    val coroutineScope = rememberCoroutineScope()
    // Retrieve the application context
    val context = LocalContext.current
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
    )
    if (openDialogCustom.value) {
        CustomDialog(
            oldValue = item.name,
            neededAction = 2,
            onConfirmation = {
                openDialogCustom.value = false
                coroutineScope.launch {
                    launch {
                        delay(userPreferences.duration)
                        snackbarHostState.currentSnackbarData?.dismiss()
                    }
                    val result = snackbarHostState
                        .showSnackbar(
                            message = context.getString(R.string.snackbar_text_action_2),
                            actionLabel = context.getString(R.string.nav_drawer_modal_action_cancel_text),
                            duration = SnackbarDuration.Indefinite
                        )
                    when (result) {
                        SnackbarResult.ActionPerformed -> {
                            /* Handle snackbar approved */
                            onDeleteCancel()
                        }

                        SnackbarResult.Dismissed -> {
                            /* Handle snackbar dismissed */
                            onDeleteConfirm()
                        }
                    }
                }
            },
            onCancel = {
                openDialogCustom.value = false
                onDeleteCancel()
            },
        )
    }
}
