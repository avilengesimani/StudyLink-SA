package com.example.studylinksa

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import java.util.UUID

object AcademicRepository {

    private val client = SupabaseClientProvider.client

    private val dao
        get() = StudyLinkApp.instance.database.academicDao()

    private fun getUserId(): String {
        return client.auth.currentUserOrNull()?.id
            ?: throw Exception(
                "You are not logged in. Please log in again."
            )
    }

    private fun isOnline(): Boolean {

        val connectivityManager =
            StudyLinkApp.instance.getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager

        val network = connectivityManager.activeNetwork
            ?: return false

        val capabilities =
            connectivityManager.getNetworkCapabilities(network)
                ?: return false

        return capabilities.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_INTERNET
        )
    }

    private fun toJson(
        record: AcademicRecordEntity
    ): JsonObject {
        return Json
            .parseToJsonElement(record.dataJson)
            .jsonObject
    }

    // Schedule background synchronisation.
    private fun scheduleSync() {
        AcademicSyncWorker.enqueue(StudyLinkApp.instance)
    }

    // Refresh reminders using the locally stored records.
    private suspend fun refreshReminders(
        table: String,
        userId: String
    ) {

        val records = dao.getRecords(userId, table).map {
            toJson(it)
        }

        NotificationScheduler.scheduleForTable(
            table,
            records
        )
    }

    // Retrieve records from Room and refresh from Supabase.
    suspend fun getRecords(table: String): List<JsonObject> {

        val userId = getUserId()

        if (isOnline()) {
            try {

                val remoteRecords = client
                    .from(table)
                    .select {
                        filter {
                            eq("user_id", userId)
                        }
                    }
                    .decodeList<JsonObject>()

                // Remove old synchronised cache entries.
                dao.clearSyncedRecords(userId, table)

                remoteRecords.forEach { record ->

                    val recordId = record["id"]
                        ?.jsonPrimitive
                        ?.contentOrNull
                        ?: return@forEach

                    val localRecord = dao.getRecord(
                        userId,
                        table,
                        recordId
                    )

                    // Preserve pending local changes.
                    if (localRecord?.needsSync == true) {
                        return@forEach
                    }

                    dao.insertRecord(
                        AcademicRecordEntity(
                            tableName = table,
                            recordId = recordId,
                            userId = userId,
                            dataJson = record.toString(),
                            isDeleted = false,
                            needsSync = false
                        )
                    )
                }

            } catch (e: Exception) {
                // Use local records if Supabase is unavailable.
            }
        }

        val records = dao.getRecords(userId, table).map {
            toJson(it)
        }

        NotificationScheduler.scheduleForTable(
            table,
            records
        )

        return records
    }

    // Create a record locally and queue it for upload.
    suspend fun addRecord(
        table: String,
        values: Map<String, JsonElement>
    ) {

        val userId = getUserId()
        val recordId = UUID.randomUUID().toString()

        val record = buildJsonObject {

            put("id", JsonPrimitive(recordId))
            put("user_id", JsonPrimitive(userId))

            values.forEach { (key, value) ->

                if (key != "id" && key != "user_id") {
                    put(key, value)
                }
            }
        }

        dao.insertRecord(
            AcademicRecordEntity(
                tableName = table,
                recordId = recordId,
                userId = userId,
                dataJson = record.toString(),
                isDeleted = false,
                needsSync = true
            )
        )

        scheduleSync()

        // Schedule any applicable academic reminders.
        refreshReminders(table, userId)
    }

    // Update locally and queue the latest version for upload.
    suspend fun updateRecord(
        table: String,
        recordId: String,
        values: Map<String, JsonElement>
    ) {

        val userId = getUserId()

        val existing = dao.getRecord(
            userId,
            table,
            recordId
        ) ?: throw Exception(
            "Record not found locally. Please refresh while online."
        )

        if (existing.isDeleted) {
            throw Exception("This record has already been deleted.")
        }

        val updatedRecord = buildJsonObject {

            val oldData = toJson(existing)

            oldData.forEach { (key, value) ->
                put(key, value)
            }

            values.forEach { (key, value) ->

                if (key != "id" && key != "user_id") {
                    put(key, value)
                }
            }
        }

        dao.insertRecord(
            existing.copy(
                dataJson = updatedRecord.toString(),
                isDeleted = false,
                needsSync = true,
                updatedAt = System.currentTimeMillis()
            )
        )

        scheduleSync()

        // Update reminders after the record changes.
        refreshReminders(table, userId)
    }

    // Mark for deletion locally and queue cloud deletion.
    suspend fun deleteRecord(
        table: String,
        recordId: String
    ) {

        val userId = getUserId()

        val existing = dao.getRecord(
            userId,
            table,
            recordId
        ) ?: throw Exception(
            "Record not found locally. Please refresh while online."
        )

        if (existing.isDeleted) {
            return
        }

        dao.markAsDeleted(
            userId,
            table,
            recordId,
            System.currentTimeMillis()
        )

        // Cancel reminders for the deleted record.
        NotificationScheduler.cancelRecord(
            table,
            recordId
        )

        scheduleSync()
    }
}