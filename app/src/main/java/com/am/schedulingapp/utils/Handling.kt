package com.am.schedulingapp.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.am.schedulingapp.R
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.musfickjamil.snackify.Snackify
import com.razzaghimahdi78.dotsloading.circle.LoadingCircleRotation
import me.ibrahimsn.lib.SmoothBottomBar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object HandlingUi {
    fun setupHideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun setupDisableHintForField(vararg fields: TextInputLayout) {
        fields.forEach { field ->
            field.editText?.setOnFocusChangeListener { _, hasFocus ->
                field.hint = if (hasFocus) null else field.hint
            }
        }
    }

    fun setupClearTextForField(vararg editText: TextInputEditText) {
        for (edt in editText) {
            edt.setText("")
        }
    }

    fun generateTimeList(startHour: Int, endHour: Int, intervalMinutes: Int): List<String> {
        val timeList = mutableListOf<String>()
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("hh:mm", Locale.getDefault())

        for (hour in startHour..endHour) {
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, 0)
            timeList.add(format.format(calendar.time))

            if (intervalMinutes > 0) {
                calendar.add(Calendar.MINUTE, intervalMinutes)
                if (calendar.get(Calendar.HOUR_OF_DAY) <= endHour) {
                    timeList.add(format.format(calendar.time))
                }
            }
        }
        return timeList
    }

    fun setupVisibilityBottomNavigation(activity: Activity?, isGone: Boolean) {
        val bottomNavigation =
            activity?.findViewById<SmoothBottomBar>(R.id.bottomNavigation)
        if (isGone) bottomNavigation?.visibility = View.GONE else bottomNavigation?.visibility =
            View.VISIBLE
    }
}

object ProgressHandling {
    fun showProgressbar(progressBar: LoadingCircleRotation, isVisible: Boolean) {
        progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
}

object NotificationHandling {
    fun showSuccess(view: View, message: String, duration: Int = Snackbar.LENGTH_SHORT) {
        Snackify.success(view, message, duration).show()
    }

    fun showErrorMessage(view: View, message: String, duration: Int = Snackbar.LENGTH_SHORT) {
        Snackify.error(view, message, duration).show()
    }
}
