package com.intern.calculator.costperday.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.intern.calculator.costperday.R
import com.intern.calculator.costperday.ui.expenses.ExpensesDestination
import com.intern.calculator.costperday.ui.expenses.ExpensesScreen
import com.intern.calculator.costperday.ui.home.HomeDestination
import com.intern.calculator.costperday.ui.home.HomeScreen
import com.intern.calculator.costperday.ui.item.edit.ItemEditDestination
import com.intern.calculator.costperday.ui.item.edit.ItemEditScreen
import com.intern.calculator.costperday.ui.item.entry.ItemEntryDestination
import com.intern.calculator.costperday.ui.item.entry.ItemEntryScreen
import com.intern.calculator.costperday.ui.settings.AboutDestination
import com.intern.calculator.costperday.ui.settings.AboutScreen
import com.intern.calculator.costperday.ui.settings.SettingsDestination
import com.intern.calculator.costperday.ui.settings.SettingsScreen

// Composable function to define the navigation host for the app
@Composable
fun CostPerDayNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    // Define the navigation graph using NavHost composable
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        // Define individual destinations with associated composable screens
        composable(
            route = HomeDestination.route,
        ) {
            HomeScreen(
                navigateToItemEntry = {
                    // Navigate to ItemEntryDestination
                    navController.navigate(ItemEntryDestination.route)
                },
                navigateToHistory = {
                    // Navigate to ItemDetailsDestination with itemId as argument
                    navController.navigate(ExpensesDestination.route)
                },
                navigateToSettings = {
                    // Navigate to SettingsDestination
                    navController.navigate(SettingsDestination.route)
                },
            )
        }
        // ItemEntryDestination with itemId as argument
        composable(
            route = ItemEntryDestination.route,
        ) {
            ItemEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                buttonText = R.string.item_entry_save_button_text,
            )
        }
        // ExpensesDestination
        composable(
            route = ExpensesDestination.route,
        ) {
            ExpensesScreen(
                onNavigateUp = { navController.navigateUp() },
                navigateToItemUpdate = {
                    // Navigate to ItemDetailsDestination with itemId as argument
                    navController.navigate("${ItemEditDestination.route}/${it}")
                },
                navigateToSettings = {
                    // Navigate to SettingsDestination
                    navController.navigate(SettingsDestination.route)
                },
            )
        }
        // ItemEditDestination with itemId as argument
        composable(
            route = ItemEditDestination.routeWithArgs,
            arguments = listOf(navArgument(ItemEditDestination.itemIdArg) {
                type = NavType.IntType // Define argument type
            })
        ) {
            ItemEditScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
            )
        }
        // SettingsDestination
        composable(route = SettingsDestination.route) {
            SettingsScreen(
                navigateUp = { navController.navigateUp() },
                navigateToAbout = { navController.navigate(AboutDestination.route) },
            )
        }
        // AboutDestination
        composable(route = AboutDestination.route) {
            AboutScreen(
                navigateUp = { navController.navigateUp() },
            )
        }
    }
}
