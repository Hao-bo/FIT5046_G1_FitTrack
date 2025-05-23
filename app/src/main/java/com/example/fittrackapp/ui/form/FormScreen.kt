package com.example.fittrackapp.ui.form

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import android.icu.util.Calendar
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fittrackapp.ui.form.FormViewModel
import com.example.fittrackapp.di.Graph
import com.example.fittrackapp.R
import com.example.fittrackapp.data.model.WorkoutSession
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class WorkoutData(val time: String, val category: String)

/**
 * Formats a timestamp (Long) into a date string (e.g., "yyyy-MM-dd").
 * @param timestamp The timestamp in milliseconds since epoch.
 * @return A formatted date string.
 */
// formatting timestamps
fun formatTimestampToDateString(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

/**
 * Formats a timestamp (Long) into a time string (e.g., "HH:mm").
 * @param timestamp The timestamp in milliseconds since epoch.
 * @return A formatted time string.
 */
fun formatTimestampToTimeString(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

/**
 * Composable function for the Form Screen.
 * This screen allows users to view their workout sessions filtered by date or by month.
 *
 * @param viewModel The [FormViewModel] instance used to fetch and manage workout data.
 * Defaults to the instance provided by [Graph.formViewModel].
 */
@Composable
fun FormScreen(
    viewModel: FormViewModel = Graph.formViewModel // get FormViewModel instance
) {
    // Collecting Workout Data from the ViewModel
    val workoutsForDate by viewModel.workoutsForSelectedDate.collectAsState()
    val workoutsForMonth by viewModel.workoutsForSelectedMonth.collectAsState()

    // Date picker state
    var showDatePicker by remember { mutableStateOf(false) }
    val currentCalendar = remember { Calendar.getInstance() } // Used to get the default year

    // Month and year selector states
    val months = remember { (1..12).map { SimpleDateFormat("MMM", Locale.getDefault()).format(Calendar.getInstance().apply { set(Calendar.MONTH, it - 1) }.time) } }
    var selectedMonthName by remember { mutableStateOf(months[currentCalendar.get(Calendar.MONTH)]) } // use current month as default
    var selectedYearForMonthView by remember { mutableIntStateOf(currentCalendar.get(Calendar.YEAR)) } // use current year as default
    var monthDropdownExpanded by remember { mutableStateOf(false) }


    Log.d("FormScreen", "FormScreen - workoutsForDate changed: ${workoutsForDate.size} items")
    workoutsForDate.forEach { workout ->
        Log.d("FormScreen", "FormScreen - date workout: id=${workout.id}, activity=${workout.activityType}, duration=${workout.durationMinutes}min")
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // View Section by Date
            Text("View Workouts by Date", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))
            DisplayDatePicker(
                showDialog = showDatePicker,
                onShowDialogChange = { showDatePicker = it },
                onDateSelected = { dateMillis ->
                    viewModel.loadWorkoutsForDate(dateMillis) // Call ViewModel to load data
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (workoutsForDate.isEmpty()) {
                Text("No workouts found for the selected date.", modifier = Modifier.padding(vertical = 16.dp))
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(workoutsForDate) { workout ->
                        WorkoutItemCard(workout = workout)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // View Section by Month
            Text("View Workouts by Month", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))
            MonthYearPicker(
                selectedMonthName = selectedMonthName,
                selectedYear = selectedYearForMonthView, // pass year parameter
                months = months,
                isExpanded = monthDropdownExpanded,
                onExpandedChange = { monthDropdownExpanded = it },
                onMonthSelected = { monthName ->
                    selectedMonthName = monthName
                    val monthIndex = months.indexOf(monthName)
                    if (monthIndex != -1) {
                        // Call ViewModel to load data (month is 1-12)
                        viewModel.loadWorkoutsForMonth(selectedYearForMonthView, monthIndex + 1)
                    }
                    monthDropdownExpanded = false
                },

            )
            Spacer(modifier = Modifier.height(8.dp))
            if (workoutsForMonth.isEmpty()) {
                Text("No workouts found for the selected month.", modifier = Modifier.padding(vertical = 16.dp))
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(workoutsForMonth) { workout ->
                        WorkoutItemCard(workout = workout)
                    }
                }
            }
        }
    }
}


/**
 * Composable function to display a single workout session in a Card.
 *
 * @param workout The [WorkoutSession] data to display.
 */
@Composable
fun WorkoutItemCard(workout: WorkoutSession) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = workout.activityType,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Date: ${formatTimestampToDateString(workout.timestamp)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Time: ${formatTimestampToTimeString(workout.timestamp)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Duration: ${workout.durationMinutes} min",
                style = MaterialTheme.typography.bodyMedium
            )
            if (!workout.notes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Notes: ${workout.notes}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}


/**
 * Composable function for a Date Picker TextField and Dialog.
 *
 * @param showDialog Boolean state to control the visibility of the DatePickerDialog.
 * @param onShowDialogChange Lambda to update the [showDialog] state.
 * @param onDateSelected Lambda callback that provides the selected date as a UTC millisecond timestamp.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayDatePicker(
    showDialog: Boolean,
    onShowDialogChange: (Boolean) -> Unit,
    onDateSelected: (Long) -> Unit // Callback selected date and time stamp (UTC milliseconds)
) {
    var selectedDateText by remember { mutableStateOf("Click to select a date") }
    val formatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    // initialization DatePickerState，
    val datePickerState = rememberDatePickerState()

    TextField(
        value = selectedDateText,
        onValueChange = {}, // Direct editing is not allowed
        readOnly = true,
        label = { Text("Search by date") },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onShowDialogChange(true) }, // Show dialog when clicking on text box
        trailingIcon = {
            Icon(
                painter = painterResource(R.drawable.calendar_icon),
                contentDescription = "Select Date",
                modifier = Modifier
                    .clickable { onShowDialogChange(true) } // Also show dialog when clicking icon
                    .size(24.dp)
            )
        }
    )

    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { onShowDialogChange(false) },
            confirmButton = {
                TextButton(onClick = {
                    onShowDialogChange(false)
                    datePickerState.selectedDateMillis?.let { millis ->
                        onDateSelected(millis) // Call the callback, passing the selected timestamp
                        selectedDateText = formatter.format(Date(millis)) // Update the displayed text
                    }
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { onShowDialogChange(false) }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}


/**
 * Composable function for a Month and Year Picker using an ExposedDropdownMenuBox.
 *
 * @param selectedMonthName The currently selected month's name (e.g., "Jan").
 * @param selectedYear The currently selected year (e.g., 2023).
 * @param months List of month names to display in the dropdown.
 * @param isExpanded Boolean state to control the expansion of the dropdown menu.
 * @param onExpandedChange Lambda to update the [isExpanded] state.
 * @param onMonthSelected Lambda callback that provides the name of the selected month.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthYearPicker(
    selectedMonthName: String,
    selectedYear: Int, // accept year
    months: List<String>,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onMonthSelected: (String) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = isExpanded,
        modifier = Modifier.fillMaxWidth(),
        onExpandedChange = onExpandedChange,
    ) {
        TextField(
            modifier = Modifier
                .menuAnchor() // Important: Set the TextField as an anchor
                .fillMaxWidth()
                .focusProperties { canFocus = false } // Prevent keyboard from popping up
                .padding(bottom = 8.dp),
            readOnly = true,
            value = "$selectedMonthName $selectedYear", // Displays the selected month and year
            onValueChange = {},
            label = { Text("Select Month") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
        )
        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            months.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        onMonthSelected(selectionOption)
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DisplayDatePickerPreview() {
    FormScreen()
}