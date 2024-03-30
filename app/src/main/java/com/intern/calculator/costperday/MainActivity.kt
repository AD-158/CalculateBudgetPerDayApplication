package com.intern.calculator.costperday

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.intern.calculator.costperday.ui.theme.CostPerDayTheme

// Define the main activity class which extends AppCompatActivity for changing language
class MainActivity : AppCompatActivity() {
    // Override the onCreate method to initialize the activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the content of the activity using Jetpack Compose
        setContent {
            // Apply the theme defined in CostPerDayTheme
            CostPerDayTheme {
                // Set up a Surface composable as the root with MaterialTheme colors
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Display the main content of the app
                    CostPerDayApp()
                }
            }
        }
    }
}