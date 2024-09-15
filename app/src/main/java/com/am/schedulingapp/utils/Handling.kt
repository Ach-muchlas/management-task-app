package com.am.schedulingapp.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.musfickjamil.snackify.Snackify
import com.razzaghimahdi78.dotsloading.circle.LoadingCircleRotation

object HandlingUi {

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
