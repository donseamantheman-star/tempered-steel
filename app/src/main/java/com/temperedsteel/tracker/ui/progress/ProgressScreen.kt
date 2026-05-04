package com.temperedsteel.tracker.ui.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(onBack: () -> Unit, viewModel: ProgressViewModel) {
    val exercise by viewModel.exercise.collectAsState()
    val entries  by viewModel.entriesWithTrend.collectAsState()

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(exercise?.name ?: "", style = MaterialTheme.typography.titleLarge, color = OnSurface)
                        Text("${entries.size} sessions", style = MaterialTheme.typography.bodySmall, color = OnSurfaceDim)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = OnSurface) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        }
    ) { padding ->
        if (entries.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No history yet.\nLog your first set.", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceDim, textAlign = TextAlign.Center)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(entries, key = { _, item -> item.entry.id }) { _, item ->
                    SwipeToDeleteEntry(item = item, onDelete = { viewModel.deleteEntry(item.entry) })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteEntry(item: EntryWithTrend, onDelete: () -> Unit) {
    val state = rememberSwipeToDismissBoxState(
        confirmValueChange = { if (it == SwipeToDismissBoxValue.EndToStart) { onDelete(); true } else false }
    )
    SwipeToDismissBox(
        state = state,
        backgroundContent = {
            Box(Modifier.fillMaxSize().clip(RoundedCornerShape(14.dp)).background(ProgressRed.copy(alpha = 0.15f)), contentAlignment = Alignment.CenterEnd) {
                Text("Delete", modifier = Modifier.padding(end = 20.dp), color = ProgressRed, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
            }
        },
        content = { EntryCard(item) },
        enableDismissFromStartToEnd = false
    )
}

@Composable
fun EntryCard(item: EntryWithTrend) {
    val w      = item.entry.weightKg
    val wStr   = if (w % 1 == 0f) w.toInt().toString() else w.toString()
    val vol    = w * item.entry.reps
    val volStr = if (vol % 1 == 0f) vol.toInt().toString() else String.format("%.1f", vol)
    val date   = SimpleDateFormat("EEE d MMM", Locale.getDefault()).format(Date(item.entry.timestamp))

    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Surface).padding(horizontal = 18.dp, vertical = 14.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("${wStr}kg × ${item.entry.reps}", style = MaterialTheme.typography.titleMedium, color = OnSurface)
                Spacer(Modifier.height(3.dp))
                Text("$date  ·  ${volStr}kg vol.", style = MaterialTheme.typography.bodySmall, color = OnSurfaceDim)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (item.isWeightPR) EntryBadge("↑ weight", ProgressGreen)
                if (item.isRepPR)    EntryBadge("+rep", ProgressAmber)
            }
        }
    }
}

@Composable
fun EntryBadge(text: String, color: androidx.compose.ui.graphics.Color) {
    Box(Modifier.clip(RoundedCornerShape(6.dp)).background(color.copy(alpha = 0.15f)).padding(horizontal = 8.dp, vertical = 3.dp)) {
        Text(text, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 11.sp), color = color)
    }
}
