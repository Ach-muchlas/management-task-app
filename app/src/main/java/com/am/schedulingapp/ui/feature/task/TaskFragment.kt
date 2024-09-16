package com.am.schedulingapp.ui.feature.task

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.am.schedulingapp.data.model.Task
import com.am.schedulingapp.databinding.FragmentTaskBinding
import com.am.schedulingapp.service.source.Status
import com.am.schedulingapp.ui.adapter.CalenderAdapter
import com.am.schedulingapp.ui.adapter.TaskAdapter
import com.am.schedulingapp.utils.DateUtils.generateDatesForCurrentMonth
import com.am.schedulingapp.utils.Destination
import com.am.schedulingapp.utils.Navigation.navigateToFragment
import com.am.schedulingapp.utils.NotificationHandling
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskFragment : Fragment() {
    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TaskViewModel by inject()
    private var selectedDate: Date? = null

    private val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
    private val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskBinding.inflate(inflater, container, false)
        setupNavigation()
        setupCalenderAdapter()
        setupGetData(selectedDate)
        return binding.root
    }

    private fun setupGetData(selectedDate: Date?) {
        viewModel.getTaskByDate(selectedDate!!).observe(viewLifecycleOwner) { result ->
            when (result.status) {
                Status.LOADING -> {}
                Status.SUCCESS -> {
                    setupDataAdapter(result.data)
                    NotificationHandling.showSuccess(requireView(), result.message.toString())
                    Log.e(TAG, " data : ${result.data}")
                }

                Status.ERROR -> {
                    NotificationHandling.showErrorMessage(requireView(), result.message.toString())
                    Log.e(TAG, " data : ${result.message}")
                }
            }
        }
    }

    private fun setupNavigation() {
        binding.fabAddTask.setOnClickListener {
            navigateToFragment(
                Destination.TASK_TO_ADD_OR_UPDATE_TASK, findNavController()
            )
        }
    }

    private fun setupUpdateHeader(selectedDate: Date) {
        val date = dateFormat.format(selectedDate)
        val month = monthFormat.format(selectedDate)
        binding.txtDate.text = buildString {
            append(date)
            append(" ")
            append(month)
        }
    }

    private fun setupDataAdapter(data: List<Task>?) {
        val taskAdapter = TaskAdapter().apply {
            submitList(data)
            callbackOnclick { task ->
                navigateToFragment(
                    Destination.TASK_TO_ADD_OR_UPDATE_TASK, findNavController(), Bundle().apply {
                        putParcelable(
                            KEY_BUNDLE_ID, task
                        )
                    }
                )
            }
        }
        binding.rvTask.let {
            it.adapter = taskAdapter
            it.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupCalenderAdapter() {
        val dates = generateDatesForCurrentMonth()
        val calenderAdapter = CalenderAdapter().apply {
            submitList(dates)
            callbackOnDateSelected { date ->
                selectedDate = date
                setupUpdateHeader(date)
                NotificationHandling.showSuccess(requireView(), "data : $date")
            }
            getSelectedDate()?.let { date ->
                selectedDate = date
                setupUpdateHeader(date)
            }
        }

        binding.rvDate.let {
            it.adapter = calenderAdapter
            it.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val TAG = "Check_Task_Fragment"
        const val KEY_BUNDLE_ID = "key_task"
    }
}