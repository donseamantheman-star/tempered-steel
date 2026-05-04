package com.temperedsteel.tracker.ui.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.temperedsteel.tracker.data.Exercise
import com.temperedsteel.tracker.data.WorkoutEntry
import com.temperedsteel.tracker.repository.WorkoutRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class EntryWithTrend(val entry: WorkoutEntry, val isWeightPR: Boolean, val isRepPR: Boolean)

class ProgressViewModel(private val repository: WorkoutRepository, private val exerciseId: Long) : ViewModel() {

    private val _exercise = MutableStateFlow<Exercise?>(null)
    val exercise: StateFlow<Exercise?> = _exercise

    val entriesWithTrend: StateFlow<List<EntryWithTrend>> =
        repository.getEntriesForExercise(exerciseId)
            .map { entries ->
                entries.mapIndexed { i, entry ->
                    val prev = entries.getOrNull(i + 1)
                    EntryWithTrend(
                        entry      = entry,
                        isWeightPR = prev == null || entry.weightKg > prev.weightKg,
                        isRepPR    = prev != null && entry.reps > prev.reps
                    )
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.getAllExercises().first().find { it.id == exerciseId }?.let { _exercise.value = it }
        }
    }

    fun deleteEntry(entry: WorkoutEntry) { viewModelScope.launch { repository.deleteEntry(entry) } }
}
