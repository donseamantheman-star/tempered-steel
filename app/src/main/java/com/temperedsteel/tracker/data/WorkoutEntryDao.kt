package com.temperedsteel.tracker.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutEntryDao {

    @Query("SELECT * FROM workout_entries WHERE exerciseId = :exerciseId ORDER BY timestamp DESC")
    fun getEntriesForExercise(exerciseId: Long): Flow<List<WorkoutEntry>>

    @Query("SELECT * FROM workout_entries WHERE exerciseId = :exerciseId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestEntry(exerciseId: Long): WorkoutEntry?

    @Query("SELECT * FROM workout_entries WHERE exerciseId = :exerciseId ORDER BY timestamp DESC LIMIT 2")
    suspend fun getLastTwoEntries(exerciseId: Long): List<WorkoutEntry>

    @Query("SELECT * FROM workout_entries ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<WorkoutEntry>>

    @Insert
    suspend fun insertEntry(entry: WorkoutEntry): Long

    @Delete
    suspend fun deleteEntry(entry: WorkoutEntry)

    @Update
    suspend fun updateEntry(entry: WorkoutEntry)
}
