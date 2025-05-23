package com.example.fittrackapp.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

/**
 * Composable function for the "Edit Profile" screen.
 * Allows users to modify their profile information.
 *
 * @param navController The NavController used for navigation actions, specifically to go back.
 */
@Composable
fun EditProfileScreen(navController: NavController) {
    var nickname by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Edit Profile", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.size(16.dp))
        TextField(value = nickname, onValueChange = { nickname = it }, label = { Text("Nickname") })
        Spacer(modifier = Modifier.size(8.dp))
        TextField(value = gender, onValueChange = { gender = it }, label = { Text("Gender") })
        Spacer(modifier = Modifier.size(8.dp))
        TextField(value = age, onValueChange = { age = it }, label = { Text("Age") })
        Spacer(modifier = Modifier.size(8.dp))
        TextField(value = region, onValueChange = { region = it }, label = { Text("Region") })
        Spacer(modifier = Modifier.size(8.dp))
        TextField(value = bio, onValueChange = { bio = it }, label = { Text("Bio") })
        Spacer(modifier = Modifier.size(16.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("Save")
        }
    }
}