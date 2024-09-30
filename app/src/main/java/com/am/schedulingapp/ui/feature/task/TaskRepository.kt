package com.am.schedulingapp.ui.feature.task

import android.net.Uri
import android.util.Log
import androidx.lifecycle.liveData
import com.am.schedulingapp.data.model.Task
import com.am.schedulingapp.data.model.TaskStatus
import com.am.schedulingapp.service.source.Result
import com.am.schedulingapp.utils.DateFormatter.isSameDay
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID

class TaskRepository(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) {
    private val tasksCollection = firestore.collection("tasks")

    fun addTask(task: Task, fileUri: Uri? = null) = liveData(Dispatchers.IO) {
        emit(Result.loading(null))
        try {
            val newTask = if (fileUri != null) {
                val filename = "${UUID.randomUUID()}"
                val ref = storage.reference.child("task_files/$filename")
                val uploadTask = ref.putFile(fileUri).await()
                val downloadUrl = uploadTask.storage.downloadUrl.await()
                task.copy(
                    id = task.id.ifEmpty { tasksCollection.document().id },
                    fileUrls = downloadUrl.toString()
                )
            } else {
                task.copy(id = task.id.ifEmpty { tasksCollection.document().id })
            }

            tasksCollection.document(newTask.id).set(newTask).await()
            emit(Result.success(newTask))
        } catch (e: Exception) {
            emit(Result.error(null, e.message ?: "Failed to add task"))
        }
    }

    fun updateTask(task: Task, fileUri: Uri? = null) = liveData(Dispatchers.IO) {
        emit(Result.loading(null))
        try {
            val updatedTask = if (fileUri != null) {
                val filename = "${UUID.randomUUID()}"
                val ref = storage.reference.child("task_files/$filename")
                val uploadTask = ref.putFile(fileUri).await()
                val downloadUrl = uploadTask.storage.downloadUrl.await()
                task.copy(fileUrls = downloadUrl.toString(), updatedAt = Date())
            } else {
                task.copy(updatedAt = Date())
            }

            tasksCollection.document(updatedTask.id).set(updatedTask).await()
            emit(Result.success(updatedTask))
        } catch (e: Exception) {
            emit(Result.error(null, e.message ?: "Failed to update task"))
        }
    }

    fun getTaskByDate(selectDate : Date) = liveData(Dispatchers.IO)
    {
        emit(Result.loading(null))
        try {
            val snapshot = tasksCollection.get().await()
            val tasks = snapshot.toObjects(Task::class.java)
            val filteredTasks = tasks.filter { task ->
                isSameDay(task.date, selectDate)
            }
            emit(Result.success(filteredTasks))
        } catch (e: Exception) {
            emit(Result.error(null, e.message ?: "Failed to get tasks"))
        }
    }

    fun getTaskByStatus(status: TaskStatus) = liveData(Dispatchers.IO) {
        emit(Result.loading(null))
        try {
            val snapshot = tasksCollection
                .whereEqualTo("status", status.name)
                .orderBy("updatedAt", Query.Direction.DESCENDING)
                .limit(3)
                .get()
                .await()

            Log.d("TaskRepository", "Query completed. Documents count: ${snapshot.size()}")

            val filterTask = snapshot.toObjects(Task::class.java)
            emit(Result.success(filterTask))
        } catch (e: Exception) {
            emit(Result.error(null, e.message ?: "Failed to get tasks"))
        }
    }
}