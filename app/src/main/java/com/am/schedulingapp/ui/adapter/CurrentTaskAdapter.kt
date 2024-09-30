package com.am.schedulingapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.am.schedulingapp.data.model.Task
import com.am.schedulingapp.databinding.ItemContentCurrentTaskBinding
import com.am.schedulingapp.databinding.ItemContentTaskBinding
import com.am.schedulingapp.utils.DateFormatter

class CurrentTaskAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<Any>()

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_TASK = 1
    }

    fun setData(pending: List<Task>, progress: List<Task>, done: List<Task>) {
        items.clear()
        if (pending.isNotEmpty()) {
            items.add("Tugas pending")
            items.addAll(pending)
        }
        if (progress.isNotEmpty()) {
            items.add("Tugas in progress")
            items.addAll(progress)
        }
        if (done.isNotEmpty()) {
            items.add("Tugas done")
            items.addAll(done)
        }
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is String -> TYPE_HEADER
            is Task -> TYPE_TASK
            else -> throw IllegalArgumentException("Invalid item type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER -> HeaderViewHolder(
                ItemContentCurrentTaskBinding.inflate(inflater, parent, false)
            )

            TYPE_TASK -> TaskViewHolder(
                ItemContentTaskBinding.inflate(inflater, parent, false)
            )

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is String -> (holder as HeaderViewHolder).bind(item)
            is Task -> (holder as TaskViewHolder).bind(item)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class HeaderViewHolder(private val binding: ItemContentCurrentTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(header: String) {
            binding.textStatusTask.text = header
        }
    }

    inner class TaskViewHolder(private val binding: ItemContentTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            with(binding) {
                txtTitleTask.text = task.title
                txtSubjectTask.text = task.subject
                txtDescription.text = task.description
                txtStatus.text = DateFormatter.formatDate(task.date)
                root.setOnClickListener { }
            }
        }
    }
}