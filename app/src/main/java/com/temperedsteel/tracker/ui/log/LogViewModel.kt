package com.temperedsteel.tracker.ui.log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.temperedsteel.tracker.data.Exercise
import com.temperedsteel.tracker.data.WorkoutEntry
import com.temperedsteel.tracker.repository.WorkoutRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LogViewModel(
    private val repository: WorkoutRepository,
    private val exerciseId: Long
) : ViewModel() {

    private val _exercise    = MutableStateFlow<Exercise?>(null)
    val exercise: StateFlow<Exercise?> = _exercise

    private val _lastEntry   = MutableStateFlow<WorkoutEntry?>(null)
    val lastEntry: StateFlow<WorkoutEntry?> = _lastEntry

    private val _weightInput = MutableStateFlow("")
    val weightInput: StateFlow<String> = _weightInput

    private val _repsInput   = MutableStateFlow("")
    val repsInput: StateFlow<String> = _repsInput

    private val _saved       = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved

    init {
        viewModelScope.launch {
            repository.getAllExercises().first().find { it.id == exerciseId }?.let { _exercise.value = it }
            repository.getLatestEntry(exerciseId)?.let { last ->
                _lastEntry.value = last
                val w = last.weightKg
                _weightInput.value = if (w % 1 == 0f) w.toInt().toString() else w.toString()
                _repsInput.value   = last.reps.toString()
            }
        }
    }

    fun onWeightChange(v: String) { if (v.matches(Regex("^\\d{0,4}(\\.\\d{0,2})?\$"))) _weightInput.value = v }
    fun onRepsChange(v: String)   { if (v.matches(Regex("^\\d{0,3}\$"))) _repsInput.value = v }

    fun save() {
        val weight = _weightInput.value.toFloatOrNull() ?: return
        val reps   = _repsInput.value.toIntOrNull()    ?: return
        if (weight <= 0f || reps <= 0) return
        viewModelScope.launch {
            repository.logEntry(exerciseId, weight, reps)
            _saved.value = true
        }
    }
}
