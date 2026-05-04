package com.temperedsteel.tracker.ui.log

import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.temperedsteel.tracker.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogScreen(onBack: () -> Unit, viewModel: LogViewModel) {
    val exercise  by viewModel.exercise.collectAsState()
    val lastEntry by viewModel.lastEntry.collectAsState()
    val weight    by viewModel.weightInput.collectAsState()
    val reps      by viewModel.repsInput.collectAsState()
    val saved     by viewModel.saved.collectAsState()
    val context   = LocalContext.current
    val view      = LocalView.current
    val repsFocus = remember { FocusRequester() }

    LaunchedEffect(saved) {
        if (saved) {
            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
            Toast.makeText(context, "Saved ✓", Toast.LENGTH_SHORT).show()
            onBack()
        }
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text(exercise?.name ?: "", style = MaterialTheme.typography.titleLarge, color = OnSurface) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = OnSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))
            Text(
                text = if (lastEntry != null) {
                    val w = lastEntry!!.weightKg
                    "Last: ${if (w % 1 == 0f) w.toInt() else w}kg × ${lastEntry!!.reps}"
                } else "First session — set your baseline",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceDim
            )
            Spacer(Modifier.height(40.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("WEIGHT", style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 2.sp, fontSize = 10.sp, color = Accent))
                    Spacer(Modifier.height(8.dp))
                    NumberField(weight, viewModel::onWeightChange, "kg", ImeAction.Next, onNext = { repsFocus.requestFocus() })
                }
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("REPS", style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 2.sp, fontSize = 10.sp, color = Accent))
                    Spacer(Modifier.height(8.dp))
                    NumberField(reps, viewModel::onRepsChange, "reps", ImeAction.Done, onDone = { viewModel.save() }, modifier = Modifier.focusRequester(repsFocus))
                }
            }
            Spacer(Modifier.height(48.dp))
            Button(
                onClick = { viewModel.save() },
                modifier = Modifier.fillMaxWidth().height(58.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Background, disabledContainerColor = OnSurfaceFaint),
                enabled = weight.isNotEmpty() && reps.isNotEmpty()
            ) {
                Text("SAVE SET", style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 2.sp, fontSize = 14.sp))
            }
        }
    }
}

@Composable
fun NumberField(
    value: String, onValueChange: (String) -> Unit, suffix: String,
    imeAction: ImeAction, onNext: (() -> Unit)? = null, onDone: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Surface).padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = value, onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.displayLarge.copy(textAlign = TextAlign.Center),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = imeAction),
            keyboardActions = KeyboardActions(onNext = { onNext?.invoke() }, onDone = { onDone?.invoke() }),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Accent, unfocusedBorderColor = OnSurfaceFaint,
                focusedTextColor = OnSurface, unfocusedTextColor = OnSurface,
                cursorColor = Accent, focusedContainerColor = Surface, unfocusedContainerColor = Surface
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
        )
        Spacer(Modifier.height(6.dp))
        Text(suffix, style = MaterialTheme.typography.bodySmall, color = OnSurfaceDim)
    }
}
