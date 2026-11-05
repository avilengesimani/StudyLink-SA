package com.example.studylinksa

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AcademicDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: AcademicRecordEntity)

    @Query("""
        SELECT * FROM academic_records
        WHERE userId = :userId
        AND tableName = :tableName
        AND recordId = :recordId
        LIMIT 1
    """)
    suspend fun getRecord(
        userId: String,
        tableName: String,
        recordId: String
    ): AcademicRecordEntity?

    @Query("""
        SELECT * FROM academic_records
        WHERE userId = :userId
        AND tableName = :tableName
        AND isDeleted = 0
        ORDER BY updatedAt DESC
    """)
    suspend fun getRecords(
        userId: String,
        tableName: String
    ): List<AcademicRecordEntity>

    @Query("""
        SELECT * FROM academic_records
        WHERE userId = :userId
        AND needsSync = 1
    """)
    suspend fun getPendingSyncRecords(
        userId: String
    ): List<AcademicRecordEntity>

    // Only mark the exact version that was uploaded as synced.
    // This protects a newer edit made while the upload was running.
    @Query("""
        UPDATE academic_records
        SET needsSync = 0
        WHERE userId = :userId
        AND tableName = :tableName
        AND recordId = :recordId
        AND updatedAt = :updatedAt
    """)
    suspend fun markAsSynced(
        userId: String,
        tableName: String,
        recordId: String,
        updatedAt: Long
    )

    @Query("""
        UPDATE academic_records
        SET isDeleted = 1,
            needsSync = 1,
            updatedAt = :updatedAt
        WHERE userId = :userId
        AND tableName = :tableName
        AND recordId = :recordId
    """)
    suspend fun markAsDeleted(
        userId: String,
        tableName: String,
        recordId: String,
        updatedAt: Long
    )

    @Query("""
        DELETE FROM academic_records
        WHERE userId = :userId
        AND tableName = :tableName
        AND recordId = :recordId
    """)
    suspend fun deleteRecord(
        userId: String,
        tableName: String,
        recordId: String
    )

    @Query("""
        DELETE FROM academic_records
        WHERE userId = :userId
        AND tableName = :tableName
        AND needsSync = 0
    """)
    suspend fun clearSyncedRecords(
        userId: String,
        tableName: String
    )
}