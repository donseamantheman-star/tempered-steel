package com.temperedsteel.tracker.backup

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.temperedsteel.tracker.data.Exercise
import com.temperedsteel.tracker.data.WorkoutEntry

class BackupManager(private val context: Context) {

    private val gson = Gson()

    data class ExerciseBackup(
        val name: String,
        val isCustom: Boolean,
        val sortOrder: Int,
        val isCurrent: Boolean
    )

    data class EntryBackup(
        val exerciseName: String,
        val weightKg: Float,
        val reps: Int,
        val timestamp: Long
    )

    data class BackupData(
        val version: Int = 1,
        val exportedAt: Long = System.currentTimeMillis(),
        val exercises: List<ExerciseBackup>,
        val entries: List<EntryBackup>
    )

    fun export(uri: Uri, exercises: List<Exercise>, entries: List<WorkoutEntry>): Boolean {
        return try {
            val nameMap = exercises.associateBy { it.id }
            val backup = BackupData(
                exercises = exercises.map {
                    ExerciseBackup(it.name, it.isCustom, it.sortOrder, it.isCurrent)
                },
                entries = entries.mapNotNull { e ->
                    val name = nameMap[e.exerciseId]?.name ?: return@mapNotNull null
                    EntryBackup(name, e.weightKg, e.reps, e.timestamp)
                }
            )
            context.contentResolver.openOutputStream(uri)?.use { out ->
                out.write(gson.toJson(backup).toByteArray())
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun import(uri: Uri): BackupData? {
        return try {
            val json = context.contentResolver.openInputStream(uri)
                ?.bufferedReader()?.readText() ?: return null
            gson.fromJson(json, BackupData::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
