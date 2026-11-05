package com.example.studylinksa

import android.app.Application

class StudyLinkApp : Application() {

    // Shared local database instance.
    val database: StudyLinkDatabase by lazy {
        StudyLinkDatabase.getDatabase(this)
    }

    companion object {
        lateinit var instance: StudyLinkApp
            private set
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
    }
}