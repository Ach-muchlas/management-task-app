package com.am.schedulingapp.ui.feature.task

import androidx.lifecycle.ViewModel
import com.am.schedulingapp.data.model.Task

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {
    fun addTask(task: Task) = repository.addTask(task)
}