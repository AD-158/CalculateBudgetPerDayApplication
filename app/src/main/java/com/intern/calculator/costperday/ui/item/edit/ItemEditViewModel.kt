package com.intern.calculator.costperday.ui.item.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intern.calculator.costperday.data.repository.online.ItemRepository
import com.intern.calculator.costperday.ui.item.entry.ItemDetails
import com.intern.calculator.costperday.ui.item.entry.ItemUiState
import com.intern.calculator.costperday.ui.item.entry.toItem
import com.intern.calculator.costperday.ui.item.entry.toItemUiState
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ItemEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemRepository: ItemRepository,
) : ViewModel() {

    // Mutable state for the UI state of the item being edited
    var itemUiState by mutableStateOf(ItemUiState())
        private set

    // ID of the item being edited
    private val itemId: Int = checkNotNull(savedStateHandle[ItemEditDestination.itemIdArg])

    init {
        // Fetch the item details and convert them to UI state when the ViewModel is initialized
        viewModelScope.launch {
            itemUiState = itemRepository.getItemStream(itemId)
                .filterNotNull()
                .first()
                .toItemUiState(true)
        }
    }

    // Function to update the item in the repository
    suspend fun updateItem() {
        if (validateInput(itemUiState.itemDetails)) {
            itemRepository.updateItem(itemUiState.itemDetails.toItem())
        }
    }

    // Function to update the UI state based on item details
    fun updateUiState(itemDetails: ItemDetails) {
        itemUiState =
            ItemUiState(itemDetails = itemDetails, isEntryValid = validateInput(itemDetails))
    }

    // Function to delete the item
    suspend fun deleteItem() {
        itemRepository.deleteItem(itemUiState.itemDetails.toItem())
    }

    // Function to validate input for the item details
    private fun validateInput(uiState: ItemDetails = itemUiState.itemDetails): Boolean {
        return with(uiState) {
//            name.isNotBlank() &&
                    price.isNotBlank()
        }
    }
}
