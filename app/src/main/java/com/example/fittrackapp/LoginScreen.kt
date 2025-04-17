package com.example.fittrackapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun WelcomeScreen(navController: NavController){
    val state = remember { TextFieldState() }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var icon = if (showPassword) {
        Icons.Filled.Visibility
    } else {
        Icons.Filled.VisibilityOff
    }
    Surface(modifier = Modifier.fillMaxSize(),
        ) {
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Image(painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(300.dp))
            Spacer(modifier = Modifier.height(32.dp))
            Text(text = "Welcome to Fit Track", fontSize = 30.sp,
                color = androidx.compose.ui.graphics.Color.Black,
                fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(64.dp))
            TextField(label = { Text(text = "Email: example@gmail.com")},
                value = "",
                onValueChange = {},
                modifier = Modifier.width(280.dp),
                shape = RoundedCornerShape(16.dp))
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password:") },
                placeholder = { Text("Password") },
                modifier = Modifier.width(280.dp),
                shape = RoundedCornerShape(16.dp),
                trailingIcon = {
                    IconButton( onClick = { showPassword = !showPassword } ) {
                        Icon(
                            icon,
                            contentDescription = "Visibility icon"
                        )
                    }
                },
                visualTransformation = if (showPassword) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                }
            )
            Spacer(modifier = Modifier.height(32.dp))
            Row {
                Button(onClick = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                    modifier = Modifier.width(130.dp),) {
                    Text("Sign in")
                }
                Spacer(modifier = Modifier.width(20.dp))
                Button(onClick = {},
                    modifier = Modifier.width(130.dp)) {
                    Text("Sign up")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {},
                modifier = Modifier.width(280.dp),) {
                Text("Sign In / Sign Up with Google")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {},
                modifier = Modifier.width(280.dp),) {
                Text("Forget password")
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview(){

}
