package com.example.fittrackapp.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.fittrackapp.R
import com.example.fittrackapp.ui.workout.ExerciseViewModel

/**
 * Composable function for the Home Screen.
 * This screen displays a list of exercise categories (e.g., Chest, Abs, Arms, Legs)
 * as clickable cards, allowing users to navigate to detailed exercise information.
 * It also provides a link to upload a new workout.
 *
 * @param navController The NavController used for navigation actions.
 */
@SuppressLint("SuspiciousIndentation")
@Composable
fun HomeScreen(navController: NavController){
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
                        text = "Upload Workout",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable{
                            navController.navigate("add_workout")
                        }
                    )
                }
            }
            // LazyColumn to display a list of exercise category cards.
            // Using weight(1f) to make it take available vertical space.
            // Padding at the bottom to avoid overlap with a bottom navigation bar (if any, e.g., 72.dp).
            LazyColumn(modifier = Modifier.fillMaxSize().weight(1f).padding(bottom = 72.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // because only 4 cards here
                items(1) {
                    // Card for "Chest" exercises.
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
                    // Card for "Abs" exercises.
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
                    // Card for "Arms" exercises.
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
                    // Card for "Leg" exercises.
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