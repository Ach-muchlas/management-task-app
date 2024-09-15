package com.am.schedulingapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.am.schedulingapp.R
import com.am.schedulingapp.databinding.ItemContentDateBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CalenderAdapter(private var onDateSelected: ((Date) -> Unit)? = null) :
    ListAdapter<Date, CalenderAdapter.ViewHolder>(DiffCallback()) {

    init {
        selectTodayDate()
    }

    fun callbackOnDateSelected(listener: (Date) -> Unit) {
        onDateSelected = listener
    }

    private var selectedPosition = -1

    private fun selectTodayDate() {
        val today = Calendar.getInstance().time
        val todayPosition = currentList.indexOfFirst {
            isSameDay(it, today)
        }
        if (todayPosition != -1) {
            selectDate(todayPosition)
            onDateSelected?.invoke(getItem(todayPosition))
        }
    }

    fun getSelectedDate(): Date? {
        return if (selectedPosition != -1 && selectedPosition < currentList.size) {
            getItem(selectedPosition)
        } else {
            null
        }
    }


    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    inner class ViewHolder(val binding: ItemContentDateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.cardRootCalenderItem.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    selectDate(position)
                    onDateSelected?.invoke(getItem(position))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemContentDateBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val date = getItem(position)
        val formatter = SimpleDateFormat("dd", Locale.getDefault())
        val dayFormatter = SimpleDateFormat("EEE", Locale.getDefault())

        holder.binding.txtNoDate.text = formatter.format(date)
        holder.binding.txtDate.text = dayFormatter.format(date)

        updateDateAppearance(holder, position)
    }

    private fun selectDate(position: Int) {
        val previousSelected = selectedPosition
        selectedPosition = position
        notifyItemChanged(previousSelected)
        notifyItemChanged(selectedPosition)
    }

    private fun updateDateAppearance(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context

        if (position == selectedPosition) {
            // Ubah tampilan ketika dipilih
            holder.binding.cardRootCalenderItem.setCardBackgroundColor(
                ContextCompat.getColor(context, R.color.dark_blue_100)
            )
            holder.binding.txtNoDate.setTextColor(ContextCompat.getColor(context, R.color.white))
            holder.binding.txtDate.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            // Tampilan awal sebelum dipilih
            holder.binding.cardRootCalenderItem.setCardBackgroundColor(
                ContextCompat.getColor(context, R.color.light_sky_blue)
            )
            holder.binding.txtNoDate.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.dark_blue_100
                )
            )
            holder.binding.txtDate.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.dark_blue_100
                )
            )
        }
    }

    override fun submitList(list: List<Date>?) {
        super.submitList(list)
        if (selectedPosition == -1) {
            selectTodayDate()
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Date>() {
        override fun areItemsTheSame(oldItem: Date, newItem: Date): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Date, newItem: Date): Boolean {
            return oldItem == newItem
        }
    }
}