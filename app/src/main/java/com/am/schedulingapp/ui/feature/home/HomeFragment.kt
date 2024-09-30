package com.am.schedulingapp.ui.feature.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.am.schedulingapp.data.model.Task
import com.am.schedulingapp.data.model.TaskStatus
import com.am.schedulingapp.databinding.FragmentHomeBinding
import com.am.schedulingapp.service.source.Status
import com.am.schedulingapp.ui.adapter.CurrentTaskAdapter
import com.am.schedulingapp.ui.feature.task.TaskViewModel
import com.am.schedulingapp.utils.NotificationHandling
import org.koin.android.ext.android.inject


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TaskViewModel by inject()
    private lateinit var adapter: CurrentTaskAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setupGetDataTaskPending()
    }


    private fun setupGetDataTaskPending() {
        viewModel.getTaskByStatus(TaskStatus.PENDING).observe(viewLifecycleOwner) { result ->
            when (result.status) {
                Status.LOADING -> {}
                Status.SUCCESS -> {
                    viewModel.getTaskByStatus(TaskStatus.ON_PROGRESS)
                        .observe(viewLifecycleOwner) { resultProgress ->
                            when (resultProgress.status) {
                                Status.LOADING -> {}
                                Status.SUCCESS -> {
                                    viewModel.getTaskByStatus(TaskStatus.DONE)
                                        .observe(viewLifecycleOwner) { resultDone ->
                                            when (resultDone.status) {
                                                Status.LOADING -> {}
                                                Status.SUCCESS -> {
                                                    adapter.setData(
                                                        result.data!!,
                                                        resultProgress.data!!,
                                                        resultDone.data!!
                                                    )
                                                }

                                                Status.ERROR -> {
                                                    NotificationHandling.showErrorMessage(
                                                        requireView(),
                                                        result.message.toString()
                                                    )
                                                }
                                            }
                                        }
                                }

                                Status.ERROR -> {
                                    NotificationHandling.showErrorMessage(
                                        requireView(),
                                        result.message.toString()
                                    )
                                }
                            }
                        }

                }

                Status.ERROR -> {
                    NotificationHandling.showErrorMessage(requireView(), result.message.toString())
                }
            }
        }

    }

    private fun setupAdapter() {
        adapter = CurrentTaskAdapter()
        binding.rvCurrentTask.let {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(requireContext())
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}