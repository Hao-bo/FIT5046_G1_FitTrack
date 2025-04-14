package com.example.fittrackapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposableOpenTarget
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
            Text(text = "Training Day Gym Clayton")
        }
    }
}


@Preview
@Composable
fun MapScreenPreview(){
    MapScreen()
}