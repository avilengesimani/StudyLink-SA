package com.example.studylinksa

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {

        val title = inputData.getString("title")
            ?: "StudyLink SA Reminder"

        val message = inputData.getString("message")
            ?: "You have an upcoming academic activity."

        val id = inputData.getInt("notification_id", 1001)

        NotificationHelper.showNotification(
            applicationContext,
            title,
            message,
            id
        )

        return Result.success()
    }
}