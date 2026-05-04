package com.temperedsteel.tracker.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.temperedsteel.tracker.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onExerciseTap: (Long) -> Unit, viewModel: HomeViewModel) {
    val items by viewModel.exercisesWithProgress.collectAsState()

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("TEMPERED STEEL", style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 3.sp, color = Accent, fontSize = 11.sp))
                        Text("Training Log", style = MaterialTheme.typography.titleLarge, color = OnSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        }
    ) { padding ->
        if (items.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No exercises yet.\nAdd some from the Exercises tab.", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceDim, textAlign = TextAlign.Center)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items, key = { it.exercise.id }) { item ->
                    ExerciseCard(item = item, onClick = { onExerciseTap(item.exercise.id) })
                }
            }
        }
    }
}

@Composable
fun ExerciseCard(item: ExerciseWithProgress, onClick: () -> Unit) {
    val trendColor = if (item.trend == Trend.UP_WEIGHT || item.trend == Trend.UP_REPS) ProgressGreen else OnSurfaceDim
    val trendLabel = when (item.trend) {
        Trend.UP_WEIGHT -> "↑ weight"
        Trend.UP_REPS   -> "+1 rep"
        Trend.SAME      -> "—"
        Trend.FIRST     -> ""
    }
    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
            .background(Surface).clickable(onClick = onClick).padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.exercise.name, style = MaterialTheme.typography.titleMedium, color = OnSurface)
                Spacer(Modifier.height(4.dp))
                if (item.latestEntry != null) {
                    val w = item.latestEntry.weightKg
                    val wStr = if (w % 1 == 0f) w.toInt().toString() else w.toString()
                    Text("${wStr}kg × ${item.latestEntry.reps}", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceDim)
                } else {
                    Text("No entries yet — tap to log", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceFaint)
                }
            }
            if (trendLabel.isNotEmpty()) {
                Spacer(Modifier.width(12.dp))
                Text(trendLabel, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = trendColor)
            }
        }
    }
}
