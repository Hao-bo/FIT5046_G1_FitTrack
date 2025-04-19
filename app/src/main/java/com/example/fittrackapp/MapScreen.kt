package com.example.fittrackapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.runtime.ComposableOpenTarget
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location

@Composable
fun MapScreen(){
    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)){
            val mapViewportState = rememberMapViewportState()
            MapboxMap(
                Modifier.fillMaxSize(),
                mapViewportState = mapViewportState,
            ) {
                MapEffect(Unit) { mapView ->
                    mapView.location.updateSettings {
                        locationPuck = createDefault2DPuck(withBearing = true)
                        enabled = true
                        puckBearing = PuckBearing.COURSE
                        puckBearingEnabled = true
                    }
                    mapViewportState.transitionToFollowPuckState()
                }
            }
        }
        Column(modifier = Modifier.weight(1f).padding(16.dp)) {
            Spacer(modifier = Modifier.height(26.dp))
            GymCard(
                name = "Training Day Gym Clayton",
                address = "123 Fitness Avenue, Clayton, VIC 3168",
                hours = "5:00 AM - 11:00 PM",
                distance = "600m",
                description = "This modern gym features top-tier equipment, personal training sessions, " +
                        "group classes, and a spacious cardio area. Perfect for both beginners and fitness enthusiasts!"
            )

            Spacer(modifier = Modifier.height(16.dp))

            GymCard(
                name = "Iron Temple Fitness Center",
                address = "78 Workout Road, Mulgrave, VIC 3170",
                hours = "24/7 Access",
                distance = "1.2km",
                description = "Iron Temple offers a hardcore training environment for serious lifters. Includes a full weight room, " +
                        "powerlifting platforms, recovery zone, and nutrition bar."
            )
        }
    }
}

@Composable
fun GymCard(
    name: String,
    address: String,
    hours: String,
    distance: String,
    description: String
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
            Text(text = "🚶‍♂️ $distance from your location", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "💪 $description",
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 20.sp
            )
        }
    }
}


@Preview
@Composable
fun MapScreenPreview(){
    MapScreen()
}