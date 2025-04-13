package com.example.fittrackapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WelcomeScreen(){
    Surface(modifier = Modifier.fillMaxSize(),
        color = Color(0xFFDDFCB7)) {
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
            TextField(label = { Text(text = "Email:")},
                value = "",
                onValueChange = {},
                modifier = Modifier.width(280.dp),
                shape = RoundedCornerShape(16.dp))
            Spacer(modifier = Modifier.height(16.dp))
            TextField(label = { Text(text = "Password:")},
                value = "",
                onValueChange = {},
                modifier = Modifier.width(280.dp),
                shape = RoundedCornerShape(16.dp))
            Spacer(modifier = Modifier.height(32.dp))
            Row {
                Button(onClick = {},
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
        }

    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview(){
    WelcomeScreen()
}
