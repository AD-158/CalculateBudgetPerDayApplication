package com.intern.calculator.costperday.data

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.intern.calculator.costperday.data.repository.offline.OfflineItemRepository
import com.intern.calculator.costperday.data.repository.online.ItemRepository
import com.intern.calculator.costperday.ui.settings.UserPreferencesRepository

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val itemRepository: ItemRepository
    val settingsRepository: UserPreferencesRepository
}

/**
 * [AppContainer] implementation that provides instances of [OfflineItemRepository].
 */
class AppDataContainer(private val context: Context) : AppContainer {
    // DataStore for storing user preferences
    private val Context.dataStore by preferencesDataStore(
        name = "settings"
    )

    // Lazily initialize the repositories with database instances
    override val itemRepository: ItemRepository by lazy {
        OfflineItemRepository(CostPerDayDatabase.getDatabase(context).itemDao())
    }
    override val settingsRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(dataStore = context.dataStore)
    }
}