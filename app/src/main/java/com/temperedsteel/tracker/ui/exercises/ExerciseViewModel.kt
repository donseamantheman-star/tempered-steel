package com.temperedsteel.tracker.ui.exercises

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.temperedsteel.tracker.backup.BackupManager
import com.temperedsteel.tracker.data.Exercise
import com.temperedsteel.tracker.repository.WorkoutRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ExerciseViewModel(
    private val repository: WorkoutRepository,
    private val backupManager: BackupManager
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    @OptIn(ExperimentalCoroutinesApi::class)
    val exercises: StateFlow<List<Exercise>> = _searchQuery
        .flatMapLatest { q -> if (q.isBlank()) repository.getAllExercises() else repository.searchExercises(q) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentExercises: StateFlow<List<Exercise>> = repository.getCurrentExercises()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage

    fun onSearchChange(q: String) { _searchQuery.value = q }
    fun openAddDialog()  { _showAddDialog.value = true }
    fun closeAddDialog() { _showAddDialog.value = false }

    fun addExercise(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch { repository.addExercise(name); _showAddDialog.value = false }
    }

    fun deleteExercise(exercise: Exercise) {
        viewModelScope.launch { repository.deleteExercise(exercise) }
    }

    fun toggleCurrent(exercise: Exercise) {
        viewModelScope.launch { repository.toggleCurrent(exercise) }
    }

    fun exportData(uri: Uri) {
        viewModelScope.launch {
            val exercises = repository.getAllExercises().first()
            val entries   = repository.getAllEntries().first()
            val ok = backupManager.export(uri, exercises, entries)
            _statusMessage.value = if (ok) "Export successful! ${entries.size} entries saved." else "Export failed."
        }
    }

    fun importData(uri: Uri) {
        viewModelScope.launch {
            val backup = backupManager.import(uri)
            if (backup == null) {
                _statusMessage.value = "Import failed — invalid file."
                return@launch
            }
            repository.restoreFromBackup(backup)
            _statusMessage.value = "Import successful! ${backup.exercises.size} exercises, ${backup.entries.size} entries restored."
        }
    }

    fun clearStatus() { _statusMessage.value = null }
}
