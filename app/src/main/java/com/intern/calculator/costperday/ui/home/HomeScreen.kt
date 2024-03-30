package com.intern.calculator.costperday.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// Destination for the home screen
object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

// Composable function for the HomeScreen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToItemEntry: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToHistory: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    // Remember coroutine scope for launching coroutines
    val coroutineScope = rememberCoroutineScope()
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
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
    var diffDateInDays by remember { mutableLongStateOf(0L) }
    LaunchedEffect(userPreferences.startDate) {
        diffDateInDays = (calendar.timeInMillis - userPreferences.startDate) / (1000L * 60 * 60 * 24)
    }

    val spentWithout2d = viewModel.itemUiStateWithoutToday.collectAsState().value.itemList.sumOf { it.price }
    val allSpent = viewModel.itemUiState.collectAsState().value.itemList.sumOf { it.price }

    var spentToday by remember { mutableDoubleStateOf(0.0) }
    LaunchedEffect(allSpent) {
        spentToday = allSpent - spentWithout2d
    }
    val sumForToday =
        if ((((userPreferences.amount-spentWithout2d)/(userPreferences.period-diffDateInDays))-spentToday) <= 0.0)
            0.0
        else
            (((userPreferences.amount-spentWithout2d)/(userPreferences.period-diffDateInDays))-spentToday)
    val sumForTomorrow = (userPreferences.amount - allSpent) / (userPreferences.period-diffDateInDays - 1)
    var spentMoney by remember { mutableStateOf("") }

    val openDialog = remember { mutableStateOf(false) }
    val showDatePickerDialog = remember { mutableStateOf(false) }
    val newAmount = remember { mutableStateOf(userPreferences.amount.toString()) }
    val newDate = remember { mutableLongStateOf(userPreferences.startDate + (1000L * 60 * 60 * 24 * userPreferences.period)) }
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {  },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                        coroutineScope.launch {
                            settingsViewModel.updateAmount(newAmount.value.toDouble())
                            settingsViewModel.updateStartDate(calendar.timeInMillis)
                            settingsViewModel.updatePeriod(((newDate.longValue - calendar.timeInMillis) / (1000 * 60 * 60 * 24)).toInt())
                        }
                    },
                ) {
                    Text(stringResource(id = R.string.nav_drawer_modal_action_1_approve))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                        newAmount.value = userPreferences.amount.toString()
                    }
                ) {
                    Text(stringResource(id = R.string.nav_drawer_modal_action_cancel_text))
                }
            },
            text = {
                Column {
                    Row {
                        OutlinedTextField(
                            value = newAmount.value,
                            onValueChange = { newAmount.value = it},
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            label = { Text(stringResource(R.string.home_change_budget)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    Row {
                        OutlinedTextField(
                            value = if (newDate.longValue <= calendar.timeInMillis)
                                        SimpleDateFormat("EE dd MMMM yyyy", Locale(if (userPreferences.language.toString() == "English") "en" else "ru")).format(Date(userPreferences.startDate + (1000L * 60 * 60 * 24 * userPreferences.period)))
                                    else
                                        SimpleDateFormat("EE dd MMMM yyyy", Locale(if (userPreferences.language.toString() == "English") "en" else "ru")).format(Date(newDate.longValue)),
                            onValueChange = {
                            },
                            label = { Text(stringResource(R.string.home_period)) },
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
                                    contentDescription = "",
                                    modifier = Modifier.clickable {
                                        showDatePickerDialog.value = true
                                    }
                                )
                            },
                        )
                    }
                }
            }
        )
    }
    if (showDatePickerDialog.value) {
        newDate.longValue = userPreferences.startDate + (1000L * 60 * 60 * 24 * userPreferences.period)
        val dateState = rememberDatePickerState(
            newDate.longValue,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return (utcTimeMillis >= calendar.timeInMillis)
                }
            }
        )
        val confirmEnabled = remember {
            derivedStateOf { dateState.selectedDateMillis != null }
        }
        DatePickerDialog(
            onDismissRequest = {
                showDatePickerDialog.value = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePickerDialog.value = false
                        newDate.longValue = dateState.selectedDateMillis ?: 0L
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

    // State for snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    MaterialTheme {
        Scaffold(
            topBar = {
                MyTopAppBar(
                    title = null,
                    navigationIcon = Icons.Outlined.MonetizationOn,
                    navigationIconContentDescription = "Navigation icon",
                    actionIcon = Icons.Outlined.Settings,
                    actionIconContentDescription = "Action icon",
                    onNavigationClick = navigateToHistory,
                    onActionClick = navigateToSettings,
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            floatingActionButton = {
//                FloatingActionButton(
//                    onClick = { navigateToItemEntry() },
//                    shape = MaterialTheme.shapes.medium,
//                    modifier = Modifier.padding(20.dp)
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Add,
//                        contentDescription = stringResource(R.string.item_entry_title)
//                    )
//                }
            },
        ) {innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(
                        state = rememberScrollState(),
                        enabled = true,
                    )
            ) {
                Row {
                    OutlinedCard(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                        ),
                        border = BorderStroke(1.dp, Color.Black),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 2.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (userPreferences.period.toLong() == diffDateInDays) {
                                    Text(
                                        text = stringResource(R.string.home_screen_title),
                                        modifier = Modifier,
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                }
                                Text(
                                    text = stringResource(R.string.home_title_second,
                                        (userPreferences.amount - allSpent).toBigDecimal()
                                            .setScale(2, RoundingMode.FLOOR)
                                    ),
                                    modifier = Modifier,
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                if (userPreferences.period.toLong() > diffDateInDays) {
                                    Text(
                                        text = stringResource(R.string.home_title_third, userPreferences.period - diffDateInDays)
                                            + if (userPreferences.period == 1)
                                                stringResource(R.string.home_title_day_1)
                                            else if (userPreferences.period < 5)
                                                stringResource(R.string.home_title_day_2)
                                            else
                                                stringResource(R.string.home_title_day_3),
                                        modifier = Modifier.padding(vertical = 2.dp),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                }
                            }
                            Column(
                                modifier = Modifier.padding(end = 8.dp),
                                horizontalAlignment = Alignment.End
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .clickable { }
                                        .then(modifier)
                                ) {
                                    FloatingActionButton(
                                        onClick = {
                                            openDialog.value = true
                                        },
                                        modifier = Modifier
                                            .padding(vertical = 2.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Edit,
                                            contentDescription = "navigationIconContentDescription",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                if (userPreferences.period.toLong() > diffDateInDays) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        OutlinedCard(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                            ),
                            border = BorderStroke(1.dp, Color.Black),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp),
                            onClick = { }
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    text = stringResource(
                                        R.string.home_screen_left_title,
                                        sumForToday.toBigDecimal().setScale(2, RoundingMode.FLOOR)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.titleLarge
                                )
//                            Text(
//                                text = if (sumForToday == 0.0) "Бюджет на сегодня закончился" else "Тратим сегодняшний бюджет",
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(vertical = 2.dp),
//                                textAlign = TextAlign.Center,
//                                style = MaterialTheme.typography.titleLarge
//                            )
                            }
                        }
                    }
                }
                if ((sumForToday == 0.0) && (userPreferences.period.toLong() > diffDateInDays)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        OutlinedCard(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                            ),
                            border = BorderStroke(2.dp, MaterialTheme.colorScheme.error),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp),
                            onClick = { }
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    text = stringResource(
                                        R.string.home_title_tomorrow, sumForTomorrow.toBigDecimal()
                                            .setScale(2, RoundingMode.FLOOR)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedTextField(
                        value = spentMoney.toString(),
                        readOnly = true,
                        onValueChange = {},
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.None
                        ),
                        modifier = Modifier.fillMaxWidth().padding(0.dp),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.titleLarge
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(3f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CalculatorButton(
                                symbol = "7",
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.primary)
                                    .aspectRatio(1f)
                                    .weight(1f),
                                onClick = { spentMoney += 7 }
                            )
                            CalculatorButton(
                                symbol = "8",
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.primary)
                                    .aspectRatio(1f)
                                    .weight(1f),
                                onClick = { spentMoney += 8 }
                            )
                            CalculatorButton(
                                symbol = "9",
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.primary)
                                    .aspectRatio(1f)
                                    .weight(1f),
                                onClick = { spentMoney += 9 }
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CalculatorButton(
                                symbol = "4",
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.primary)
                                    .aspectRatio(1f)
                                    .weight(1f),
                                onClick = { spentMoney += 4 }
                            )
                            CalculatorButton(
                                symbol = "5",
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.primary)
                                    .aspectRatio(1f)
                                    .weight(1f),
                                onClick = { spentMoney += 5 }
                            )
                            CalculatorButton(
                                symbol = "6",
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.primary)
                                    .aspectRatio(1f)
                                    .weight(1f),
                                onClick = { spentMoney += 6 }
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CalculatorButton(
                                symbol = "1",
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.primary)
                                    .aspectRatio(1f)
                                    .weight(1f),
                                onClick = { spentMoney += 1 }
                            )
                            CalculatorButton(
                                symbol = "2",
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.primary)
                                    .aspectRatio(1f)
                                    .weight(1f),
                                onClick = { spentMoney += 2 }
                            )
                            CalculatorButton(
                                symbol = "3",
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.primary)
                                    .aspectRatio(1f)
                                    .weight(1f),
                                onClick = { spentMoney += 3 }
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CalculatorButton(
                                symbol = "0",
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.primary)
                                    .aspectRatio(2f)
                                    .weight(2f),
                                onClick = { spentMoney += 0 }
                            )
                            CalculatorButton(
                                symbol = ",",
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.primary)
                                    .aspectRatio(1f)
                                    .weight(1f),
                                onClick = {
                                    if (spentMoney == "") {
                                        spentMoney += "0,"
                                    }
                                    else if (!spentMoney.contains(",")) {
                                        spentMoney += ","
                                    }
                                }
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(start = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.tertiary)
                                    .aspectRatio(1f),
                            ) {
                                IconButton(
                                    onClick = { spentMoney = spentMoney.dropLast(1) },
                                    enabled = spentMoney != "",
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .aspectRatio(1f),
                                    colors = IconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.tertiary,
                                        contentColor = MaterialTheme.colorScheme.onTertiary,
                                        disabledContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                        disabledContentColor = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Outlined.Backspace,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onTertiary
                                    )
                                }
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        viewModel.createItem(
                                            Item(
                                                name = "",
                                                price = spentMoney.toDouble(),
                                                date = calendar.timeInMillis
                                            )
                                        )
                                        spentMoney = ""
                                    }
                                },
                                enabled = spentMoney != "",
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .aspectRatio(0.3f),
                                colors = IconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor = MaterialTheme.colorScheme.onError,
                                    disabledContainerColor = MaterialTheme.colorScheme.errorContainer,
                                    disabledContentColor = MaterialTheme.colorScheme.onErrorContainer
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onError
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}