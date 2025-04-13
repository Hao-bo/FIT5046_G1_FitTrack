package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.lazy.items

data class Exercise(
    val name: String,
    val description: String,
    val recommendation: String
)


@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    var selectedType by remember { mutableStateOf("") }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var exerciseName by remember { mutableStateOf("") }
    var setsOrDuration by remember { mutableStateOf("") }
    var repsOrDistance by remember { mutableStateOf("") }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = {
                        Icon(painterResource(id = R.drawable.ic_home), contentDescription = "Home")
                    },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = {
                        Icon(painterResource(id = R.drawable.ic_map), contentDescription = "Map")
                    },
                    label = { Text("Map") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = {
                        Icon(painterResource(id = R.drawable.ic_form), contentDescription = "Form")
                    },
                    label = { Text("Form") }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFE3FFBB))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Training Summary
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFCDECC8)),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("🔥 Today's Training Summary", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("(Trained 20 min)")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("🗓 Weekly Progress", color = Color.Blue)
                }
            }

            // Recommended Exercise (LazyColumn version)
            val recommendedExercises = listOf(
                Exercise("Barbell Bench Press", "Chest-focused", "3 sets × 10 reps"),
                Exercise("Push Ups", "Full body", "4 sets × 15 reps"),
                Exercise("Dumbbell Curl", "Biceps", "3 sets × 12 reps")
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp)
            ) {
                items(recommendedExercises) { exercise ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFD9F0D2)),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("🏋 Recommended Exercises", color = Color.Red, fontWeight = FontWeight.Bold)
                            Text("🏋 ${exercise.name}", fontWeight = FontWeight.Bold)
                            Text("Description: ${exercise.description}")
                            Text("Recommended: ${exercise.recommendation}")
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Text("⭐ Favorite")
                                Text("➕ Add to Plan")
                            }
                        }
                    }
                }
            }


            // Custom Input
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFD9F0D2)),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("📝 Custom Exercise Input", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    Box {
                        OutlinedTextField(
                            value = selectedType,
                            onValueChange = {},
                            label = { Text("Select Training Type") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isDropdownExpanded = true },
                            readOnly = true
                        )
                        DropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Strength Training") },
                                onClick = {
                                    selectedType = "Strength Training"
                                    isDropdownExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Cardio Training") },
                                onClick = {
                                    selectedType = "Cardio Training"
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = exerciseName,
                        onValueChange = { exerciseName = it },
                        label = { Text("Exercise Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = setsOrDuration,
                        onValueChange = { setsOrDuration = it },
                        label = { Text("Sets/Duration") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = repsOrDistance,
                        onValueChange = { repsOrDistance = it },
                        label = { Text("Reps/Distance") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("➕ Add to My Plan")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    HomeScreen()
}
