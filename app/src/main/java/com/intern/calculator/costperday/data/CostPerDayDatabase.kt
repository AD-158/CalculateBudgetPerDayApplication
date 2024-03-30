package com.intern.calculator.costperday.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.intern.calculator.costperday.data.classes.Converters
import com.intern.calculator.costperday.data.classes.Item
import com.intern.calculator.costperday.data.dao.ItemDAO

// Define the Room database with entities and version number
@Database(entities = [Item::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class CostPerDayDatabase : RoomDatabase() {
    // Define abstract functions to retrieve DAOs
    abstract fun itemDao(): ItemDAO

    companion object {
        @Volatile
        private var Instance: CostPerDayDatabase? = null

        // Get an instance of the database
        fun getDatabase(context: Context): CostPerDayDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, CostPerDayDatabase::class.java, "item_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
