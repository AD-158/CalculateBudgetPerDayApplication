package com.intern.calculator.costperday.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.intern.calculator.costperday.CalculateCostPerDayApplication
import com.intern.calculator.costperday.CostPerDayApp
import com.intern.calculator.costperday.ui.expenses.ExpensesViewModel
import com.intern.calculator.costperday.ui.home.HomeViewModel
import com.intern.calculator.costperday.ui.item.edit.ItemEditViewModel
import com.intern.calculator.costperday.ui.item.entry.ItemEntryViewModel
import com.intern.calculator.costperday.ui.settings.SettingsViewModel

// Object to provide view model instances using ViewModelFactory
object AppViewModelProvider {
    // Factory instance for creating view models
    val Factory = viewModelFactory {
        // Initializer for HomeViewModel
        initializer {
            HomeViewModel(
                itemRepository = CalculateCostPerDayApplication().container.itemRepository,
            )
        }
        // Initializer for ExpensesViewModel
        initializer {
            ExpensesViewModel(
                itemRepository = CalculateCostPerDayApplication().container.itemRepository,
            )
        }
        // Initializer for ItemEntryViewModel
        initializer {
            ItemEntryViewModel(
                itemRepository = CalculateCostPerDayApplication().container.itemRepository,
            )
        }
        // Initializer for ItemEditViewModel
        initializer {
            ItemEditViewModel(
                savedStateHandle = this.createSavedStateHandle(),
                itemRepository = CalculateCostPerDayApplication().container.itemRepository,
            )
        }
        // Initializer for SettingsViewModel
        initializer {
            SettingsViewModel(
                repository = CalculateCostPerDayApplication().container.settingsRepository,
                itemRepository = CalculateCostPerDayApplication().container.itemRepository,
            )
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [CostPerDayApp].
 */
fun CreationExtras.CalculateCostPerDayApplication(): CalculateCostPerDayApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as CalculateCostPerDayApplication)