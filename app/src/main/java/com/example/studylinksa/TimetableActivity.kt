package com.example.studylinksa

class TimetableActivity : AcademicScreenActivity() {

    override val tableName = "timetable_classes"

    override val screenTitle = "My Timetable"

    override val fields = listOf(
        AcademicField("subject", "Subject", true),
        AcademicField("day", "Day", true, "e.g. Monday"),
        AcademicField("start_time", "Start Time", true, "e.g. 08:00"),
        AcademicField("end_time", "End Time", true, "e.g. 10:00"),
        AcademicField("venue", "Venue", false)
    )
}