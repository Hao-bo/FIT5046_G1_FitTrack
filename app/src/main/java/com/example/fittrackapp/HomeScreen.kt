package com.example.fittrackapp

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(exerciseViewModel: ExerciseViewModel = viewModel()) {

    val exercises by exerciseViewModel.exercises.collectAsState()
    val showForm = remember { mutableStateOf(false) }

    val context = LocalContext.current
    val dao = WorkoutDatabase.getDatabase(context).workoutDao()
    val workoutViewModel: WorkoutViewModel = viewModel(factory = WorkoutViewModelFactory(dao))

    var date by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    val savedRecords by workoutViewModel.records.collectAsState()

    var selectedDate by remember { mutableStateOf("") }

    val filteredRecords = if (selectedDate.isBlank()) savedRecords else savedRecords.filter { it.date == selectedDate }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Title bar
        Column {
            Text(
                text = "Home",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "What's New",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Start Workout",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Course List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(exercises) { exercise ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(8.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.gym1),
                            contentDescription = "Gym",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            text = exercise.translations.firstOrNull()?.name ?: "No name",
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Button(
                            onClick = {
                                exerciseViewModel.addToMyCourses(context, exercise)
                            },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp)
                        ) {
                            Text("Move with us!")
                        }
                    }
                }
            }
        }

        // Toggle Form
        Button(
            onClick = { showForm.value = !showForm.value },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text(if (showForm.value) "❌ Hide Training Record" else "＋ Add Training Record")
        }

        // Form Input
        AnimatedVisibility(
            visible = showForm.value,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column {
                Text(
                    text = "Add Training Record",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date (yyyy-MM-dd)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Exercise Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Duration (minutes)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
                Button(
                    onClick = {
                        if (date.isNotBlank() && name.isNotBlank() && duration.isNotBlank()) {
                            val record = WorkoutRecord(
                                date = date,
                                exerciseName = name,
                                durationMinutes = duration.toIntOrNull() ?: 0
                            )
                            workoutViewModel.insertRecord(record)
                            Toast.makeText(context, "Saved successfully!", Toast.LENGTH_SHORT).show()
                            date = ""
                            name = ""
                            duration = ""
                        } else {
                            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text("Save Record")
                }
            }
        }

        // Filter input
        OutlinedTextField(
            value = selectedDate,
            onValueChange = { selectedDate = it },
            label = { Text("Filter by Date (yyyy-MM-dd)") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // Display saved training records
        Text(
            text = "Saved Records",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            items(filteredRecords) { record ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(" ${record.date}")
                            Text(" ${record.exerciseName}")
                            Text(" ${record.durationMinutes} min")
                        }
                        Button(
                            onClick = {
                                workoutViewModel.deleteRecord(record)
                                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
