package com.temperedsteel.tracker.ui.exercises

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.temperedsteel.tracker.data.Exercise
import com.temperedsteel.tracker.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseScreen(viewModel: ExerciseViewModel, onExerciseTap: (Long) -> Unit) {
    val exercises      by viewModel.exercises.collectAsState()
    val currentList    by viewModel.currentExercises.collectAsState()
    val query          by viewModel.searchQuery.collectAsState()
    val showDialog     by viewModel.showAddDialog.collectAsState()
    val statusMessage  by viewModel.statusMessage.collectAsState()
    val context        = LocalContext.current
    var selectedTab    by remember { mutableIntStateOf(0) }

    // File pickers
    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        uri?.let { viewModel.exportData(it) }
    }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { viewModel.importData(it) }
    }

    // Show status toast
    LaunchedEffect(statusMessage) {
        statusMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearStatus()
        }
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("Exercises", style = MaterialTheme.typography.titleLarge, color = OnSurface) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background),
                actions = {
                    IconButton(onClick = { exportLauncher.launch("tempered_steel_backup.json") }) {
                        Icon(Icons.Default.Upload, "Export", tint = OnSurfaceDim)
                    }
                    IconButton(onClick = { importLauncher.launch(arrayOf("application/json", "*/*")) }) {
                        Icon(Icons.Default.Download, "Import", tint = OnSurfaceDim)
                    }
                    IconButton(onClick = viewModel::openAddDialog) {
                        Icon(Icons.Default.Add, "Add", tint = Accent)
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor   = Background,
                contentColor     = Accent,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Accent
                    )
                }
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 },
                    text = { Text("All Exercises", color = if (selectedTab == 0) Accent else OnSurfaceDim) })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 },
                    text = { Text("Current (${currentList.size})", color = if (selectedTab == 1) Accent else OnSurfaceDim) })
            }

            when (selectedTab) {
                0 -> AllExercisesTab(exercises, query, viewModel)
                1 -> CurrentExercisesTab(currentList, viewModel, onExerciseTap)
            }
        }
    }

    if (showDialog) {
        AddExerciseDialog(onDismiss = viewModel::closeAddDialog, onConfirm = viewModel::addExercise)
    }
}

@Composable
fun AllExercisesTab(exercises: List<Exercise>, query: String, viewModel: ExerciseViewModel) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = query, onValueChange = viewModel::onSearchChange,
            placeholder = { Text("Search exercises", color = OnSurfaceDim) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = OnSurfaceDim) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Accent, unfocusedBorderColor = OnSurfaceFaint,
                focusedTextColor = OnSurface, unfocusedTextColor = OnSurface,
                cursorColor = Accent, focusedContainerColor = Surface, unfocusedContainerColor = Surface
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp), contentPadding = PaddingValues(bottom = 100.dp)) {
            items(exercises, key = { it.id }) { ex ->
                ExerciseRow(exercise = ex, onToggleCurrent = { viewModel.toggleCurrent(ex) }, onDelete = { viewModel.deleteExercise(ex) })
            }
        }
    }
}

@Composable
fun CurrentExercisesTab(current: List<Exercise>, viewModel: ExerciseViewModel, onExerciseTap: (Long) -> Unit) {
    if (current.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                "No current exercises.\nTap the ★ on any exercise to add it here.",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceDim,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(current, key = { it.id }) { ex ->
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                        .background(Surface).clickable { onExerciseTap(ex.id) }.padding(horizontal = 18.dp, vertical = 16.dp)
                ) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(ex.name, style = MaterialTheme.typography.titleMedium, color = OnSurface, modifier = Modifier.weight(1f))
                        IconButton(onClick = { viewModel.toggleCurrent(ex) }, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.Star, "Unmark current", tint = StarYellow, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseRow(exercise: Exercise, onToggleCurrent: () -> Unit, onDelete: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Surface).padding(start = 16.dp, end = 4.dp, top = 4.dp, bottom = 4.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(exercise.name, style = MaterialTheme.typography.bodyMedium, color = OnSurface, modifier = Modifier.weight(1f))
            Row {
                IconButton(onClick = onToggleCurrent, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = if (exercise.isCurrent) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Toggle current",
                        tint = if (exercise.isCurrent) StarYellow else OnSurfaceDim,
                        modifier = Modifier.size(18.dp)
                    )
                }
                if (exercise.isCustom) {
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Delete, "Delete", tint = ProgressRed.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AddExerciseDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var text  by remember { mutableStateOf("") }
    val focus = remember { FocusRequester() }
    LaunchedEffect(Unit) { focus.requestFocus() }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = Surface,
        title = { Text("Add Exercise", style = MaterialTheme.typography.titleMedium, color = OnSurface) },
        text = {
            OutlinedTextField(
                value = text, onValueChange = { text = it },
                placeholder = { Text("Exercise name", color = OnSurfaceDim) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Accent, unfocusedBorderColor = OnSurfaceFaint,
                    focusedTextColor = OnSurface, unfocusedTextColor = OnSurface,
                    cursorColor = Accent, focusedContainerColor = SurfaceVariant, unfocusedContainerColor = SurfaceVariant
                ),
                modifier = Modifier.fillMaxWidth().focusRequester(focus)
            )
        },
        confirmButton = { TextButton(onClick = { onConfirm(text) }, enabled = text.isNotBlank()) { Text("Add", color = Accent) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = OnSurfaceDim) } }
    )
}

// Extension for tab indicator
@OptIn(ExperimentalMaterial3Api::class)
private fun Modifier.tabIndicatorOffset(tabPosition: TabPosition): Modifier =
    this.wrapContentSize(Alignment.BottomStart)
        .offset(x = tabPosition.left)
        .width(tabPosition.width)
