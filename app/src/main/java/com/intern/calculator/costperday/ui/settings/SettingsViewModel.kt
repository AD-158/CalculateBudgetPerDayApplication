package com.intern.calculator.costperday.ui.settings

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intern.calculator.costperday.data.classes.Item
import com.intern.calculator.costperday.data.repository.online.ItemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject


// Define the SettingsViewModel class which extends ViewModel
class SettingsViewModel(
    private val repository: UserPreferencesRepository,
    val itemRepository: ItemRepository,
) : ViewModel() {

    // Flow to observe user preferences
    val userPreferences: Flow<UserPreferences> = repository.userPreferencesFlow
    val items: Flow<List<Item>> = itemRepository.getAllItemsStream()

    // Function to update the theme
    suspend fun updateTheme(theme: Theme) {
        repository.updateTheme(theme.name)
    }

    // Function to update the language
    suspend fun updateLanguage(language: Language) {
        repository.updateLanguage(language.name)

        // Set application locale based on selected language
        val locale = when (language) {
            Language.English -> "en"
            Language.Russian -> "ru"
        }
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(locale))
    }

    // Function to convert locale string to Language enum
    fun toLanguage(locale: String): Language {
        return when (locale) {
            "en" -> Language.English
            "ru" -> Language.Russian
            else -> Language.English
        }
    }

    // Function to update the duration
    suspend fun updateDuration(duration: Long) {
        repository.updateDuration(duration)
    }

    // Function to update the amount preference
    suspend fun updateAmount(amount: Double) {
        repository.updateAmount(amount)
    }

    // Function to update the period preference
    suspend fun updatePeriod(period: Int) {
        repository.updatePeriod(period)
    }

    // Function to update the startDate preference
    suspend fun updateStartDate(date: Long) {
        repository.updateStartDate(date)
    }

    // Function to update the lastDate preference
    suspend fun updateLastDate(date: Long) {
        repository.updateLastDate(date)
    }

    // Function to update the amount preference
    suspend fun updateRemainingBudget(amount: Double) {
        repository.updateRemainingBudget(amount)
    }
}
