package com.am.schedulingapp.ui.feature.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.am.schedulingapp.databinding.FragmentTaskBinding
import com.am.schedulingapp.ui.adapter.CalenderAdapter
import com.am.schedulingapp.utils.Destination
import com.am.schedulingapp.utils.Navigation.navigateToFragment
import com.am.schedulingapp.utils.NotificationHandling
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TaskFragment : Fragment() {
    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!

    private val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
    private val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskBinding.inflate(inflater, container, false)
        setupNavigation()
        setupCalenderAdapter()
        return binding.root
    }

    private fun setupNavigation() {
        binding.fabAddTask.setOnClickListener {
            navigateToFragment(Destination.TASK_TO_ADD_OR_UPDATE_TASK, findNavController())
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

    private fun setupCalenderAdapter() {
        val dates = generateDatesForCurrentMonth()
        val calenderAdapter = CalenderAdapter().apply {
            submitList(dates)
            callbackOnDateSelected { date ->
                setupUpdateHeader(date)
                NotificationHandling.showSuccess(requireView(), "data : $date")
            }
        }

        calenderAdapter.getSelectedDate()?.let { date ->
            setupUpdateHeader(date)
        }
        binding.rvDate.let {
            it.adapter = calenderAdapter
            it.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }


    private fun generateDatesForCurrentMonth(): List<Date> {
        val dates = mutableListOf<Date>()
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val month = calendar.get(Calendar.MONTH)

        while (calendar.get(Calendar.MONTH) == month) {
            dates.add(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return dates
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}