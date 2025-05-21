package com.example.fittrackapp

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.android.core.permissions.PermissionsListener
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import android.Manifest
import androidx.navigation.NavType
import androidx.navigation.navArgument

class MainActivity : ComponentActivity(), PermissionsListener {

    lateinit var permissionsManager: PermissionsManager
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        permissionsManager = PermissionsManager(this)
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Permission sensitive logic called here, such as activating the Maps SDK's LocationComponent to show the device's location
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(this)
        }

        // Ask for notification permission
        askNotificationPermission()

        // ... (rest of your onCreate code)
        if (PermissionsManager.areLocationPermissionsGranted(this)) { //
            // Permission sensitive logic called here, such as activating the Maps SDK's LocationComponent to show the device's location
        } else {
            permissionsManager = PermissionsManager(this) //
            permissionsManager.requestLocationPermissions(this) //
        }

        enableEdgeToEdge()
        setContent {
            FitTrackAppTheme {
                BottomNavigationBarM3()
            }
        }
    }

    // Declare the launcher at the top of your Activity/Fragment
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. You can now show notifications.
                Toast.makeText(this, "Notification permission granted.", Toast.LENGTH_SHORT).show()
            } else {
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied.
                Toast.makeText(this, "Notification permission denied. Reminders might not work.", Toast.LENGTH_LONG).show()
            }
        }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: Display an educational UI explaining to the user the importance of
                // this permission for the feature.
                // For now, just request the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)} passing\n      in a {@link RequestMultiplePermissions} object for the {@link ActivityResultContract} and\n      handling the result in the {@link ActivityResultCallback#onActivityResult(Object) callback}.")
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationBarM3() {
    val navRoutes = listOf(
        NavRoute("home", Icons.Default.Home, "Home"),
        NavRoute("map", Icons.Default.Place, "Map"),
        NavRoute("form", Icons.Default.Info, "Form"),
    )

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            if (currentRoute != "login" && currentRoute != "profile" && currentRoute != "add_workout"){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .systemBarsPadding()
                        .height(46.dp)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                            .clickable {
                                navController.navigate("profile") {
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "User",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

//                    if (currentRoute == "home"){
//                        Icon(
//                            imageVector = Icons.Default.Add,
//                            contentDescription = "Upload workout data",
//                            modifier = Modifier.size(24.dp)
//                                .clickable {
//                                    showDialog = true
//                                }
//                        )
//                    }

                }
            }
        },
        bottomBar = {
            if (currentRoute != "login"){
                NavigationBar(
                    containerColor = Color(0xFFADD8E6)
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    navRoutes.forEach { navRoute ->
                        val isSelected = currentDestination?.hierarchy?.any { it.route == navRoute.route } == true

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                navController.navigate(navRoute.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    navRoute.icon,
                                    contentDescription = navRoute.label
                                )
                            },
                            label = { Text(navRoute.label) },
                            alwaysShowLabel = false,
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("login") { WelcomeScreen(navController) }
            composable("home") {  HomeScreen(navController)  }
            composable("form") {  FormScreen()  }
            composable("map") { MapScreen()  }
            composable("profile") { ProfileScreen(navController) }
            composable("add_workout") { AddWorkoutScreen(
                viewModel = WorkoutViewModel(Graph.workoutRepository),
                navController,
//                onSaveWorkout = { timestamp, duration, activityType, notes ->
//                    navController.popBackStack()
//                }
            ) }

            composable(
                route = "exercise_detail/{exerciseId}/{muscleName}",
                arguments = listOf(
                    navArgument("exerciseId") { type = NavType.IntType },
                    navArgument("muscleName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val exerciseId = backStackEntry.arguments?.getInt("exerciseId") ?: 0
                val muscleName = backStackEntry.arguments?.getString("muscleName") ?: ""
                ExerciseDetailScreen(
                    navController = navController,
                    exerciseId = exerciseId,
                    muscleName = muscleName
                )
            }
        }
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

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallTopAppBarExample() {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Small Top App Bar")
                }
            )
        },
    ) {
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FitTrackAppTheme {
        BottomNavigationBarM3()
    }
}
