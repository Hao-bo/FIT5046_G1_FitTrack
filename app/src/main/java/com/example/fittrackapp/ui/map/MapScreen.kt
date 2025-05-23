package com.example.fittrackapp.ui.map

import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Icon
import androidx.compose.runtime.DisposableEffect
import com.example.fittrackapp.ui.map.MapViewModel
import com.example.fittrackapp.R
import com.mapbox.navigation.core.MapboxNavigationProvider
import com.mapbox.navigation.ui.maps.NavigationStyles
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineApiOptions
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineViewOptions
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineColorResources

/**
 * Composable function for the Map Screen.
 * This screen displays a Mapbox map, allows users to search for nearby gyms,
 * view them as markers, and navigate to a selected gym.
 *
 * @param mapViewModel The ViewModel responsible for map-related logic,
 * fetching gym data, user location, and handling navigation.
 */
@Composable
fun MapScreen(mapViewModel: MapViewModel = viewModel()) {

    val context = LocalContext.current
    val gymResults by mapViewModel.gymResults.collectAsState()
    val userLocation by mapViewModel.userLocation.collectAsState()
    val isLoading by mapViewModel.isLoading.collectAsState()
    val errorMessage by mapViewModel.errorMessage.collectAsState()

    // route on map
    val navigationRoutes by mapViewModel.navigationRoutes.collectAsState()
    val selectedGym by mapViewModel.selectedGym.collectAsState()
    val isNavigating by mapViewModel.isNavigating.collectAsState()

    // Initialization Navigation SDK
    LaunchedEffect(Unit) {
        // use Provider gst instance
        val navigationInstance = MapboxNavigationProvider.retrieve()
        if (navigationInstance != null) {
            mapViewModel.setNavigationInitialized()
        }
    }

    // Map marker management related status
    var pointAnnotationManager: PointAnnotationManager? by remember { mutableStateOf(null) }
    val annotations = remember { mutableListOf<PointAnnotation>() }
    val customMarkerBitmap = remember(context) { createCustomMarkerBitmap(context) }
    var mapboxMapInstance: MapboxMap? by remember { mutableStateOf(null) }

    // Draw route
    val routeLineColorResources = remember { RouteLineColorResources.Builder().build() }
    val routeLineViewOptions = remember {
        MapboxRouteLineViewOptions.Builder(context)
            .routeLineColorResources(routeLineColorResources)
            .routeLineBelowLayerId("road-label-navigation")
            .build()
    }
    val routeLineApiOptions = remember {
        MapboxRouteLineApiOptions.Builder()
            .vanishingRouteLineEnabled(true)
            .build()
    }
    val routeLineApi = remember { MapboxRouteLineApi(routeLineApiOptions) }
    val routeLineView = remember { MapboxRouteLineView(routeLineViewOptions) }


    // clean resource when user go to other page
    // DisposableEffect to clean up route line resources when the composable leaves composition.
    DisposableEffect(Unit) {
        onDispose {
            routeLineView.cancel()
            routeLineApi.cancel()
        }
    }

    // LaunchedEffect to update the route line on the map when navigationRoutes change.
    // update route
    LaunchedEffect(navigationRoutes) {
        if (navigationRoutes.isNotEmpty()) {
            routeLineApi.setNavigationRoutes(navigationRoutes) { value ->
                mapboxMapInstance?.style?.apply {
                    routeLineView.renderRouteDrawData(this, value)
                }
            }
        } else {
            routeLineApi.clearRouteLine { value ->
                mapboxMapInstance?.style?.apply {
                    routeLineView.renderClearRouteLineValue(this, value)
                }
            }
        }
    }

    // Handling location permissions
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Permission Request Launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasLocationPermission = isGranted
            if (isGranted) {
                // After permission is granted, automatically search for nearby gyms
                mapViewModel.searchNearbyGyms()
                Toast.makeText(context, "Location permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    context,
                    "Location permission denied. Cannot search nearby gyms",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    )

    // Check location permissions on first entry
    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Handling Error Messages
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            mapViewModel.clearErrorMessage()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            val mapViewportState = rememberMapViewportState {
                // Center to the user's location if available
                userLocation?.let {
                    setCameraOptions {
                        center(it)
                        zoom(14.0)
                    }
                }
            }
            MapboxMap(
                Modifier.fillMaxSize(),
                mapViewportState = mapViewportState
            ) {
                MapEffect(Unit) { mapView ->
                    // Initialize the map with navigation style
                    mapView.mapboxMap.loadStyle(NavigationStyles.NAVIGATION_DAY_STYLE) {
                        // Initialize the route line layers
                        routeLineView.initializeLayers(it)
                    }

                    // save map instance
                    mapboxMapInstance = mapView.mapboxMap

                    mapView.location.updateSettings {
                        locationPuck = createDefault2DPuck(withBearing = true)
                        enabled = true
                        puckBearing = PuckBearing.COURSE
                        puckBearingEnabled = true
                    }

                    // Initialize the mark manager
                    val annotationApi = mapView.annotations
                    pointAnnotationManager = annotationApi.createPointAnnotationManager()

                    mapViewportState.transitionToFollowPuckState()
                }

                // Update the markers on the map when results change
                LaunchedEffect(gymResults, pointAnnotationManager, isNavigating) {
                    if (!isNavigating) {
                        pointAnnotationManager?.let { manager ->
                            // clear old marker
                            manager.deleteAll()
                            annotations.clear()

                            // add new marker
                            gymResults.forEach { result ->
                                val pointAnnotationOptions = PointAnnotationOptions()
                                    .withPoint(result.coordinate)
                                    .withIconImage(customMarkerBitmap)
                                    .withIconAnchor(IconAnchor.BOTTOM)

                                manager.create(pointAnnotationOptions)?.let { annotation ->
                                    annotations.add(annotation)
                                }
                            }
                            // Adjust the camera to fit all markers
                            // Need to adjust later
                            if (gymResults.isNotEmpty() && mapboxMapInstance != null) {
                                val points = gymResults.map { it.coordinate }
                                val edgeInsets = EdgeInsets(100.0, 100.0, 300.0, 100.0)

                                mapboxMapInstance?.cameraForCoordinates(
                                    points,
                                    CameraOptions.Builder().build(),
                                    edgeInsets,
                                    null,
                                    null
                                ) {
                                    mapViewportState.flyTo(it)
                                }
                            }
                        }
                    }
                }

                // Update camera when navigating to a gym
                LaunchedEffect(selectedGym, isNavigating) {
                    if (isNavigating && selectedGym != null && userLocation != null) {
                        val points = listOf(userLocation!!, selectedGym!!.coordinate)
                        val edgeInsets = EdgeInsets(200.0, 100.0, 500.0, 100.0)

                        mapboxMapInstance?.cameraForCoordinates(
                            points,
                            CameraOptions.Builder().build(),
                            edgeInsets,
                            null,
                            null
                        ) {
                            mapViewportState.flyTo(it)
                        }
                    }
                }
            }

            // Navigation Mode UI
            if (isNavigating) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.TopStart)
                ) {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .align(Alignment.TopStart),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {

                        Button(
                            onClick = {
                                mapViewModel.clearNavigation()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(50)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Exit navigation",
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Exit navigation")
                        }

                        Spacer(modifier = Modifier.width(24.dp))

                        selectedGym?.let { gym ->
                            Text(
                                text = "Navigating to: ${gym.name}",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                modifier = Modifier
                                    .background(Color(0xCC000000), RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            )
                        }
                    }
                }
            } else {
                // Search Button on map top right
                Button(
                    onClick = {
                        if (hasLocationPermission) {
                            mapViewModel.searchNearbyGyms(userLocation)
                        } else {
                            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            Toast.makeText(
                                context,
                                "Please grant location permission first",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(50)
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search gym nearby",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Search gym nearby")
                }
            }

            // display loading state
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x88000000)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }

        if (!isNavigating) {
            // display gym card profile
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
//                .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // show results number
                if (gymResults.isNotEmpty()) {
                    Text(
                        text = "${gymResults.size} gyms found nearby ",
//                    text = "debug: ${gymResults.toString()}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // show gym cards if there are results
                if (gymResults.isNotEmpty()) {
                    LazyColumn(modifier = Modifier.fillMaxHeight()) {
                        items(gymResults) { result ->
                            GymCard(
                                name = result.name,
                                address = result.address.formattedAddress ?: "No address available",
                                hours = "No hours info",
                                distance = "N/A", // results have no distance need to calculate
                                description = result.categories.joinToString(", "),
                                onNavigateClick = {
                                    mapViewModel.navigateToGym(result)
                                }
                            )
                        }
                    }
                } else {
                    // show the sample card if there are no results
                    if (!isLoading) {
                        Text(
                            text = "Search for gyms to see results here",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        // Sample Gym Card
                        GymCard(
                            name = "Training Day Gym Clayton",
                            address = "123 Fitness Avenue, Clayton, VIC 3168",
                            hours = "5:00 AM - 11:00 PM",
                            distance = "600m",
                            description = "This modern gym features top-tier equipment, personal training sessions, " +
                                    "group classes, and a spacious cardio area. Perfect for both beginners and fitness enthusiasts!",
                            onNavigateClick = {}
                        )
                    }
                }
            }
        }
    }
}

/**
 * Creates a custom marker bitmap from a drawable resource.
 * @param context The context to access resources.
 * @return A Bitmap object for the custom marker.
 */
// custom marker bitmap
fun createCustomMarkerBitmap(context: Context): Bitmap {
    val drawableId = R.drawable.red_marker
    return BitmapFactory.decodeResource(context.resources, drawableId)
}

@Preview(showBackground = true)
@Composable
fun MapScreenPreview() {
    GymCard(
        name = "Training Day Gym Clayton",
        address = "123 Fitness Avenue, Clayton, VIC 3168",
        hours = "5:00 AM - 11:00 PM",
        distance = "600m",
        description = "",
        onNavigateClick = {}
    )
}

/**
 * Composable function to display information about a single gym in a Card.
 *
 * @param name The name of the gym.
 * @param address The address of the gym.
 * @param hours The opening hours of the gym (currently a placeholder).
 * @param distance The distance to the gym (currently a placeholder).
 * @param description A description of the gym (uses categories from API).
 * @param onNavigateClick Lambda function to be invoked when the "Navigate To" button is clicked.
 */
@Composable
fun GymCard(
    name: String,
    address: String,
    hours: String,
    distance: String,
    description: String,
    onNavigateClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "📍 $address", style = MaterialTheme.typography.bodyMedium)
            Text(text = "🕒 Open: $hours", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "🚶‍♂️ $distance from your location",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "💪 $description",
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(),
                onClick = onNavigateClick
            ) {
                Text("Navigate To")
            }
        }
    }
}