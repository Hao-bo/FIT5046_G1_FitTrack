package com.example.fittrackapp

import android.app.Application
import android.util.Log
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.MapboxNavigationProvider
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp

class FitTrackApplication : Application() {
    override fun onCreate() {
        super.onCreate()

            // Use Provider which is the latest SDK version of Mapbox
            val mapboxNavigation = MapboxNavigationProvider.create(
                NavigationOptions.Builder(applicationContext)
                    .build()
            )

    }
}

