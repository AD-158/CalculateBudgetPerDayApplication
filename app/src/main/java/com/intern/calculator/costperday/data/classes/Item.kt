package com.intern.calculator.costperday.data.classes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "t_item",
)
data class Item(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "t_item_id") var id: Int = 0,
    @ColumnInfo(name = "t_item_name", defaultValue = "Товар") val name: String,
    @ColumnInfo(name = "t_item_price", defaultValue = "0.0") val price: Double,
    @ColumnInfo(name = "t_item_date") val date: Long,
)