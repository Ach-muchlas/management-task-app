package com.am.schedulingapp.data.model

data class Task(
    val id : String,
    val title: String,
    val subject: String,
    val description: String,
    val startTime: Long,
    val endTime: Long,
    val date: Long,
    val reminderMinutesBefore: Long,
    val status: TaskStatus = TaskStatus.PENDING,
    val fileUrls: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class TaskStatus {
    PENDING,
    ON_PROGRESS,
    DONE
}
