package com.example.studylinksa

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import java.util.concurrent.TimeUnit

class AcademicSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val client = SupabaseClientProvider.client

    private val dao
        get() = StudyLinkApp.instance.database.academicDao()

    override suspend fun doWork(): Result {

        return try {

            // Identify the currently logged-in user.
            val userId = client.auth.currentUserOrNull()?.id
                ?: return Result.retry()

            // Retrieve all records waiting for synchronisation.
            val pendingRecords =
                dao.getPendingSyncRecords(userId)

            for (record in pendingRecords) {

                if (record.isDeleted) {

                    // Delete the record from Supabase.
                    client.from(record.tableName).delete {
                        filter {
                            eq("id", record.recordId)
                            eq("user_id", userId)
                        }
                    }

                    // Remove the local record after successful deletion.
                    dao.deleteRecord(
                        userId,
                        record.tableName,
                        record.recordId
                    )

                } else {

                    // Convert the saved JSON back into a JSON object.
                    val jsonRecord = Json
                        .parseToJsonElement(record.dataJson)
                        .jsonObject

                    // Insert a new record or update an existing record.
                    client.from(record.tableName).upsert(jsonRecord) {
                        onConflict = "id"
                    }

                    // Mark only the uploaded version as synchronised.
                    dao.markAsSynced(
                        userId,
                        record.tableName,
                        record.recordId,
                        record.updatedAt
                    )
                }
            }

            Result.success()

        } catch (e: Exception) {

            // Retry later if any operation fails.
            Result.retry()
        }
    }

    companion object {

        private const val SYNC_WORK_NAME =
            "studylink_academic_sync"

        fun enqueue(context: Context) {

            // Only run when a network connection is available.
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncRequest =
                OneTimeWorkRequestBuilder<AcademicSyncWorker>()
                    .setConstraints(constraints)
                    .setBackoffCriteria(
                        BackoffPolicy.EXPONENTIAL,
                        30,
                        TimeUnit.SECONDS
                    )
                    .build()

            // Append new requests so changes made during an active
            // synchronisation are not forgotten.
            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    SYNC_WORK_NAME,
                    ExistingWorkPolicy.APPEND_OR_REPLACE,
                    syncRequest
                )
        }
    }
}