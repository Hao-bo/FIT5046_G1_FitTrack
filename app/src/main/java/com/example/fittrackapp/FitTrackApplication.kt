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

    /**
     * Schedules a daily reminder using WorkManager to trigger at a specific time (e.g., 9:50 PM).
     * This function calculates the initial delay to the next occurrence of 9:50 PM
     * and then sets up a periodic worker to run every 24 hours.
     */
    private fun scheduleDailyReminder() {
        val workManager = WorkManager.getInstance(applicationContext)

        // Calculate initial delay to
        val currentTime = Calendar.getInstance()

        // Define the target time for the daily reminder
        val dueTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 20)
            set(Calendar.MINUTE,45)
            set(Calendar.SECOND, 0)
        }

        // Check if the calculated due time has already passed for today.
        // If it has, add 24 hours to schedule it for the next day.
        if (dueTime.before(currentTime)) {
            dueTime.add(Calendar.HOUR_OF_DAY, 24)
        }

        // Calculate the initial delay in milliseconds until the first reminder should trigger.
        // This is the difference between the target due time and the current time.
        val initialDelay = dueTime.timeInMillis - currentTime.timeInMillis

        // Build a periodic work request for the ReminderWorker.
        // This request will repeat every 24 hours.
        val dailyReminderRequest =
            PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .addTag("daily_reminder_tag") // Optional: add a tag to identify the work
                .build()

        workManager.enqueueUniquePeriodicWork(
            "FitTrackDailyReminder", // A unique name for the work
            ExistingPeriodicWorkPolicy.KEEP, // Or REPLACE if you want to update the worker
            dailyReminderRequest
        )
    }


    /**
     * Schedules a periodic reminder using WorkManager.
     * This current implementation schedules a test reminder that triggers every 15 minutes.
     * The original implementation for a daily reminder at a specific time is commented out above.
     */
//    private fun scheduleDailyReminder() {
//        val workManager = WorkManager.getInstance(applicationContext)
//
//        // Create a periodic work request for the ReminderWorker.
//        // This request is configured to repeat every 15 minutes for testing purposes.
//        val testReminderRequest =
//            PeriodicWorkRequestBuilder<ReminderWorker>(15, TimeUnit.MINUTES)
//                .setInitialDelay(0, TimeUnit.MILLISECONDS) // 立即开始
//                .addTag("test_reminder_tag")
//                .build()
//
//        workManager.enqueueUniquePeriodicWork(
//            "FitTrackTestReminder",
//            ExistingPeriodicWorkPolicy.REPLACE, // Replace any existing test
//            testReminderRequest
//        )
//    }
}

