package com.temperedsteel.tracker.repository

import com.temperedsteel.tracker.backup.BackupManager
import com.temperedsteel.tracker.data.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class WorkoutRepository(
    private val exerciseDao: ExerciseDao,
    private val workoutEntryDao: WorkoutEntryDao
) {
    // ── Exercises ─────────────────────────────────────────────────────────────
    fun getAllExercises(): Flow<List<Exercise>> = exerciseDao.getAllExercises()
    fun getCurrentExercises(): Flow<List<Exercise>> = exerciseDao.getCurrentExercises()
    fun searchExercises(q: String): Flow<List<Exercise>> = exerciseDao.searchExercises(q)

    suspend fun addExercise(name: String): Long =
        exerciseDao.insertExercise(Exercise(name = name.trim(), isCustom = true))

    suspend fun deleteExercise(exercise: Exercise) = exerciseDao.deleteExercise(exercise)

    suspend fun toggleCurrent(exercise: Exercise) =
        exerciseDao.setCurrentFlag(exercise.id, !exercise.isCurrent)

    // ── Entries ───────────────────────────────────────────────────────────────
    fun getEntriesForExercise(id: Long): Flow<List<WorkoutEntry>> =
        workoutEntryDao.getEntriesForExercise(id)

    fun getAllEntries(): Flow<List<WorkoutEntry>> = workoutEntryDao.getAllEntries()

    suspend fun getLatestEntry(id: Long): WorkoutEntry? = workoutEntryDao.getLatestEntry(id)

    suspend fun getLastTwoEntries(id: Long): List<WorkoutEntry> =
        workoutEntryDao.getLastTwoEntries(id)

    suspend fun logEntry(exerciseId: Long, weightKg: Float, reps: Int): Long =
        workoutEntryDao.insertEntry(WorkoutEntry(exerciseId = exerciseId, weightKg = weightKg, reps = reps))

    suspend fun deleteEntry(entry: WorkoutEntry) = workoutEntryDao.deleteEntry(entry)

    // ── Backup ────────────────────────────────────────────────────────────────
    suspend fun restoreFromBackup(backup: BackupManager.BackupData) {
        // Insert exercises (ignore duplicates)
        backup.exercises.forEach { eb ->
            exerciseDao.insertExercise(
                Exercise(name = eb.name, isCustom = eb.isCustom, sortOrder = eb.sortOrder, isCurrent = eb.isCurrent)
            )
        }
        // Build name→id map
        val nameToId = exerciseDao.getAllExercises().first().associateBy({ it.name }, { it.id })
        // Insert entries
        backup.entries.forEach { eb ->
            val exId = nameToId[eb.exerciseName] ?: return@forEach
            workoutEntryDao.insertEntry(WorkoutEntry(exerciseId = exId, weightKg = eb.weightKg, reps = eb.reps, timestamp = eb.timestamp))
        }
    }
}
