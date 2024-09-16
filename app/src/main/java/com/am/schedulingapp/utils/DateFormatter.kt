package com.am.schedulingapp.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@SuppressLint("ConstantLocale")
object DateFormatter {
    private val dateFormatWithTime = SimpleDateFormat("MMMM d, yyyy 'at' hh:mm:ss a 'UTC'X", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("EEE MMMM yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    private val displayTimeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())


    fun formatDate(date: Date): String = dateFormat.format(date)

    fun parseTime(timeString: String): Date = timeFormat.parse(timeString) ?: Date()

    fun parseDateToTime(serverTime: String): String {
        return try {
            val date = dateFormatWithTime.parse(serverTime)
            date?.let { displayTimeFormat.format(it) } ?: "Invalid time"
        } catch (e: Exception) {
            "Invalid time"
        }
    }

    fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
    }

}