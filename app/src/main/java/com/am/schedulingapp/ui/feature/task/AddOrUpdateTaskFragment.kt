package com.am.schedulingapp.ui.feature.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.am.schedulingapp.data.model.Task
import com.am.schedulingapp.data.model.TaskStatus
import com.am.schedulingapp.databinding.FragmentAddOrUpdateTaskBinding
import com.am.schedulingapp.service.source.Status
import com.am.schedulingapp.utils.NotificationHandling.showErrorMessage
import com.am.schedulingapp.utils.NotificationHandling.showSuccess
import org.koin.android.ext.android.inject

class AddOrUpdateTaskFragment : Fragment() {
    private var _binding: FragmentAddOrUpdateTaskBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TaskViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddOrUpdateTaskBinding.inflate(inflater, container, false)
        setupNavigation()
        return binding.root
    }

    private fun setupNavigation() {
        binding.btnSave.setOnClickListener { setupAddTask() }
    }

    private fun setupAddTask() {
        val dummyTask = Task(
            id = "", // ID akan dihasilkan oleh Firebase setelah berhasil ditambahkan
            title = "Dummy Task Title",
            subject = "Dummy Task Subject",
            description = "This is a description for a dummy task.",
            startTime = System.currentTimeMillis(), // Menggunakan waktu saat ini
            endTime = System.currentTimeMillis() + 3600000, // Satu jam setelah startTime
            date = System.currentTimeMillis(), // Hari ini
            reminderMinutesBefore = 30, // 30 menit sebelum tugas dimulai
            status = TaskStatus.PENDING, // Status awal
            fileUrls = "", // Kosongkan file URLs untuk dummy data
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        viewModel.addTask(task = dummyTask).observe(viewLifecycleOwner) { result ->
            when (result.status) {
                Status.LOADING -> {}
                Status.SUCCESS -> {
                    val addedTask = result.data
                    showSuccess(
                        requireView(),
                        "Task Added : ${addedTask?.title}"
                    )
                    findNavController().popBackStack()
                }

                Status.ERROR -> {
                    showErrorMessage(requireView(), result.message.toString())
                }
            }
        }

    }
}