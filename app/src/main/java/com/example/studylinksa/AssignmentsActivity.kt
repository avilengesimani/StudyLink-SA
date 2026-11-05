package com.example.studylinksa

class AssignmentsActivity : AcademicScreenActivity() {

    override val tableName = "assignments"

    override val screenTitle = "My Assignments"

    override val fields = listOf(
        AcademicField("title", "Assignment Title", true),
        AcademicField("description", "Description"),
        AcademicField("subject", "Subject", true),
        AcademicField("due_date", "Due Date", false, "YYYY-MM-DD"),
        AcademicField("priority", "Priority", false, "Low, Medium or High")
    )

    override val defaultValues = mapOf(
        "priority" to "Medium",
        "completed" to "false"
    )

    override val supportsCompletion = true
}