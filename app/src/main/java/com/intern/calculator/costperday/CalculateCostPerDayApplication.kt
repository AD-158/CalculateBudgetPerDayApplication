package com.intern.calculator.costperday

import android.app.Application
import com.intern.calculator.costperday.data.AppContainer
import com.intern.calculator.costperday.data.AppDataContainer

// Define the application class which extends Application
class CalculateCostPerDayApplication : Application() {
    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    // Override the onCreate method to initialize the AppContainer instance
    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}