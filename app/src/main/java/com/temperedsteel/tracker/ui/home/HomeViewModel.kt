package com.temperedsteel.tracker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.temperedsteel.tracker.data.Exercise
import com.temperedsteel.tracker.data.WorkoutEntry
import com.temperedsteel.tracker.repository.WorkoutRepository
import kotlinx.coroutines.flow.*

data class ExerciseWithProgress(
    val exercise: Exercise,
    val latestEntry: WorkoutEntry?,
    val previousEntry: WorkoutEntry?,
    val trend: Trend
)

enum class Trend { UP_WEIGHT, UP_REPS, SAME, FIRST }

class HomeViewModel(private val repository: WorkoutRepository) : ViewModel() {

    /**
     * BUG FIX: Previously used getLastTwoEntries() (suspend) inside collect{} which
     * only re-ran when the exercises list changed — NOT when entries changed.
     *
     * Fix: combine() exercises flow WITH entries flow. Any change to either
     * (new entry saved, entry deleted) triggers an immediate recomposition.
     */
    val exercisesWithProgress: StateFlow<List<ExerciseWithProgress>> =
        combine(
            repository.getAllExercises(),
            repository.getAllEntries()
        ) { exercises, allEntries ->
            exercises.map { exercise ->
                val sorted   = allEntries.filter { it.exerciseId == exercise.id }
                    .sortedByDescending { it.timestamp }
                val latest   = sorted.getOrNull(0)
                val previous = sorted.getOrNull(1)
                val trend = when {
                    latest == null                       -> Trend.FIRST
                    previous == null                     -> Trend.FIRST
                    latest.weightKg > previous.weightKg -> Trend.UP_WEIGHT
                    latest.reps > previous.reps         -> Trend.UP_REPS
                    else                                 -> Trend.SAME
                }
                ExerciseWithProgress(exercise, latest, previous, trend)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
