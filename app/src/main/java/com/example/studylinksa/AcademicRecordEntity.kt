package com.example.studylinksa

import androidx.room.Entity

@Entity(
    tableName = "academic_records",
    primaryKeys = ["tableName", "recordId"]
)
data class AcademicRecordEntity(

    // Supabase table, for example assignments or exams.
    val tableName: String,

    // Unique ID of the academic record.
    val recordId: String,

    // ID of the user who owns the record.
    val userId: String,

    // Complete record stored as JSON.
    val dataJson: String,

    // Indicates whether the record was deleted locally.
    val isDeleted: Boolean = false,

    // Indicates whether the record needs synchronisation.
    val needsSync: Boolean = false,

    // Last local modification time.
    val updatedAt: Long = System.currentTimeMillis()
)