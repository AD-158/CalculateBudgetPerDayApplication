package com.intern.calculator.costperday.data.repository.online

import com.intern.calculator.costperday.data.classes.Item
import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Item] from a given data source.
 */
interface ItemRepository {
    /**
     * Retrieve all the items from the the given data source.
     */
    fun getAllItemsStream(): Flow<List<Item>>

    /**
     * Retrieve all items without today items from the local database.
     */
    fun getAllItemsWithoutToday(date: Long): Flow<List<Item>>

    /**
     * Retrieve all today items from the local database.
     */
    fun getAllTodayItems(date: Long): Flow<List<Item>>

    /**
     * Retrieve an item from the given data source that matches with the [id].
     */
    fun getItemStream(id: Int): Flow<Item?>

    /**
     * Insert item in the data source
     */
    suspend fun insertItem(item: Item)

    /**
     * Delete item from the data source
     */
    suspend fun deleteItem(item: Item)

    /**
     * Update item in the data source
     */
    suspend fun updateItem(item: Item)

    /**
     * Reset index of a table
     */
    suspend fun resetAutoIncrement(tableName: String?)
}