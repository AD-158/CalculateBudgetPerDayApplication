package com.intern.calculator.costperday.data.repository.offline

import androidx.room.Query
import com.intern.calculator.costperday.data.classes.Item
import com.intern.calculator.costperday.data.dao.ItemDAO
import com.intern.calculator.costperday.data.repository.online.ItemRepository
import kotlinx.coroutines.flow.Flow

class OfflineItemRepository(private val itemDao: ItemDAO) : ItemRepository {
    // Get a flow of all items from the local database
    override fun getAllItemsStream(): Flow<List<Item>> = itemDao.getAllItems()

    // Get a flow of all items without today items from the local database
    override fun getAllItemsWithoutToday(date: Long): Flow<List<Item>> = itemDao.getAllItemsWithoutToday(date)

    // Get a flow of all today items from the local database
    override fun getAllTodayItems(date: Long): Flow<List<Item>> = itemDao.getAllItemsWithoutToday(date)

    // Get a flow of a specific item by its ID from the local database
    override fun getItemStream(id: Int): Flow<Item?> = itemDao.getItem(id)

    // Insert a new item into the local database
    override suspend fun insertItem(item: Item) = itemDao.insert(item)

    // Delete an item from the local database
    override suspend fun deleteItem(item: Item) = itemDao.delete(item)

    // Update an existing item in the local database
    override suspend fun updateItem(item: Item) = itemDao.update(item)

    // Reset the auto-increment value for the given table in the local database
    override suspend fun resetAutoIncrement(tableName: String?) = itemDao.resetAutoIncrement(tableName)

}