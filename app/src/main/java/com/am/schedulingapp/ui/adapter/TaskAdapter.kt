package com.am.schedulingapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.am.schedulingapp.data.model.Task
import com.am.schedulingapp.databinding.ItemContentTaskBinding
import com.am.schedulingapp.utils.DateFormatter

class TaskAdapter(private var onClickToEdit: ((task: Task) -> Unit)? = null) :
    ListAdapter<Task, TaskAdapter.VIewHolder>(DIFF_CALLBACK) {

    fun callbackOnclick(listener: ((task: Task) -> Unit)? = null) {
        onClickToEdit = listener
    }

    inner class VIewHolder(private val binding: ItemContentTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Task) {
            binding.txtTitleTask.text = data.title
            binding.txtSubjectTask.text = data.subject
            binding.txtDescription.text = data.description
            binding.txtStatus.text = DateFormatter.formatDate(data.date)
            binding.root.setOnClickListener { onClickToEdit?.invoke(data) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VIewHolder {
        return VIewHolder(
            ItemContentTaskBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: VIewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Task>() {
            override fun areItemsTheSame(
                oldItem: Task, newItem: Task
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: Task, newItem: Task
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}