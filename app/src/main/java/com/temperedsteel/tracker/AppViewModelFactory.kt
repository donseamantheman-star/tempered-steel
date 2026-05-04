package com.temperedsteel.tracker

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.temperedsteel.tracker.backup.BackupManager
import com.temperedsteel.tracker.data.AppDatabase
import com.temperedsteel.tracker.repository.WorkoutRepository
import com.temperedsteel.tracker.ui.exercises.ExerciseViewModel
import com.temperedsteel.tracker.ui.home.HomeViewModel
import com.temperedsteel.tracker.ui.log.LogViewModel
import com.temperedsteel.tracker.ui.progress.ProgressViewModel

object AppViewModelFactory {

    private fun repo(context: Context): WorkoutRepository {
        val db = AppDatabase.getInstance(context.applicationContext)
        return WorkoutRepository(db.exerciseDao(), db.workoutEntryDao())
    }

    fun home(context: Context): ViewModelProvider.Factory = viewModelFactory {
        initializer { HomeViewModel(repo(context)) }
    }

    fun exercise(context: Context): ViewModelProvider.Factory = viewModelFactory {
        initializer { ExerciseViewModel(repo(context), BackupManager(context.applicationContext)) }
    }

    fun log(context: Context, exerciseId: Long): ViewModelProvider.Factory = viewModelFactory {
        initializer { LogViewModel(repo(context), exerciseId) }
    }

    fun progress(context: Context, exerciseId: Long): ViewModelProvider.Factory = viewModelFactory {
        initializer { ProgressViewModel(repo(context), exerciseId) }
    }
}
