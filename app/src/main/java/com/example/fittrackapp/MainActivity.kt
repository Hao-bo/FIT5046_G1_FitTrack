package com.example.fittrackapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fittrackapp.ui.theme.FitTrackAppTheme
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.android.core.permissions.PermissionsListener

class MainActivity : ComponentActivity(), PermissionsListener {
    lateinit var permissionsManager: PermissionsManager
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Permission sensitive logic called here, such as activating the Maps SDK's LocationComponent to show the device's location
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(this)
        }

        enableEdgeToEdge()
        setContent {
            FitTrackAppTheme {
                BottomNavigationBar()
            }
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

        override fun onExplanationNeeded(permissionsToExplain: List<String>) {
            Toast.makeText(this, "You need to accept location permissions.", Toast.LENGTH_SHORT).show()
        }

        override fun onPermissionResult(granted: Boolean) {
            if (granted) {
                Toast.makeText(this, "Location permission granted.", Toast.LENGTH_SHORT).show()
                // Permission sensitive logic called here, such as activating the Maps SDK's LocationComponent to show the device's location

            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show()
                // User denied the permission

            }
        }
}

data class NavRoute(val route: String,val icon: ImageVector,val label: String)

@Composable
fun BottomNavigationBar() {
    // Define the list of navigation routes using the data class
    val navRoutes = listOf(
        NavRoute("home", Icons.Default.Home, "Home"),
        NavRoute("map",Icons.Default.Place,"Map"),
        NavRoute("form", Icons.Default.Info, "Form")

    )

    val navController = rememberNavController()
    androidx.compose.material3.Scaffold(
        bottomBar = {
            BottomNavigation(
                modifier = Modifier.padding(bottom = 20.dp),
                backgroundColor = Color(0xFFADD8E6)
            ) //light blue color
            {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                navRoutes.forEach { navRoute ->
                    BottomNavigationItem(
                        icon = {
                            Icon(
                                navRoute.icon, contentDescription =
                                    navRoute.label
                            )
                        },
                        label = { Text(navRoute.label) },
                        selected = currentDestination?.route == navRoute.route,
                        onClick = {
                            navController.navigate(navRoute.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") {  }
            composable("form") { FormScreen() }
            composable("map") { MapScreen() }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FitTrackAppTheme {

    }
}