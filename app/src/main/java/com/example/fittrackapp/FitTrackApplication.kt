package com.example.fittrackapp

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.fittrackapp.Graph.authViewModel
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.MapboxNavigationProvider
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp
import java.util.Calendar
import java.util.concurrent.TimeUnit

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
        scheduleDailyReminder()

    }

    private fun scheduleDailyReminder() {
        val workManager = WorkManager.getInstance(applicationContext)

        // Calculate initial delay to 8 PM
        val currentTime = Calendar.getInstance()
        val dueTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 22) // 8 PM
            set(Calendar.MINUTE, 30)
            set(Calendar.SECOND, 0)
        }

        if (dueTime.before(currentTime)) {
            dueTime.add(Calendar.HOUR_OF_DAY, 24) // If it's past 8 PM, schedule for tomorrow
        }

        val initialDelay = dueTime.timeInMillis - currentTime.timeInMillis

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
}

