package com.example.fittrackapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import android.icu.util.Calendar
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.breens.beetablescompose.BeeTablesCompose
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale

data class WorkoutData(val time: String, val category: String)

@Composable
fun FormScreen() {
    val workoutDataByDay = listOf(
        WorkoutData("08:00 AM", "Warm-up"),
        WorkoutData("10:00 AM", "Strength"),
        WorkoutData("12:00 PM", "Cardio")
    )

    val workoutDataByMonth = listOf(
        WorkoutData("01/01/2024", "Warm-up"),
        WorkoutData("02/01/2024", "Strength"),
        WorkoutData("03/01/2024", "Cardio")
    )

    Surface(modifier = Modifier.fillMaxSize(),
        color = Color(0xFFDDFCB7)) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Column(modifier = Modifier.weight(1f).fillMaxWidth()) {
                val headerTitles = listOf("Time","Category")
                DisplayDatePicker()
                Spacer(modifier = Modifier.height(16.dp))
                BeeTablesCompose(data = workoutDataByDay,headerTableTitles = headerTitles)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.weight(1f).fillMaxWidth()) {
                val headerTitles = listOf("Date","Category")
                TestMenu()
                Spacer(modifier = Modifier.height(16.dp))
                BeeTablesCompose(data = workoutDataByMonth,headerTableTitles = headerTitles)
            }
        }
    }
}

@RequiresApi(0)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayDatePicker() {

    val calendar = Calendar.getInstance()
    var birthday by remember { mutableStateOf("") }
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    )
    var showDatePicker by remember {
        mutableStateOf(false)
    }
    var selectedDate by remember {
        mutableStateOf(calendar.timeInMillis)
    }
        TextField(
            value = birthday,
            onValueChange = {},
            readOnly = true,
            label = { Text("Search by date") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true },
            trailingIcon = {
                Icon(
                    painter = painterResource(R.drawable.calendar_icon),
                    contentDescription = "Select Date",
                    modifier = Modifier
                        .clickable { showDatePicker = true }
                        .size(40.dp)
                )
            }
        )
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = {
                    showDatePicker = false
                },
                confirmButton = {
                    TextButton(onClick = {
                        showDatePicker = false
                        selectedDate = datePickerState.selectedDateMillis!!
                        birthday = "DoB: ${formatter.format(Date(selectedDate))}"
                    }) {
                        Text(text = "OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDatePicker = false
                    }) {
                        Text(text = "Cancel")
                    }
                }
            ) //end of dialog
            { //still column scope
                DatePicker(
                    state = datePickerState
                )
            }
        }// end of if

}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestMenu(){
    val states = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    var isExpanded by remember { mutableStateOf(false) }
    var selectedState = remember { mutableStateOf(states[0])}

        Spacer(modifier = Modifier.height(36.dp))
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            modifier = Modifier.fillMaxWidth(),
            onExpandedChange = { isExpanded = it },
        ) {
            TextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .focusProperties {
                        canFocus = false
                    }
                    .padding(bottom = 8.dp),
                readOnly = true,
                value = selectedState.value,
                onValueChange = {},
                label = { Text("Month") },
//manages the arrow icon up and down
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
            )
            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false }
            )
            {
                states.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            selectedState.value = selectionOption
                            isExpanded = false},
                        contentPadding =
                            ExposedDropdownMenuDefaults.ItemContentPadding,
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