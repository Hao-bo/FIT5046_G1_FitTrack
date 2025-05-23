package com.example.fittrackapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

/**
 * A WorkManager Worker responsible for sending daily workout reminders.
 * This worker is scheduled to run periodically (e.g., daily or for testing, every 15 minutes)
 * and creates a system notification to remind the user to log their workout.
 *
 * @param appContext The application context.
 * @param workerParams Parameters for the worker.
 */
class ReminderWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    // Companion object to hold constants related to notifications.
    companion object {
        const val CHANNEL_ID = "FitTrackReminderChannel"
        const val NOTIFICATION_ID = 1
    }

    /**
     * The main work to be performed by this worker.
     * This method is called on a background thread.
     * @return Result.success() if the work finished successfully, Result.failure() otherwise.
     */
    override fun doWork(): Result {
        sendNotification()
        return Result.success()
    }

    /**
     * Creates and displays a system notification to remind the user.
     */
    private fun sendNotification() {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create a notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "FitTrack Reminder",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for FitTrack workout reminders"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Intent to open MainActivity when notification is tapped
        val intent = Intent(applicationContext, MainActivity::class.java).apply { //
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        // Create a PendingIntent for the notification's content action.
        // FLAG_IMMUTABLE is required for Android S (API 31) and above if the PendingIntent is not mutable.
        // FLAG_UPDATE_CURRENT ensures that if the PendingIntent already exists, it's updated with the new intent.
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Build the notification
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo) // Replace with your app's icon
            .setContentTitle("FitTrack Reminder")
            .setContentText("Don't forget to log your workout today! 💪")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // Dismiss notification when tapped
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}