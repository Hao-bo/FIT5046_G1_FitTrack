package com.example.fittrackapp.ui.map

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.common.location.Location
import com.mapbox.common.location.LocationProvider
import com.mapbox.common.location.LocationServiceFactory
import com.mapbox.geojson.Point
import com.mapbox.navigation.base.extensions.applyDefaultNavigationOptions
import com.mapbox.navigation.base.extensions.applyLanguageAndVoiceUnitOptions
import com.mapbox.navigation.base.route.NavigationRoute
import com.mapbox.navigation.base.route.NavigationRouterCallback
import com.mapbox.navigation.base.route.RouterFailure
import com.mapbox.navigation.core.MapboxNavigationProvider
import com.mapbox.search.discover.Discover
import com.mapbox.search.discover.DiscoverOptions
import com.mapbox.search.discover.DiscoverQuery
import com.mapbox.search.discover.DiscoverResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the MapScreen.
 * This ViewModel handles fetching nearby gym locations using Mapbox Discover API,
 * managing user location, and generating navigation routes using Mapbox Navigation SDK.
 *
 * @param application The application context, required for AndroidViewModel.
 */
class MapViewModel(application: Application) : AndroidViewModel(application) {

    // Initialize Discover
    private val discover: Discover = Discover.Companion.create()
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

    // implement route
    // StateFlow to hold navigation routes
    private val _navigationRoutes = MutableStateFlow<List<NavigationRoute>>(emptyList())
    val navigationRoutes: StateFlow<List<NavigationRoute>> = _navigationRoutes.asStateFlow()

    // StateFlow for selected gym to navigate to
    private val _selectedGym = MutableStateFlow<DiscoverResult?>(null)
    val selectedGym: StateFlow<DiscoverResult?> = _selectedGym.asStateFlow()

    // StateFlow for navigation state
    private val _isNavigating = MutableStateFlow(false)
    val isNavigating: StateFlow<Boolean> = _isNavigating.asStateFlow()

    // Track navigation initialization state
    private val _isNavigationInitialized = MutableStateFlow(false)


    /**
     * Initialization block.
     * Fetches the current user location when the ViewModel is created.
     */
    init {
        getCurrentUserLocation()
    }

    /**
     * Fetches the last known location from the location provider and updates the _userLocation StateFlow.
     */
    private fun getCurrentUserLocation() {
        locationProvider.getLastLocation { location ->
            if (location != null) {
                _userLocation.value = location.toPoint()
            }
        }
    }

    /**
     * Extension function to convert Mapbox common.location.Location to Mapbox geojson.Point.
     * @return A Point object representing the location.
     */
    fun Location.toPoint(): Point = Point.fromLngLat(this.longitude, this.latitude)

    /**
     * Searches for nearby gyms using the Mapbox Discover API.
     * Uses the provided proximity point or the current user location.
     * Updates _gymResults, _isLoading, and _errorMessage StateFlows.
     *
     * @param proximity The Point around which to search. Defaults to the current _userLocation.value.
     */
    fun searchNearbyGyms(proximity: Point? = _userLocation.value) {
        if (proximity == null) {
            _errorMessage.value = "User location not available."
            return
        }

        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            val response = discover.search(
                query = DiscoverQuery.Category.GYM_FITNESS,
                proximity = proximity,
                options = DiscoverOptions(limit = 5)
            )

            response.onValue { results ->
                _gymResults.value = results
                _isLoading.value = false
            }.onError { e ->
                _errorMessage.value = "Failed to find gyms: ${e.message}"
                _gymResults.value = emptyList() // Clear previous results on error
                _isLoading.value = false
            }
        }
    }

    /**
     * Sets the navigation initialization state to true.
     * Called when the Mapbox Navigation SDK is confirmed to be ready.
     */
    // Set navigation as initialize
    fun setNavigationInitialized() {
        _isNavigationInitialized.value = true
    }

    /**
     * Initiates navigation to a selected gym.
     * Sets the selected gym, updates loading state, and requests a route.
     *
     * @param gymResult The DiscoverResult representing the gym to navigate to.
     */
    // Select a gym and generate a navigation route
    fun navigateToGym(gymResult: DiscoverResult) {

        val userPoint = _userLocation.value
        if (userPoint == null) {
            _errorMessage.value = "User location not available for navigation."
            _isLoading.value = false
            return
        }

        _isLoading.value = true
        _selectedGym.value = gymResult

        requestRoute(userPoint, gymResult)
    }

    /**
     * Requests navigation routes from the user's current location to the destination gym.
     * Uses Mapbox Navigation SDK to fetch routes.
     *
     * @param userPoint The starting Point (user's location).
     * @param gymResult The DiscoverResult representing the destination gym.
     */
    private fun requestRoute(userPoint: Point, gymResult: DiscoverResult) {
        // Check navigation instance again
        val navigationInstance = MapboxNavigationProvider.retrieve()
        if (navigationInstance == null) {
            _errorMessage.value = "Navigation is not available. Please restart the app."
            _isLoading.value = false
            return
        }


        // Build route options for the directions request.
        // Constructing a directions request
        val routeOptions = RouteOptions.builder()
            .applyDefaultNavigationOptions()
            .applyLanguageAndVoiceUnitOptions(getApplication())
            .coordinatesList(listOf(userPoint, gymResult.coordinate))
            .alternatives(true)
            .layersList(listOf(1,null))
            .build()

        // Request route
        navigationInstance.requestRoutes(
            routeOptions,
            object : NavigationRouterCallback {
                override fun onRoutesReady(
                    routes: List<NavigationRoute>,
                    routerOrigin: String
                ) {
                    _navigationRoutes.value = routes
                    _isLoading.value = false
                    _isNavigating.value = true
                }

                override fun onFailure(
                    reasons: List<RouterFailure>,
                    routeOptions: RouteOptions
                ) {
                    _errorMessage.value = "Failed to generate route: ${reasons.firstOrNull()?.message ?: "Unknown error"}"
                    _isLoading.value = false
                }

                override fun onCanceled(
                    routeOptions: RouteOptions,
                    routerOrigin: String
                ) {
                    Log.e("MapViewModel_NAV", "onCanceled CALLED")
                    _isLoading.value = false
                }
            }
        )
    }

    /**
     * Clears the current navigation state.
     * Resets navigation routes, selected gym, and navigation status.
     */
    // Clear navigation state
    fun clearNavigation() {
        _navigationRoutes.value = emptyList()
        _selectedGym.value = null
        _isNavigating.value = false
    }

    /**
     * Clears the current error message.
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    /**
     * Clears the current gym search results.
     */
    fun clearSearchResults() {
        _gymResults.value = emptyList()
    }
}