package com.example.fittrackapp

import android.app.Application
import com.example.fittrackapp.Graph.authViewModel
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.MapboxNavigationProvider
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp

class FitTrackApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        authViewModel = AuthViewModel(this)
        Graph.provide(this,authViewModel)
        // Use Provider which is the latest SDK version of Mapbox
        val mapboxNavigation = MapboxNavigationProvider.create(
            NavigationOptions.Builder(applicationContext)
                .build()
        )

    }
}

