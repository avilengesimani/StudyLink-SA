package com.example.studylinksa

class ExamsActivity : AcademicScreenActivity() {

    override val tableName = "exams"

    override val screenTitle = "My Examinations"

    override val fields = listOf(
        AcademicField("name", "Examination Name", true),
        AcademicField("subject", "Subject", true),
        AcademicField("exam_date", "Examination Date", true, "YYYY-MM-DD"),
        AcademicField("exam_time", "Examination Time", false, "e.g. 09:00"),
        AcademicField("venue", "Venue"),
        AcademicField("notes", "Additional Notes")
    )
}