package com.intern.calculator.costperday.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowRightAlt
import androidx.compose.material.icons.automirrored.outlined.Message
import androidx.compose.material.icons.outlined.ArrowCircleDown
import androidx.compose.material.icons.outlined.ArrowCircleUp
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Timelapse
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.intern.calculator.costperday.R
import com.intern.calculator.costperday.data.classes.Item
import com.intern.calculator.costperday.ui.AppViewModelProvider
import com.intern.calculator.costperday.ui.components.MyTopAppBar
import com.intern.calculator.costperday.ui.navigation.NavigationDestination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


// Define the destination for the settings screen
object SettingsDestination : NavigationDestination {
    override val route = "settings"
    override val titleRes = R.string.setting_title
}

// Composable function for the SettingsScreen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navigateUp: () -> Unit,
                   navigateToAbout: () -> Unit,
                   viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    // Observe user preferences from the view model
    val preferences by viewModel.userPreferences.collectAsState(
        initial = UserPreferences(
            Theme.System,
            viewModel.toLanguage(Locale.getDefault().language),
            1,
            4000L,
            15453.0,
            30,
            0L,
            0L,
            0.0)
    )
    // Remember coroutine scope for launching coroutines
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val items by viewModel.items.collectAsState(initial = emptyList())

    // Mutable state for showing theme, language, duration and others dialogs
    val showThemeDialog = remember { mutableStateOf(false) }
    val showLanguageDialog = remember { mutableStateOf(false) }
    val showDurationDialog = remember { mutableStateOf(false) }

    // Theme dialog
    if (showThemeDialog.value) {
        ThemeDialog(showThemeDialog, preferences, coroutineScope, viewModel)
    }

    // Language dialog
    if (showLanguageDialog.value) {
        LanguageDialog(showLanguageDialog, preferences, coroutineScope, viewModel)
    }

    // Duration dialog
    if (showDurationDialog.value) {
        DurationDialog(showDurationDialog, preferences, coroutineScope, viewModel)
    }

    Scaffold(
        topBar = {
            MyTopAppBar(
                title = stringResource(SettingsDestination.titleRes),
                navigationIcon = Icons.AutoMirrored.Outlined.ArrowBack,
                navigationIconContentDescription = "Navigate back",
                actionIcon = null,
                actionIconContentDescription = null,
                onNavigationClick = navigateUp
            )
        }
    ) { padding ->
        // Retrieve the application context
        Column(
            Modifier
                .padding(padding)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            ) {
                // List items for different settings options
                // Each item consists of a headline, supporting content, and optional trailing content
                // Each item is clickable and performs a specific action
                HorizontalDivider()
                ListItem(
                    headlineContent = {
                        Row {
                            Icon(
                                imageVector = Icons.Outlined.DarkMode,
                                contentDescription = stringResource(id = R.string.setting_theme),
                                modifier = Modifier.padding(end = 2.dp)
                            )
                            Text(text = stringResource(id = R.string.setting_theme))
                        }
                    },
                    supportingContent = {Text(text = stringResource(id = preferences.theme.title))},
                    modifier = Modifier.clickable {
                        showThemeDialog.value = true
                    },
                )
                HorizontalDivider()
                ListItem(
                    headlineContent = {
                        Row {
                            Icon(
                                imageVector = Icons.Outlined.Translate,
                                contentDescription = stringResource(id = R.string.setting_language),
                                modifier = Modifier.padding(end = 2.dp)
                            )
                            Text(text = stringResource(id = R.string.setting_language))
                        }
                    },
                    supportingContent = {Text(text = preferences.language.name)},
                    modifier = Modifier.clickable {
                        showLanguageDialog.value = true
                    },
                )
                HorizontalDivider()
                ListItem(
                    headlineContent = {
                        Row {
                            Icon(
                                imageVector = Icons.Outlined.Timelapse,
                                contentDescription = stringResource(id = R.string.setting_duration),
                                modifier = Modifier.padding(end = 2.dp)
                            )
                            Text(text = stringResource(id = R.string.setting_duration))
                        }
                    },
                    supportingContent = {
                        Text(
                            text = (preferences.duration/1000).toString() +
                                    stringResource(id = R.string.setting_duration_value))
                    },
                    modifier = Modifier.clickable {
                        showDurationDialog.value = true
                    },
                )
                HorizontalDivider()
                ListItem(
                    headlineContent = {
                        Row {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Message,
                                contentDescription = stringResource(id = R.string.setting_send_feedback),
                                modifier = Modifier.padding(end = 2.dp)
                            )
                            Text(text = stringResource(id = R.string.setting_send_feedback))
                        }
                    },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowRightAlt,
                            contentDescription = stringResource(id = R.string.setting_send_feedback),
                            modifier = Modifier.padding(end = 2.dp)
                        )
                    },
                    modifier = Modifier.clickable {
                        val email = "mcrn158@gmail.com"
                        val subject = context.getString(R.string.setting_mail_subject)
                        val message = context.getString(R.string.setting_mail_message)

                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:$email")
                            putExtra(Intent.EXTRA_SUBJECT, subject)
                            putExtra(Intent.EXTRA_TEXT, message)
                        }
                            context.startActivity(intent)

                    },
                )
                HorizontalDivider()
                ListItem(
                    headlineContent = {
                        Row {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = stringResource(id = R.string.setting_about_app),
                                modifier = Modifier.padding(end = 2.dp)
                            )
                            Text(text = stringResource(id = R.string.setting_about_app))
                        }
                    },
                    modifier = Modifier.clickable {
                        navigateToAbout()
                    },
                )
            }
        }
    }
}

@Composable
private fun ThemeDialog(
    showThemeDialog: MutableState<Boolean>,
    preferences: UserPreferences,
    coroutineScope: CoroutineScope,
    viewModel: SettingsViewModel
) {
    AlertDialog(
        onDismissRequest = { showThemeDialog.value = false },
        title = { Text(text = stringResource(id = R.string.setting_theme)) },
        text = {
            // Box to hold the column
            Box(modifier = Modifier) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .selectableGroup()
                        .padding(all = 0.dp),
                ) {
                    // Radio buttons for selecting theme options
                    // Each row represents a theme option
                    Row(
                        Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = preferences.theme == Theme.System,
                            onClick = {
                                coroutineScope.launch {
                                    viewModel.updateTheme(Theme.System)
                                }
                            },
                            modifier = Modifier.semantics {
                                contentDescription = "Localized Description"
                            }
                        )
                        Text(
                            text = stringResource(id = R.string.setting_theme_system),
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Row(
                        Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = preferences.theme == Theme.Light,
                            onClick = {
                                coroutineScope.launch {
                                    viewModel.updateTheme(Theme.Light)
                                }
                            },
                            modifier = Modifier.semantics {
                                contentDescription = "Localized Description"
                            }
                        )
                        Text(
                            text = stringResource(id = R.string.setting_theme_light),
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Row(
                        Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = preferences.theme == Theme.Dark,
                            onClick = {
                                coroutineScope.launch {
                                    viewModel.updateTheme(Theme.Dark)
                                }
                            },
                            modifier = Modifier.semantics {
                                contentDescription = "Localized Description"
                            }
                        )
                        Text(
                            text = stringResource(id = R.string.setting_theme_dark),
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(
                onClick = {
                    showThemeDialog.value = false
                }
            ) {
                Text(stringResource(id = R.string.nav_drawer_modal_action_cancel_text))
            }
        },
    )
}

@Composable
private fun LanguageDialog(
    showLanguageDialog: MutableState<Boolean>,
    preferences: UserPreferences,
    coroutineScope: CoroutineScope,
    viewModel: SettingsViewModel
) {
    AlertDialog(
        onDismissRequest = { showLanguageDialog.value = false },
        title = { Text(text = stringResource(id = R.string.setting_language)) },
        text = {
            // Box to hold the column
            Box(modifier = Modifier) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .selectableGroup()
                        .padding(all = 0.dp),
                ) {
                    // Radio buttons for selecting language options
                    // Each row represents a language option
                    Row(
                        Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = preferences.language == Language.English,
                            onClick = {
                                coroutineScope.launch {
                                    viewModel.updateLanguage(Language.English)
                                }
                            },
                            modifier = Modifier.semantics {
                                contentDescription = "Localized Description"
                            }
                        )
                        Text(
                            text = stringResource(id = R.string.setting_language_english),
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Row(
                        Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = preferences.language == Language.Russian,
                            onClick = {
                                coroutineScope.launch {
                                    viewModel.updateLanguage(Language.Russian)
                                }
                            },
                            modifier = Modifier.semantics {
                                contentDescription = "Localized Description"
                            }
                        )
                        Text(
                            text = stringResource(id = R.string.setting_language_russian),
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(
                onClick = {
                    showLanguageDialog.value = false
                }
            ) {
                Text(stringResource(id = R.string.nav_drawer_modal_action_cancel_text))
            }
        },
    )
}

@Composable
private fun DurationDialog(
    showDurationDialog: MutableState<Boolean>,
    preferences: UserPreferences,
    coroutineScope: CoroutineScope,
    viewModel: SettingsViewModel
) {
    var sliderPosition by remember {
        mutableFloatStateOf(
            if (preferences.duration > 1000)
                (preferences.duration / 1000).toFloat()
            else preferences.duration.toFloat()
        )
    }
    AlertDialog(
        onDismissRequest = { showDurationDialog.value = false },
        title = {
            Text(
                text = stringResource(id = R.string.setting_duration_title),
                textAlign = TextAlign.Center
            )
        },
        text = {
            // Box to hold the column
            Box(modifier = Modifier) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(all = 0.dp),
                ) {
                    Slider(
                        value = sliderPosition,
                        onValueChange = {
                            sliderPosition = it
                            coroutineScope.launch {
                                viewModel.updateDuration((it*1000).toLong())
                            }
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.secondary,
                            activeTrackColor = MaterialTheme.colorScheme.secondary,
                            inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                        ),
                        steps = 19,
                        valueRange = 0f..20f
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                        )
                        Text(
                            text = (sliderPosition.toInt()).toString(),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    showDurationDialog.value = false
                }
            ) {
                Text(stringResource(id = R.string.nav_drawer_modal_action_1_approve))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    sliderPosition = 4f
                    coroutineScope.launch {
                        viewModel.updateDuration(4000L)
                    }
                }
            ) {
                Text(stringResource(id = R.string.setting_duration_reset))
            }
        },
    )
}