package com.example.studylinksa

import androidx.work.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import java.util.concurrent.TimeUnit

object NotificationScheduler {

    private fun scheduleOnce(
        key: String,
        title: String,
        message: String,
        target: LocalDateTime
    ) {

        val delay = java.time.Duration.between(
            LocalDateTime.now(),
            target
        ).toMillis()

        if (delay <= 0) return

        val id = key.hashCode() and 0x7fffffff

        val data = workDataOf(
            "title" to title,
            "message" to message,
            "notification_id" to id
        )

        val request =
            OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInputData(data)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build()

        WorkManager.getInstance(
            StudyLinkApp.instance
        ).enqueueUniqueWork(
            key,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    fun cancelRecord(table: String, recordId: String) {

        val workManager =
            WorkManager.getInstance(StudyLinkApp.instance)

        val prefixes = listOf("reminder1", "reminder2", "class")

        prefixes.forEach { prefix ->
            workManager.cancelUniqueWork(
                "${prefix}_${table}_$recordId"
            )
        }
    }

    fun scheduleForTable(
        table: String,
        records: List<JsonObject>
    ) {

        records.forEach { record ->

            val id = record["id"]
                ?.jsonPrimitive
                ?.contentOrNull
                ?: return@forEach

            cancelRecord(table, id)

            when (table) {

                "assignments" -> {

                    val completed = record["completed"]
                        ?.jsonPrimitive
                        ?.contentOrNull
                        ?.toBoolean() ?: false

                    if (completed) return@forEach

                    val title = record["title"]
                        ?.jsonPrimitive
                        ?.contentOrNull
                        ?: "Assignment"

                    val dueDate = record["due_date"]
                        ?.jsonPrimitive
                        ?.contentOrNull
                        ?: return@forEach

                    val date = runCatching {
                        LocalDate.parse(dueDate.take(10))
                    }.getOrNull() ?: return@forEach

                    scheduleOnce(
                        "reminder1_${table}_$id",
                        "Assignment due tomorrow",
                        "$title is approaching its due date.",
                        date.minusDays(1).atTime(9, 0)
                    )

                    scheduleOnce(
                        "reminder2_${table}_$id",
                        "Assignment due today",
                        "$title is due today. Remember to submit it.",
                        date.atTime(9, 0)
                    )
                }

                "exams" -> {

                    val name = record["name"]
                        ?.jsonPrimitive
                        ?.contentOrNull
                        ?: "Examination"

                    val examDate = record["exam_date"]
                        ?.jsonPrimitive
                        ?.contentOrNull
                        ?: return@forEach

                    val examTime = record["exam_time"]
                        ?.jsonPrimitive
                        ?.contentOrNull
                        ?: return@forEach

                    val date = runCatching {
                        LocalDate.parse(examDate.take(10))
                    }.getOrNull() ?: return@forEach

                    val time = runCatching {
                        LocalTime.parse(examTime.take(5))
                    }.getOrNull() ?: return@forEach

                    val examDateTime = date.atTime(time)

                    scheduleOnce(
                        "reminder1_${table}_$id",
                        "Examination tomorrow",
                        "$name is scheduled for tomorrow.",
                        examDateTime.minusHours(24)
                    )

                    scheduleOnce(
                        "reminder2_${table}_$id",
                        "Examination in one hour",
                        "$name starts in approximately one hour.",
                        examDateTime.minusHours(1)
                    )
                }

                "timetable_classes" -> {

                    val subject = record["subject"]
                        ?.jsonPrimitive
                        ?.contentOrNull
                        ?: "Class"

                    val day = record["day"]
                        ?.jsonPrimitive
                        ?.contentOrNull
                        ?: return@forEach

                    val startTime = record["start_time"]
                        ?.jsonPrimitive
                        ?.contentOrNull
                        ?: return@forEach

                    val dayOfWeek = runCatching {
                        when (day.trim().lowercase(Locale.ROOT)) {
                            "mon", "monday" -> DayOfWeek.MONDAY
                            "tue", "tuesday" -> DayOfWeek.TUESDAY
                            "wed", "wednesday" -> DayOfWeek.WEDNESDAY
                            "thu", "thursday" -> DayOfWeek.THURSDAY
                            "fri", "friday" -> DayOfWeek.FRIDAY
                            "sat", "saturday" -> DayOfWeek.SATURDAY
                            "sun", "sunday" -> DayOfWeek.SUNDAY
                            else -> throw IllegalArgumentException()
                        }
                    }.getOrNull() ?: return@forEach

                    val time = runCatching {
                        LocalTime.parse(startTime.take(5))
                    }.getOrNull() ?: return@forEach

                    var nextClass = LocalDate.now()
                        .with(
                            TemporalAdjusters.nextOrSame(dayOfWeek)
                        )
                        .atTime(time)
                        .minusMinutes(15)

                    if (!nextClass.isAfter(LocalDateTime.now())) {
                        nextClass = nextClass.plusWeeks(1)
                    }

                    val delay = java.time.Duration.between(
                        LocalDateTime.now(),
                        nextClass
                    ).toMillis()

                    val data = workDataOf(
                        "title" to "Upcoming class",
                        "message" to "$subject starts in 15 minutes.",
                        "notification_id" to
                                ("class_$id".hashCode() and 0x7fffffff)
                    )

                    val request =
                        PeriodicWorkRequestBuilder<ReminderWorker>(
                            7, TimeUnit.DAYS
                        )
                            .setInputData(data)
                            .setInitialDelay(
                                delay,
                                TimeUnit.MILLISECONDS
                            )
                            .build()

                    WorkManager.getInstance(
                        StudyLinkApp.instance
                    ).enqueueUniquePeriodicWork(
                        "class_${table}_$id",
                        ExistingPeriodicWorkPolicy.REPLACE,
                        request
                    )
                }
            }
        }
    }
}