package com.example.fittrackapp

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@SuppressLint("SuspiciousIndentation")
@Composable
fun HomeScreen(navController: NavController){
    val cardTitles = listOf("class 1", "class 2", "class 3", "class 4")
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
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
                        text = "Upload Workout",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable{
                            navController.navigate("add_workout")
                        }
                    )
                }
            }
            LazyColumn(modifier = Modifier.fillMaxSize().weight(1f).padding(bottom = 72.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                items(1) {
                    Card(modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(8.dp)
                        .clickable{
                            navController.navigate("exercise_detail/${ExerciseViewModel.CHEST_EXERCISE_ID}/Chest")
                        }) {
                        Box(modifier = Modifier.fillMaxSize()){
                            Image(painter = painterResource(id = R.drawable.chest),
                                contentDescription = "Chest Exercise",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop)
                            Text(text = "Chest",
                                modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
                                color = Color.White,
                                style = MaterialTheme.typography.titleLarge)
                            Button(onClick = {
                                navController.navigate("exercise_detail/${ExerciseViewModel.CHEST_EXERCISE_ID}/Chest")
                            },
                                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)) {
                                Text("Move with us!")
                            }
                        }
                    }
                    Card(modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(8.dp)
                        .clickable{
                            navController.navigate("exercise_detail/${ExerciseViewModel.ABS_EXERCISE_ID}/Abs")
                        }) {
                        Box(modifier = Modifier.fillMaxSize()){
                            Image(painter = painterResource(id = R.drawable.abs),
                                contentDescription = "Abs Exercise",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop)
                            Text(text = "Abs",
                                modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
                                color = Color.White,
                                style = MaterialTheme.typography.titleLarge)
                            Button(onClick = {
                                navController.navigate("exercise_detail/${ExerciseViewModel.ABS_EXERCISE_ID}/Abs")
                            },
                                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)) {
                                Text("Move with us!")
                            }
                        }
                    }
                    Card(modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(8.dp)
                        .clickable{
                            navController.navigate("exercise_detail/${ExerciseViewModel.ARMS_EXERCISE_ID}/Arms")
                        }) {
                        Box(modifier = Modifier.fillMaxSize()){
                            Image(painter = painterResource(id = R.drawable.arms),
                                contentDescription = "Arm Exercise",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop)
                            Text(text = "Arms",
                                modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
                                color = Color.White,
                                style = MaterialTheme.typography.titleLarge)
                            Button(onClick = {
                                navController.navigate("exercise_detail/${ExerciseViewModel.ARMS_EXERCISE_ID}/Arms")
                            },
                                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)) {
                                Text("Move with us!")
                            }
                        }
                    }
                    Card(modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(8.dp)
                        .clickable{
                            navController.navigate("exercise_detail/${ExerciseViewModel.LEGS_EXERCISE_ID}/Legs")
                        }) {
                        Box(modifier = Modifier.fillMaxSize()){
                            Image(painter = painterResource(id = R.drawable.leg),
                                contentDescription = "Leg Exercise",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop)
                            Text(text = "Leg",
                                modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
                                color = Color.White,
                                style = MaterialTheme.typography.titleLarge)
                            Button(onClick = {
                                navController.navigate("exercise_detail/${ExerciseViewModel.LEGS_EXERCISE_ID}/Legs")
                            },
                                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)) {
                                Text("Move with us!")
                            }
                        }
                    }
                }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview(){
    HomeScreen(navController = NavController(LocalContext.current))
}