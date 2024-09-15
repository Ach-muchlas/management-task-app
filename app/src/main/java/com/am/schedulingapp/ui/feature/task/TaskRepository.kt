package com.am.schedulingapp.ui.feature.task

import android.net.Uri
import androidx.lifecycle.liveData
import com.am.schedulingapp.data.model.Task
import com.am.schedulingapp.data.model.TaskStatus
import com.am.schedulingapp.service.source.Result
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import java.util.UUID

class TaskRepository(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) {
    private val tasksCollection = firestore.collection("tasks")

    fun addTask(task: Task) = liveData(Dispatchers.IO) {
        emit(Result.loading(null))
        try {
            val newTask = task.copy(id = tasksCollection.document().id)
            tasksCollection.document(newTask.id).set(newTask).await()
            emit(Result.success(newTask))
        } catch (e: Exception) {
            emit(Result.error(null, e.message ?: "Failed to add task"))
        }
    }

    fun updateTask(task: Task) = liveData(Dispatchers.IO) {
        emit(Result.loading(null))
        try {
            tasksCollection.document(task.id).set(task).await()
            emit(Result.success(task))
        } catch (e: Exception) {
            emit(Result.error(null, e.message ?: "Failed to update task"))
        }
    }

    fun getTask(taskId: String) = liveData(Dispatchers.IO) {
        emit(Result.loading(null))
        try {
            val snapshot = tasksCollection.document(taskId).get().await()
            val task = snapshot.toObject(Task::class.java)
            if (task != null) {
                emit(Result.success(task))
            } else {
                emit(Result.error(null, "Task not found"))
            }
        } catch (e: Exception) {
            emit(Result.error(null, e.message ?: "Failed to get task"))
        }
    }

    fun getTasks() = liveData(Dispatchers.IO) {
        emit(Result.loading(null))
        try {
            val snapshot = tasksCollection.get().await()
            val tasks = snapshot.toObjects(Task::class.java)
            emit(Result.success(tasks))
        } catch (e: Exception) {
            emit(Result.error(null, e.message ?: "Failed to get tasks"))
        }
    }

    fun deleteTask(taskId: String) = liveData(Dispatchers.IO) {
        emit(Result.loading(null))
        try {
            tasksCollection.document(taskId).delete().await()
            emit(Result.success(true))
        } catch (e: Exception) {
            emit(Result.error(null, e.message ?: "Failed to delete task"))
        }
    }

    fun uploadFile(uri: Uri) = liveData(Dispatchers.IO) {
        emit(Result.loading(null))
        try {
            val filename = "${UUID.randomUUID()}"
            val ref = storage.reference.child("task_files/$filename")
            val uploadTask = ref.putFile(uri).await()
            val downloadUrl = uploadTask.storage.downloadUrl.await()
            emit(Result.success(downloadUrl.toString()))
        } catch (e: Exception) {
            emit(Result.error(null, e.message ?: "Failed to upload file"))
        }
    }

    fun updateTaskStatus(taskId: String, newStatus: TaskStatus) = liveData(Dispatchers.IO) {
        emit(Result.loading(null))
        try {
            val taskRef = tasksCollection.document(taskId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(taskRef)
                val task = snapshot.toObject(Task::class.java)
                if (task != null) {
                    val updatedTask =
                        task.copy(status = newStatus, updatedAt = System.currentTimeMillis())
                    transaction.set(taskRef, updatedTask)
                    updatedTask
                } else {
                    throw Exception("Task not found")
                }
            }.await()
            val updatedTask = taskRef.get().await().toObject(Task::class.java)
            if (updatedTask != null) {
                emit(Result.success(updatedTask))
            } else {
                emit(Result.error(null, "Task not found after update"))
            }
        } catch (e: Exception) {
            emit(Result.error(null, e.message ?: "Failed to update task status"))
        }
    }
}