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
    val showForm = remember { mutableStateOf(false) } // 默认隐藏

    // context + DAO + WorkoutViewModel
    val context = LocalContext.current
    val dao = WorkoutDatabase.getDatabase(context).workoutDao()
    val workoutViewModel: WorkoutViewModel = viewModel(factory = WorkoutViewModelFactory(dao))

    // Form Status
    var date by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }

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

        // Show/Hide toggle button
        Button(
            onClick = { showForm.value = !showForm.value },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text(if (showForm.value) "❌ Hide Training Record" else "＋ Add Training Record")
        }

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
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Exercise Name") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Duration (minutes)") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
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
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
