package com.intern.calculator.costperday.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intern.calculator.costperday.data.classes.Item
import com.intern.calculator.costperday.data.repository.online.ItemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import java.util.Calendar

class HomeViewModel(
    private val itemRepository: ItemRepository,
) : ViewModel() {
    // StateFlow for quantity unit UI state
    val itemUiState: StateFlow<ItemUiState> =
        itemRepository.getAllItemsStream().map { ItemUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ItemUiState()
            )

    val itemUiStateWithoutToday: StateFlow<ItemUiState> =
        itemRepository.getAllItemsWithoutToday(getTodayDate()).map { ItemUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ItemUiState()
            )

    val itemUiStateWithoutTwoDays: StateFlow<ItemUiState> =
        itemRepository.getAllItemsWithoutToday(getTodayDate()-1000L*60*60*24).map { ItemUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ItemUiState()
            )

    // Function to create a new Item
    suspend fun createItem(Item: Item) {
        itemRepository.insertItem(Item)
    }

    // Function to Delete all items with date less than chosen from a table
    suspend fun deleteItems(chosenDate: Long) {
        itemRepository.deleteAllRowsBeforeChosenDate(chosenDate)
    }

    // Function to reset auto-increment for a given table
    suspend fun resetAutoIncrement(tableName: String?) {
        withContext(Dispatchers.IO) {
            itemRepository.resetAutoIncrement(tableName)
        }
    }

    fun getTodayDate(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    // Companion object holding constant values
    companion object {
        const val TIMEOUT_MILLIS = 5_000L
    }
}

// Data class representing the UI state for the home screen
data class ItemUiState(var itemList: List<Item> = listOf())