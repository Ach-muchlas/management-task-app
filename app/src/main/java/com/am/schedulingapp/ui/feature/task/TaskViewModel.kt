package com.am.schedulingapp.ui.feature.task

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.am.schedulingapp.data.model.Task
import com.am.schedulingapp.data.model.TaskStatus
import java.util.Date

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {
    fun getTaskByDate(selectedDate: Date) = repository.getTaskByDate(selectedDate)
    fun addTask(task: Task, fileUri: Uri? = null) = repository.addTask(task, fileUri)
    fun updateTask(task: Task, fileUri: Uri? = null) = repository.updateTask(task, fileUri)

    fun getTaskByStatus(status: TaskStatus) = repository.getTaskByStatus(status)
}