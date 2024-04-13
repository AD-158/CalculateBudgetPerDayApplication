package com.intern.calculator.costperday.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.intern.calculator.costperday.data.classes.Item
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDAO {
    // Insert an item into the database
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(item: Item)

    // Update an item in the database
    @Update
    suspend fun update(item: Item)

    // Delete an item from the database
    @Delete
    suspend fun delete(item: Item)

    // Get all items from the database but without today items
    @Query("SELECT * from t_item WHERE t_item_date < :date")
    fun getAllItemsWithoutToday(date: Long): Flow<List<Item>>

    // Get all today items from the database
    @Query("SELECT * from t_item WHERE t_item_date = :date")
    fun getAllTodayItems(date: Long): Flow<List<Item>>

    // Get all items from the database
    @Query("SELECT * from t_item")
    fun getAllItems(): Flow<List<Item>>

    // Get an item from the database by its ID
    @Query("SELECT * from t_item WHERE t_item_id = :id")
    fun getItem(id: Int): Flow<Item>

    // Reset the auto-increment value for a given table
    @Query("UPDATE sqlite_sequence SET seq = 0 WHERE name = :tableName")
    fun resetAutoIncrement(tableName: String?)

    // Delete all items with date less than chosen from a table
    @Query("DELETE FROM t_item WHERE t_item_date < :chosenDate")
    fun deleteAllRowsBeforeChosenDate(chosenDate: Long)
}