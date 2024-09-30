package com.am.schedulingapp.data.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Task(
    val id: String = "",
    val title: String = "",
    val subject: String = "",
    val description: String = "",
    val startTime: Date = Date(),
    val endTime: Date = Date(),
    val date: Date = Date(),
    val reminder: Int = 0,
    val status: TaskStatus = TaskStatus.PENDING,
    val fileUrls: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) : Parcelable

enum class TaskStatus {
    PENDING,
    ON_PROGRESS,
    DONE
}
