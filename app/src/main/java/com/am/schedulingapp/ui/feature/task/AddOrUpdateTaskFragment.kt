package com.am.schedulingapp.ui.feature.task

import android.R
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.am.schedulingapp.data.model.Task
import com.am.schedulingapp.data.model.TaskStatus
import com.am.schedulingapp.databinding.FragmentAddOrUpdateTaskBinding
import com.am.schedulingapp.service.source.Result
import com.am.schedulingapp.service.source.Status
import com.am.schedulingapp.ui.adapter.CalenderAdapter
import com.am.schedulingapp.utils.DateFormatter
import com.am.schedulingapp.utils.DateUtils.generateDatesForCurrentMonth
import com.am.schedulingapp.utils.HandlingUi
import com.am.schedulingapp.utils.HandlingUi.generateTimeList
import com.am.schedulingapp.utils.NotificationHandling.showErrorMessage
import com.am.schedulingapp.utils.NotificationHandling.showSuccess
import com.am.schedulingapp.utils.ProgressHandling.showProgressbar
import com.google.android.material.tabs.TabLayout
import org.koin.android.ext.android.inject
import java.util.Date

class AddOrUpdateTaskFragment : Fragment() {
    private var _binding: FragmentAddOrUpdateTaskBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TaskViewModel by inject()
    private lateinit var calenderAdapter: CalenderAdapter
    private var selectedStartTime: Date? = null
    private var selectedEndTime: Date? = null
    private var selectedFileUri: Uri? = null
    private var selectedDate: Date? = null
    private var selectedStatus: TaskStatus = TaskStatus.PENDING

    private val task: Task? by lazy { arguments?.getParcelable(TaskFragment.KEY_BUNDLE_ID) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddOrUpdateTaskBinding.inflate(inflater, container, false)
        Log.e(TAG, "data id : $task")
        setupView()
        setupCalenderAdapter()
        setupNavigation()
        setupTabLayoutStatus()
        return binding.root
    }


    private fun setupNavigation() {
        binding.btnSave.setOnClickListener {
            if (task != null) {
                updateTask()
            } else {
                setupAddTask()
            }
        }
        binding.cardUploadFile.setOnClickListener { openFilePicker() }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, REQUEST_CODE_PICK_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_FILE && resultCode == Activity.RESULT_OK) {
            selectedFileUri = data?.data
        }
    }

    private fun setupView() {
        val times = generateTimeList(8, 18, 30)
        HandlingUi.setupVisibilityBottomNavigation(activity, true)
        HandlingUi.setupDisableHintForField(
            binding.edlTitleTask,
            binding.edlSubjectTask,
            binding.edlDescriptionTask,
            binding.edlStartTimeTask,
            binding.edlEndTimeTask,
            binding.edlReminderTask
        )
        setupDropdown(binding.dropdownStartTime, times) { time ->
            selectedStartTime = DateFormatter.parseTime(time)
        }
        setupDropdown(binding.dropdownEndTime, times) { time ->
            selectedEndTime = DateFormatter.parseTime(time)
        }
        task?.let { existingTask ->
            binding.edtTitleTask.setText(existingTask.title)
            binding.edtSubjectTask.setText(existingTask.subject)
            binding.edtDescriptionTask.setText(existingTask.description)
            binding.edtReminderTask.setText(existingTask.reminder.toString())
            selectedStartTime = existingTask.startTime
            selectedEndTime = existingTask.endTime
            selectedDate = existingTask.date
            selectedStatus = existingTask.status
            binding.dropdownStartTime.setText(DateFormatter.timeFormat.format(existingTask.startTime))
            binding.dropdownEndTime.setText(DateFormatter.timeFormat.format(existingTask.endTime))
            updateTabLayoutStatus(existingTask.status)
        }

    }

    private fun updateTabLayoutStatus(status: TaskStatus) {
        val tabLayout = binding.tlStatus
        when (status) {
            TaskStatus.PENDING -> tabLayout.getTabAt(0)?.select()
            TaskStatus.ON_PROGRESS -> tabLayout.getTabAt(1)?.select()
            TaskStatus.DONE -> tabLayout.getTabAt(2)?.select()
        }
    }


    private fun setupAddTask() {
        viewModel.addTask(dataTask(), selectedFileUri).observe(viewLifecycleOwner) { result ->
            handleApiStatus(result, result.message.toString()) {
                val addedTask = result.data
                showSuccess(
                    requireView(),
                    "Task Added : ${addedTask?.title}"
                )
                findNavController().popBackStack()
            }
        }
    }

    private fun updateTask() {
        viewModel.updateTask(dataTask(), selectedFileUri)
            .observe(viewLifecycleOwner) { result ->
                handleApiStatus(result, result.message.toString()) {
                    val addedTask = result.data
                    showSuccess(
                        requireView(),
                        "Task Updated : ${addedTask?.title}"
                    )
                    findNavController().popBackStack()
                }
            }
    }

    private fun setupCalenderAdapter() {
        val dates = generateDatesForCurrentMonth()
        calenderAdapter = CalenderAdapter().apply {
            submitList(dates)
            callbackOnDateSelected { date ->
                selectedDate = date
            }
            getSelectedDate()?.let { date ->
                selectedDate = date
            }
        }
        binding.rvDate.let {
            it.adapter = calenderAdapter
            it.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupDropdown(
        dropdown: AutoCompleteTextView,
        data: List<String>,
        onItemSelected: (String) -> Unit
    ) {
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_dropdown_item_1line, data)
        dropdown.setAdapter(adapter)

        dropdown.setOnItemClickListener { parent, _, position, _ ->
            val selectedTime = parent.getItemAtPosition(position).toString()
            onItemSelected(selectedTime)
        }
    }

    private fun setupTabLayoutStatus() {
        val tabLayout = binding.tlStatus
        tabLayout.addTab(tabLayout.newTab().setText("Pending"))
        tabLayout.addTab(tabLayout.newTab().setText("Progress"))
        tabLayout.addTab(tabLayout.newTab().setText("Done"))

        for (i in 0 until tabLayout.tabCount) {
            val tab = (tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
            val params = tab.layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(10, 0, 10, 0)
            tab.requestLayout()
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        selectedStatus = TaskStatus.PENDING
                    }

                    1 -> {
                        selectedStatus = TaskStatus.ON_PROGRESS
                    }

                    2 -> {
                        selectedStatus = TaskStatus.DONE
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }


    private fun <T> handleApiStatus(
        result: Result<T>,
        errorMessage: String,
        onSuccess: () -> Unit
    ) {
        when (result.status) {
            Status.LOADING -> {
                showProgressbar(binding.progressBar, true)
            }

            Status.SUCCESS -> {
                showProgressbar(binding.progressBar, false)
                onSuccess.invoke()
            }

            Status.ERROR -> {
                showProgressbar(binding.progressBar, false)
                showErrorMessage(requireView(), errorMessage)
            }
        }
    }

    private fun dataTask(): Task {
        return Task(
            id = "",
            title = binding.edtTitleTask.text.toString(),
            subject = binding.edtSubjectTask.text.toString(),
            description = binding.edtDescriptionTask.text.toString(),
            startTime = selectedStartTime ?: Date(),
            endTime = selectedEndTime ?: Date(),
            date = selectedDate ?: Date(),
            reminder = binding.edtReminderTask.text.toString().toIntOrNull() ?: 0,
            status = selectedStatus,
            createdAt = Date(),
            updatedAt = Date()
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        HandlingUi.setupVisibilityBottomNavigation(activity, false)
        _binding = null
    }

    companion object {
        private const val REQUEST_CODE_PICK_FILE = 1001
        private const val TAG = "Check_add_or_update"
    }

}