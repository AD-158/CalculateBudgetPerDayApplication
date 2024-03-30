package com.intern.calculator.costperday

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.intern.calculator.costperday.ui.navigation.CostPerDayNavHost

/**
 * Top level composable that represents screens for the application.
 */
@Composable
fun CostPerDayApp(navController: NavHostController = rememberNavController()) {
    CostPerDayNavHost(navController = navController)
}