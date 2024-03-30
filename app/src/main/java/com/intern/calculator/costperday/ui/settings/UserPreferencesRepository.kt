package com.intern.calculator.costperday.ui.settings

import android.util.Log
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.intern.calculator.costperday.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// Define the data class representing user preferences
data class UserPreferences(
    val theme: Theme,
    val language: Language,
    val selectedList: Int,
    val duration: Long,
    val amount: Double,
    val period: Int,
    val startDate: Long
)

// Enum representing different theme options
enum class Theme(@StringRes val title: Int) {
    Light(R.string.setting_theme_light),
    Dark(R.string.setting_theme_dark),
    System(R.string.setting_theme_system),
}

// Enum representing different language options
enum class Language {
    English,
    Russian,
}

// Repository class to manage user preferences stored in DataStore
class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    // Tag for logging purposes
    private val tag: String = "settings"

    // Keys used to store user preferences in DataStore
    private object PreferencesKeys {
        val THEME_KEY = stringPreferencesKey("theme")
        val LANGUAGE_KEY = stringPreferencesKey("language")
        val SELECTED_LIST_KEY = intPreferencesKey("selectedList")
        val DURATION_KEY = longPreferencesKey("duration")
        val AMOUNT_KEY = doublePreferencesKey("amount")
        val PERIOD_KEY = intPreferencesKey("period")
        val START_DATE_KEY = longPreferencesKey("startDate")
    }

    // Flow to observe user preferences changes
    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(tag, "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            mapUserPreferences(preferences)
        }

    // Function to update the theme preference
    suspend fun updateTheme(theme: String) {
        updatePreferences { preferences ->
            preferences[PreferencesKeys.THEME_KEY] = theme
        }
    }

    // Function to update the language preference
    suspend fun updateLanguage(language: String) {
        updatePreferences { preferences ->
            preferences[PreferencesKeys.LANGUAGE_KEY] = language
        }
    }

    // Function to update the selected list preference
    suspend fun updateSelectedList(listNumber: Int) {
        updatePreferences { preferences ->
            preferences[PreferencesKeys.SELECTED_LIST_KEY] = listNumber
        }
    }

    // Function to update the duration preference
    suspend fun updateDuration(duration: Long) {
        updatePreferences { preferences ->
            preferences[PreferencesKeys.DURATION_KEY] = duration
        }
    }

    // Function to update the amount preference
    suspend fun updateAmount(amount: Double) {
        updatePreferences { preferences ->
            preferences[PreferencesKeys.AMOUNT_KEY] = amount
        }
    }

    // Function to update the period preference
    suspend fun updatePeriod(period: Int) {
        updatePreferences { preferences ->
            preferences[PreferencesKeys.PERIOD_KEY] = period
        }
    }

    // Function to update the startDate preference
    suspend fun updateStartDate(date: Long) {
        updatePreferences { preferences ->
            preferences[PreferencesKeys.START_DATE_KEY] = date
        }
    }

    // Function to update preferences in the DataStore
    private suspend fun updatePreferences(update: suspend (MutablePreferences) -> Unit) {
        dataStore.edit { preferences ->
            update(preferences)
        }
    }

    // Function to map preferences stored in DataStore to UserPreferences object
    private fun mapUserPreferences(preferences: Preferences): UserPreferences {
        val theme = preferences[PreferencesKeys.THEME_KEY]?.let { Theme.valueOf(it) } ?: Theme.System
        val language = preferences[PreferencesKeys.LANGUAGE_KEY]?.let { Language.valueOf(it) }
            ?: toLanguage(Locale.getDefault().language)
        val selectedList = preferences[PreferencesKeys.SELECTED_LIST_KEY] ?: 1
        val duration = preferences[PreferencesKeys.DURATION_KEY] ?: 4000L
        val amount = preferences[PreferencesKeys.AMOUNT_KEY] ?: 15453.0
        val period = preferences[PreferencesKeys.PERIOD_KEY] ?: 30
        var startDate = preferences[PreferencesKeys.START_DATE_KEY] ?: 0L
        if (startDate == 0L) {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            startDate = calendar.timeInMillis
        }

        // Set application locale based on selected language
        val locale = when (language) {
            Language.English -> "en"
            Language.Russian -> "ru"
        }
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(locale))

        return UserPreferences(
            theme = theme,
            language = language,
            selectedList = selectedList,
            duration = duration,
            amount = amount,
            period = period,
            startDate = startDate
        )
    }

    // Function to convert locale string to Language enum
    private fun toLanguage(locale: String): Language {
        return when (locale) {
            "en" -> Language.English
            "ru" -> Language.Russian
            else -> Language.English
        }
    }
}
