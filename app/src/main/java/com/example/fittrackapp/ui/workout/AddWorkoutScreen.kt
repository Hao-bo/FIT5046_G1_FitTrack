package com.example.fittrackapp.ui.workout

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fittrackapp.ui.workout.WorkoutViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Composable function for the "Add Workout" screen.
 * Allows users to input details about their workout session.
 *
 * @param viewModel The ViewModel responsible for handling workout data logic.
 * @param navController The NavController used for navigation actions.
 */
@Composable
fun AddWorkoutScreen(
    viewModel: WorkoutViewModel,
    navController: NavController,
//    onSaveWorkout: (timestamp: Long, duration: Int, activityType: String, notes: String?) -> Unit
) {
    // Get the current context for displaying Toast and Dialogs
    val context = LocalContext.current

    // State variables manage user input
    var selectedDateTimeMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var durationMinutes by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    // workout type list
    val activityTypes = listOf("Running", "Yoga", "HIIT", "Swimming", "Cycing", "Strength Training", "Other")
    var selectedActivityType by remember { mutableStateOf(activityTypes[0]) }
    var activityTypeDropdownExpanded by remember { mutableStateOf(false) }

    // Formatting Date and Time
    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    // Display the selected date and time
    val selectedDateText = remember(selectedDateTimeMillis) {
        dateFormatter.format(Date(selectedDateTimeMillis))
    }
    val selectedTimeText = remember(selectedDateTimeMillis) {
        timeFormatter.format(Date(selectedDateTimeMillis))
    }

    // Calendar use for instance DatePickerDialog and TimePickerDialog
    val calendar = Calendar.getInstance()

    // Date picker
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            selectedDateTimeMillis = calendar.timeInMillis // update time stamp
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // Time picker
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            selectedDateTimeMillis = calendar.timeInMillis
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true // true indicates 24-hour format
    )


    // Root container for the screen content.
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Row for the close button, aligned to the end (right side).
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .systemBarsPadding(),
                horizontalArrangement = Arrangement.End
            ){

                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Return Icon",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            navController.popBackStack()
                        }
                )
            }

            // pick date and time
            Text("Sport date and time:", style = MaterialTheme.typography.titleMedium)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = { datePickerDialog.show() }) {
                    Icon(Icons.Filled.CalendarToday, contentDescription = "select date")
                    Spacer(Modifier.width(8.dp))
                    Text(selectedDateText)
                }
                OutlinedButton(onClick = { timePickerDialog.show() }) {
                    Text("⏱️")
                    Spacer(Modifier.width(8.dp))
                    Text(selectedTimeText)
                }
            }

            // Choose workout type
            Text("Type of workout:", style = MaterialTheme.typography.titleMedium)
            Box {
                OutlinedTextField(
                    value = selectedActivityType,
                    onValueChange = { /* Editing is not allowed */ },
                    readOnly = true,
                    label = { Text("Select the type of exercise") },
                    trailingIcon = {
                        Icon(
                            Icons.Filled.ArrowDropDown,
                            contentDescription = "Expand the list",
                            Modifier.clickable { activityTypeDropdownExpanded = true }
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Filled.FitnessCenter, contentDescription = "Sport icons")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                DropdownMenu(
                    expanded = activityTypeDropdownExpanded,
                    onDismissRequest = { activityTypeDropdownExpanded = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    activityTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                selectedActivityType = type
                                activityTypeDropdownExpanded = false
                            }
                        )
                    }
                }
            }


            // Duration Time
            Text("duration time (min):", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = durationMinutes,
                onValueChange = { durationMinutes = it.filter { char -> char.isDigit() } }, // only allow digits
                label = { Text("Example: 30 min") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Filled.Timer, contentDescription = "Time Icon")
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Note
            Text("Note (optional):", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("How do you feel today?") },
                leadingIcon = {
                    Icon(Icons.AutoMirrored.Filled.Notes, contentDescription = "Note Icon")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.weight(1f)) // push button to the bottom

            // Save button
            Button(
                onClick = {
                    val durationInt = durationMinutes.toIntOrNull()
                    if (durationInt == null || durationInt <= 0) {
                        Toast.makeText(context, "Please enter a valid duration (minutes)", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.saveWorkout(
                            selectedDateTimeMillis,
                            durationInt,
                            selectedActivityType,
                            notes.ifBlank { null }
                        )
                        Toast.makeText(context, "Workout record saved!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Upload my workout record")
            }
        }

    }
}


