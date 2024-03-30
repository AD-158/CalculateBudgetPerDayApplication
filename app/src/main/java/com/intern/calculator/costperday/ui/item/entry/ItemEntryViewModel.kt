package com.intern.calculator.costperday.ui.item.entry

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.intern.calculator.costperday.data.classes.Item
import com.intern.calculator.costperday.data.repository.online.ItemRepository
import java.text.NumberFormat
import java.util.Date

// ViewModel responsible for handling the logic related to adding new items
class ItemEntryViewModel(
    private val itemRepository: ItemRepository,
) : ViewModel() {
    // Mutable state to hold the UI state of the item entry screen
    var itemUiState by mutableStateOf(ItemUiState())
        private set

    // Update the UI state based on the entered item details
    fun updateUiState(itemDetails: ItemDetails) {
        itemUiState =
            ItemUiState(itemDetails = itemDetails, isEntryValid = validateInput(itemDetails))
    }

    // Save the item to the repository if input is valid
    suspend fun saveItem() {
        if (validateInput()) {
            itemRepository.insertItem(itemUiState.itemDetails.toItem())
        }
    }

    // Function to delete the item
    suspend fun deleteItem() {
        itemRepository.deleteItem(itemUiState.itemDetails.toItem())
    }

    // Validate the input fields
    private fun validateInput(uiState: ItemDetails = itemUiState.itemDetails): Boolean {
        return with(uiState) {
//            name.isNotBlank() &&
            price.isNotBlank()
        }
    }
}

// UI state for the item entry screen
data class ItemUiState(
    val itemDetails: ItemDetails = ItemDetails(),
    val isEntryValid: Boolean = false
)

// Data class representing the details of an item
data class ItemDetails(
    val id: Int = 0,
    val name: String = "",
    val price: String = "",
    val date: Long = 0L
)

// Extension function to convert ItemDetails to Item
fun ItemDetails.toItem(): Item = Item(
    id = id,
    name = name,
    price = price.replace(",",".").toDoubleOrNull() ?: 0.0,
    date = date
)

// Extension function to format the price of an item
fun Item.formatedPrice(): String {
    return NumberFormat.getCurrencyInstance().format(price)
}

// Extension function to convert Item to ItemUiState
fun Item.toItemUiState(isEntryValid: Boolean = false): ItemUiState = ItemUiState(
    itemDetails = this.toItemDetails(),
    isEntryValid = isEntryValid
)

// Extension function to convert Item to ItemDetails
fun Item.toItemDetails(): ItemDetails = ItemDetails(
    id = id,
    name = name,
    price = price.toString(),
    date = date
)