package com.example.studylinksa

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [AcademicRecordEntity::class],
    version = 1,
    exportSchema = false
)
abstract class StudyLinkDatabase : RoomDatabase() {

    abstract fun academicDao(): AcademicDao

    companion object {

        @Volatile
        private var INSTANCE: StudyLinkDatabase? = null

        fun getDatabase(context: Context): StudyLinkDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StudyLinkDatabase::class.java,
                    "studylink_database"
                ).build()

                INSTANCE = instance

                instance
            }
        }
    }
}