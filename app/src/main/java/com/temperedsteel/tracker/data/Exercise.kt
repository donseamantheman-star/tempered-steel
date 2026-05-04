package com.temperedsteel.tracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val isCustom: Boolean = false,
    val sortOrder: Int = 0,
    val isCurrent: Boolean = false
)
