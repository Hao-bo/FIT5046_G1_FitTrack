package com.example.fittrackapp

import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(){
    val cardTitles = listOf("class 1", "class 2", "class 3", "class 4")
    var showDialog by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxSize().padding(8.dp)){
        LazyColumn(modifier = Modifier.fillMaxSize().padding(bottom = 72.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(cardTitles.size) {
                Card(modifier = Modifier.fillMaxWidth().height(200.dp).padding(8.dp)) {
                    Box(modifier = Modifier.fillMaxSize()){
                            Image(painter = painterResource(id = R.drawable.gym1),
                                contentDescription = "Gym",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop)
                            Text(text = "Class 1",
                                modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
                                color = Color.White,
                                style = MaterialTheme.typography.titleLarge)
                            Button(onClick = { },
                                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)) {
                                Text("Move with us!")
                            }
                    }
                }
                Card(modifier = Modifier.fillMaxWidth().height(200.dp).padding(8.dp)) {
                    Box(modifier = Modifier.fillMaxSize()){
                        Image(painter = painterResource(id = R.drawable.gym1),
                            contentDescription = "Gym",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop)
                        Text(text = "Class 2",
                            modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge)
                        Button(onClick = { },
                            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)) {
                            Text("Move with us!")
                        }
                    }
                }
                Card(modifier = Modifier.fillMaxWidth().height(200.dp).padding(8.dp)) {
                    Box(modifier = Modifier.fillMaxSize()){
                        Image(painter = painterResource(id = R.drawable.gym1),
                            contentDescription = "Gym",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop)
                        Text(text = "Class 3",
                            modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge)
                        Button(onClick = { },
                            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)) {
                            Text("Move with us!")
                        }
                    }
                }
                Card(modifier = Modifier.fillMaxWidth().height(200.dp).padding(8.dp)) {
                    Box(modifier = Modifier.fillMaxSize()){
                        Image(painter = painterResource(id = R.drawable.gym1),
                            contentDescription = "Gym",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop)
                        Text(text = "Class 4",
                            modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge)
                        Button(onClick = { },
                            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)) {
                            Text("Move with us!")
                        }
                    }
                }
            }
        }
        Button(onClick = { showDialog = true },
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)) {
            Text(text = "Upload my workout data")
        }
        if (showDialog){
            AlertDialog(onDismissRequest = { showDialog = false },
                confirmButton = {
                    Row(modifier = Modifier.fillMaxWidth().padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Button(onClick = { showDialog = false }) {
                            Text(text = "Cancel")
                        }
                        Button(onClick = { showDialog = false }) {
                            Text(text = "Upload")
                        }
                    }
                },
                title = { Text(text = "Upload your workout data")},
                text = {
                    Column{
                        OutlinedTextField(value = "",
                            onValueChange = {},
                            label = { Text(text = "Date")},
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                        OutlinedTextField(value = "",
                            label = {Text(text = "Category")},
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                    }
                })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview(){
    HomeScreen()
}