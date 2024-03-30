package com.intern.calculator.costperday.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intern.calculator.costperday.data.classes.Item
import com.intern.calculator.costperday.data.repository.online.ItemRepository
import com.intern.calculator.costperday.ui.expenses.ItemUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class HomeViewModel(
    private val itemRepository: ItemRepository,
) : ViewModel() {
    // MutableStateFlow to hold the UI state
    private val _itemUiState = MutableStateFlow(ItemUiState())
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

    // Function to get the list of items
    @Composable
    fun getItemList(): List<Item> {
        return itemUiState.collectAsState().value.itemList
    }

    // Function to get the list of items without today items
    fun getAllItemsWithoutToday(date: Long): Flow<List<Item>> {
        return itemRepository.getAllItemsWithoutToday(date)
    }

    // Function to get the list of today items
    fun getAllTodayItems(date: Long): Flow<List<Item>> {
        return itemRepository.getAllTodayItems(date)
    }

    // Function to create a new Item
    suspend fun createItem(Item: Item) {
        itemRepository.insertItem(Item)
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