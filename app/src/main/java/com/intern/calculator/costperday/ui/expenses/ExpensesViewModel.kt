package com.intern.calculator.costperday.ui.expenses

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intern.calculator.costperday.data.classes.Item
import com.intern.calculator.costperday.data.repository.online.ItemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat

class ExpensesViewModel(
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
    fun updateItemList() {
        viewModelScope.launch {
            itemRepository.getAllItemsStream().collect { newList ->
                // Update the HomeUiState with the new list of items
                val newUiState = ItemUiState(newList)
                _itemUiState.value = newUiState
            }
        }
    }

    // Function to get the list of categories
    @Composable
    fun getItemList(): List<Item> {
        return itemUiState.collectAsState().value.itemList
    }

    // Function to create a new Item
    suspend fun createItem(Item: Item) {
        itemRepository.insertItem(Item)
    }

    // Function to update an existing Item
    suspend fun updateItem(Item: Item) {
        itemRepository.updateItem(Item)
    }

    // Function to delete a Item
    suspend fun deleteItem(Item: Item) {
        itemRepository.deleteItem(Item)
    }

    // Function to reset auto-increment for a given table
    suspend fun resetAutoIncrement(tableName: String?) {
        withContext(Dispatchers.IO) {
            itemRepository.resetAutoIncrement(tableName)
        }
    }

    // Companion object holding constant values
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

// Extension function to format the price of an item
fun Item.formatedPrice(): String {
    return NumberFormat.getCurrencyInstance().format(price)
}

// Data class representing the UI state for the home screen
data class ItemUiState(var itemList: List<Item> = listOf())