package com.temperedsteel.tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.temperedsteel.tracker.navigation.AppNavigation
import com.temperedsteel.tracker.ui.theme.TemperedSteelTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TemperedSteelTheme { AppNavigation() }
        }
    }
}
