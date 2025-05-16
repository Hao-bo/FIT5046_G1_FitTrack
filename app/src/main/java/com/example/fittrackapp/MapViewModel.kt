package com.example.fittrackapp

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.common.location.Location
import com.mapbox.common.location.LocationProvider
import com.mapbox.common.location.LocationServiceFactory
import com.mapbox.geojson.Point
import com.mapbox.search.discover.Discover
import com.mapbox.search.discover.DiscoverOptions
import com.mapbox.search.discover.DiscoverQuery
import com.mapbox.search.discover.DiscoverResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val discover: Discover = Discover.create() // Initialize Discover
    private val locationProvider: LocationProvider = LocationServiceFactory.getOrCreate()
        .getDeviceLocationProvider(null)
        .value ?: throw Exception("Failed to get device location provider")


    // StateFlow to hold the search results
    private val _gymResults = MutableStateFlow<List<DiscoverResult>>(emptyList())
    val gymResults: StateFlow<List<DiscoverResult>> = _gymResults.asStateFlow()

    // StateFlow to hold the last known user location
    private val _userLocation = MutableStateFlow<Point?>(null)
    val userLocation: StateFlow<Point?> = _userLocation.asStateFlow()

    // StateFlow for loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // StateFlow for error messages
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        getCurrentUserLocation()
        Log.d("MapViewModel", "getCurrentUserLocation() was called")
    }

    private fun getCurrentUserLocation() {
        locationProvider.getLastLocation { location ->
            if (location != null) {
                Log.d("MapViewModel", "User location: ${location.latitude}, ${location.longitude}")
                _userLocation.value = location.toPoint()
            }
        }
    }

    fun Location.toPoint(): Point = Point.fromLngLat(this.longitude, this.latitude)

    fun searchNearbyGyms(proximity: Point? = _userLocation.value) {
        if (proximity == null) {
            _errorMessage.value = "User location not available."
            Log.e("MapViewModel", "User location not available for search.")
            return
        }

        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            val response = discover.search(
                query = DiscoverQuery.Category.GYM_FITNESS,
                proximity = proximity,
                options = DiscoverOptions(limit = 10)
            )

            response.onValue { results ->
                _gymResults.value = results
                _isLoading.value = false
            }.onError { e ->
                Log.e("MapViewModel", "Error happened during search request", e)
                _errorMessage.value = "Failed to find gyms: ${e.message}"
                _gymResults.value = emptyList() // Clear previous results on error
                _isLoading.value = false
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun clearSearchResults() {
        _gymResults.value = emptyList()
    }
}