package com.am.schedulingapp.ui.feature.task

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.am.schedulingapp.data.model.Task
import java.util.Date

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {
    fun getTask() = repository.getTasks()
    fun getTaskByDate(selectedDate: Date) = repository.getTaskByDate(selectedDate)
    fun addTask(task: Task) = repository.addTask(task)
    fun uploadFileAndAddTask(task: Task, fileUrls: Uri) =
        repository.uploadFileAndAddTask(task, fileUrls)
}