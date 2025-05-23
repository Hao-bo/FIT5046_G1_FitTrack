package com.example.fittrackapp

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.fittrackapp.di.Graph
import com.example.fittrackapp.di.Graph.authViewModel
import com.example.fittrackapp.ui.auth.AuthViewModel
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.MapboxNavigationProvider
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Custom Application class for FitTrackApp.
 * This class is instantiated when the application starts and is used for
 * initializing application-wide components and settings.
 */
class FitTrackApplication : Application() {
    /**
     * Called when the application is starting, before any other application objects have been created.
     * Used for global initializations.
     */
    override fun onCreate() {
        super.onCreate()

        // Initialize AuthViewModel and provide it to the Graph (dependency container).
        // Note: Storing ViewModel instances directly in the Application class or a static object
        // like Graph can be problematic if not handled carefully with lifecycle considerations.
        // It's generally preferred to use ViewModelProviders at the Activity/Fragment level.
        authViewModel = AuthViewModel(this)
        Graph.provide(this,authViewModel)
        // Use Provider which is the latest SDK version of Mapbox
        val mapboxNavigation = MapboxNavigationProvider.create(
            NavigationOptions.Builder(applicationContext)
                .build()
        )
        scheduleDailyReminder()

    }

    // This is the first version of scheduleDailyReminder, which is commented out.
    // It was intended to schedule a daily reminder at a specific time (e.g., 8 PM or 9:50 PM).
//    private fun scheduleDailyReminder() {
//        val workManager = WorkManager.getInstance(applicationContext)
//
//        // Calculate initial delay to 8 PM
//        val currentTime = Calendar.getInstance()
//        val dueTime = Calendar.getInstance().apply {
//            set(Calendar.HOUR_OF_DAY, 21) // 8 PM
//            set(Calendar.MINUTE,50)
//            set(Calendar.SECOND, 0)
//        }
//
//        if (dueTime.before(currentTime)) {
//            dueTime.add(Calendar.HOUR_OF_DAY, 24) // If it's past 8 PM, schedule for tomorrow
//        }
//
//        val initialDelay = dueTime.timeInMillis - currentTime.timeInMillis
//
//        val dailyReminderRequest =
//            PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
//                .setInitialDelay(0, TimeUnit.MILLISECONDS)
//                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
//                .addTag("daily_reminder_tag") // Optional: add a tag to identify the work
//                .build()
//
//        workManager.enqueueUniquePeriodicWork(
//            "FitTrackDailyReminder", // A unique name for the work
//            ExistingPeriodicWorkPolicy.KEEP, // Or REPLACE if you want to update the worker
//            dailyReminderRequest
//        )
//    }


    /**
     * Schedules a periodic reminder using WorkManager.
     * This current implementation schedules a test reminder that triggers every 15 minutes.
     * The original implementation for a daily reminder at a specific time is commented out above.
     */
    private fun scheduleDailyReminder() {
        val workManager = WorkManager.getInstance(applicationContext)

        // Create a periodic work request for the ReminderWorker.
        // This request is configured to repeat every 15 minutes for testing purposes.
        val testReminderRequest =
            PeriodicWorkRequestBuilder<ReminderWorker>(15, TimeUnit.MINUTES)
                .setInitialDelay(0, TimeUnit.MILLISECONDS) // 立即开始
                .addTag("test_reminder_tag")
                .build()

        workManager.enqueueUniquePeriodicWork(
            "FitTrackTestReminder",
            ExistingPeriodicWorkPolicy.REPLACE, // Replace any existing test
            testReminderRequest
        )
    }
}

