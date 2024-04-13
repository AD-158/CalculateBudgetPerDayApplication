package com.intern.calculator.costperday.ui.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intern.calculator.costperday.data.classes.Item
import com.intern.calculator.costperday.data.repository.online.ItemRepository
import com.intern.calculator.costperday.ui.home.ItemUiState
import com.intern.calculator.costperday.ui.item.entry.toItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.text.NumberFormat

class ExpensesViewModel(
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


    // Function to delete the item
    suspend fun deleteItem(item: Item) {
        itemRepository.deleteItem(item = item)
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